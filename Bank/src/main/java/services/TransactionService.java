package services;

import static spark.Spark.*;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;

import implementation.BullyAlgorithm;
import implementation.IO;
import implementation.Player;
import implementation.Transmitter;
import implementation.TwoPhaseCommitProtocol;
import resourceManagment.ResourceManager;
import spark.Response;

/**
 * Our Transaction Service / Coordinator
 * @author foxhound
 *
 */
public class TransactionService {
	
	/*
	 * Info-Messages
	 */
	public static String MESSAGE_PLAYER_IS_EMPTY = "Player in body is empty";
	public static String MESSAGE_CREATE_ACCOUNT = "bank account has been created";
	public static String MESSAGE_PLAYER_EXIST = "player already got a bank account";
	public static String MESSAGE_ACCOUNT_NOT_EXIST = "account not exist to this player id";
	public static String MESSAGE_BANK_NOT_FOUND = "Bank not exist to this game ID";
	public static String MESSAGE_BODY_IS_EMPTY = "body is empty";
	public static String MESSAGE_TRANSACTION_FAIL = "transaction failed";
	private static String MESSAGE_BANK_WAS_OFFLINE = "Request can not be send to this url: ";

	// get the resource manager
	ResourceManager resourceManager = new ResourceManager();
	
	// get http io object
	IO io = new IO();
	
	// gson parser
	Gson gson = new Gson();
	
	// load balance counter
	private static int loadBalance = 0;
	
	// bully component 
	BullyAlgorithm bully;
	
	// port of this service
	private int port = 0;
	
	// here we save the url's from our bank's
	private List<String> bankServiceList = new ArrayList<String>(
			
			Arrays.asList(
						  "http://localhost:4569/banks",
					 	  "http://localhost:4568/banks")			
			);
	
	/**
	 * Constructor
	 * @param id - for the bully algorithm
	 */
	public TransactionService(int id, int port) {
		bully = new BullyAlgorithm(id, getHost(port));
		this.port = port;
		port(port);
	}
	
	/**
	 * Constructor
	 * @param id - for the bully algorithm
	 */
	public TransactionService(int id) {
		bully = new BullyAlgorithm(id, "TODO");
		this.port = 4567;
	}
	
	/**
	 * Service create a account
	 * ein Konto erstellt werden kann mit
	 * post /banks/{gameid}/players
	 */
	synchronized public void startCreatePlayerAccountService() {		
		
		// get the resource to this service
		String currentResource = resourceManager.getTransactionResources().getCreatePlayerAccountResourceService();		
		
		System.out.println("start create player service in TransactionService: " + currentResource);
		
		// bind our service to our resource
		post(currentResource, (req, res) -> {
			
			// initialize the transmitter object for the communication beetwen ts and a bank	
			Transmitter transmitter = new Transmitter
					(
					TwoPhaseCommitProtocol.SERVICE_IDENT_CREATE_PLAYER_ACCOUNT,
					gson.fromJson(req.body(), Player.class),
					req.params(resourceManager.getBankResources().paramGameID),
					"playerID",
					"from",
					"to",
					0
					);	
			return initiatedTwoPhaseCommitProtocol(transmitter, bankServiceList, res, 201, 400);			
		});
	}
	
	/**
	 * call account balance / kontostand abfragen
	 * der Kontostand abgefragt werden kann mit
	 * get /banks/{gameid}/players/{playerid}
	 */
	synchronized public void startCallAccountBalanceService() {
		// get current service resource
		String currentServiceResource = resourceManager.getTransactionResources().getCallAcountBalanceResourceService();
		System.out.println("start call account balance service: " + currentServiceResource);

		// bind current service resource
		get(currentServiceResource, (req, res) -> {
			
			// balance service
			loadBalance ++;
			
			// initialize the transmitter object for the communication beetwen ts and a bank	
			Transmitter transmitter = new Transmitter
					(
					TwoPhaseCommitProtocol.SERVICE_IDENT_CALL_ACCOUNT_BALANCE,
					null,
					req.params(resourceManager.getBankResources().paramGameID),
					req.params(resourceManager.getBankResources().paramPlayerID),
					"from",
					"to",
					0
					);	
			
			if ( loadBalance % 2 == 0 ) {
				// call amount from first bank				
				String bankUrl = bankServiceList.get(0);				
				return initiatedTwoPhaseCommitProtocol(transmitter, new ArrayList<String>(Arrays.asList(bankUrl)), res, 200, 400);
			}
									
			// call amount from second bank
			String bankUrl = bankServiceList.get(1);				
			return initiatedTwoPhaseCommitProtocol(transmitter, new ArrayList<String>(Arrays.asList(bankUrl)), res, 200, 400);
		});
	}
	
	/**
	 * Geld von der Bank überwiesen werden kann mit post
	 * /banks/{gameid}/transfer/to/{to}/{amount}
	 */
	synchronized public void startBankTransferToPlayerService() {
		// get current service resource
		String currentServiceResource = resourceManager.getTransactionResources().getBankTransferToPlayerResourceService();
		
		System.out.println("start bank transfer to player service: " + currentServiceResource);
		
		post(currentServiceResource, (req, res) -> {			
			String reason = req.body();
			
			// precondition
			if (reason.isEmpty()) {
				res.status(400);
				return MESSAGE_BODY_IS_EMPTY;
			}
			
			// initialize the transmitter object for the communication beetwen ts and a bank	
			Transmitter transmitter = new Transmitter
					(
					TwoPhaseCommitProtocol.SERVICE_IDENT_BANK_TRANSFER_MONEY_TO_PLAYER,
					null,
					req.params(resourceManager.getBankResources().paramGameID),
					"playerID",
					"from",
					req.params(resourceManager.getBankResources().paramTo),
					Integer.parseInt(req.params(resourceManager.getBankResources().paramAmount))
					);			
			
			transmitter.setReason(reason);
			return initiatedTwoPhaseCommitProtocol(transmitter, bankServiceList, res, 201, 400);
		});				
	}
	
	/**
	 * Geld eingezogen werden kann mit
	 * post /banks/{gameid}/transfer/from/{from}/{amount}
	 */
	synchronized public void startBankTransferFromPlayerService() {
	
		// get current service resource
		String currentServiceResource = resourceManager.getTransactionResources().getBankTransferFromPlayerResourceService();

		// bind current service resource
		post(currentServiceResource, (req, res) -> {
			
			String reason = req.body();
			
			// precondition
			if (reason.isEmpty()) {
				res.status(400);
				return MESSAGE_BODY_IS_EMPTY;
			}
			
			// initialize the transmitter object for the communication beetwen ts and a bank	
			Transmitter transmitter = new Transmitter
					(
					TwoPhaseCommitProtocol.SERVICE_IDENT_BANK_TRANSFER_MONEY_FROM_PLAYER,
					null,
					req.params(resourceManager.getBankResources().paramGameID),
					"playerID",
					req.params(resourceManager.getBankResources().paramFrom),
					"to",
					Integer.parseInt(req.params(resourceManager.getBankResources().paramAmount))
					);
			
			transmitter.setReason(reason);
			return initiatedTwoPhaseCommitProtocol(transmitter, bankServiceList, res, 201, 400);
		});		
	}
	
	/**
	 * Geld von einem zu anderen Konto übertragen werden kann mit
	 * post /banks/{gameid}/transfer/from/{from}/to/{to}/{amount}	
	 */
	synchronized public void startPlayerTransferToPlayerService() {

		// get current service resource
		String currentServiceResource = resourceManager.getTransactionResources().getPlayerTransferToPlayerResourceService();

		// bind current service resource
		post(currentServiceResource, (req, res) -> {

			String reason = req.body();

			// precondition
			if (reason.isEmpty()) {
				res.status(400);
				return MESSAGE_BODY_IS_EMPTY;
			}
			
			// initialize the transmitter object for the communication beetwen ts and a bank	
			Transmitter transmitter = new Transmitter
					(
					TwoPhaseCommitProtocol.SERVICE_IDENT_PLAYER_TRANSFER_TO_PLAYER,
					null,
					req.params(resourceManager.getBankResources().paramGameID),
					"playerID",
					req.params(resourceManager.getBankResources().paramFrom),
					req.params(resourceManager.getBankResources().paramTo),
					Integer.parseInt(req.params(resourceManager.getBankResources().paramAmount))
					);
			
			transmitter.setReason(reason);
			return initiatedTwoPhaseCommitProtocol(transmitter, bankServiceList, res, 201, 400);
		});
	}
	
	/**
	 * This service is to check, if our transaction service is alive
	 */
	synchronized public void startBullyService() {		
		bully.conintueElection(BullyAlgorithm.RESOURCE_PATH_2);
		
	}
//================================================================================================
//									PRIVATE HELPER METHOD'S		
//================================================================================================		
	/**
	 * full two phase commit protocol implementation for server/TransactionService site
	 * @param res - 
	 * @param body - 
	 * @return String
	 */
	synchronized private String initiatedTwoPhaseCommitProtocol(Transmitter transmitter, List<String> bankServiceList, Response res, int statusSuccess, int statusFailed) {
		
		// send all banks prepare and wait of his answer
		transmitter = startCommitRequestPhase(bankServiceList, transmitter);
		
		if ( transmitter.getOperationIsSuccessful() ) {
			// successful case, all online bank's send ready
			
			// start commit phase
			transmitter = startCommitPhase(bankServiceList, transmitter);
			
			if ( transmitter.getOperationIsSuccessful() ) {
				// close successful the transaction
				
				// return result
				res.status(statusSuccess);
				return transmitter.getResultMessage();
			} else {
				// TODO: commit phase not successful -> process: I dont know
				System.err.println("commit phase not successful");
				res.status(statusFailed);
				return "commit phase not successful";
			}
			
		} else {				
			// a bank send failed -> wee need a roll back
			transmitter = abortPhase(bankServiceList, transmitter);
			
			if ( transmitter.getOperationIsSuccessful() ) {
				// abort phase success, roll back here in close transaction					
				res.status(400);
				return transmitter.getResultMessage();
			} else {					
				// TODO: Abort phase failed ......
				System.err.println("Abort phase failed ......");
				res.status(400);
				return transmitter.getResultMessage();
			}
		}		
	}
	
	/**
	 * This method is the implementation for the ready, commit and about phase
	 * @param urlList
	 * @param transmitter
	 * @param phase
	 * @return Transmitter
	 */
	private Transmitter phaseImplementation(List<String> urlList, Transmitter transmitter, String phase) {
		
		// flag for not failed from a bank service
		boolean bankServiceNotFailed = true;
		
		// send to all bank's prepare 
		for (String url : urlList) {						
			transmitter.setTwoPhaseCommitProtocolIdentifier(phase);			
			String response = io.request(url, gson.toJson(transmitter));
			
			// check if a bank was offline
			if ( isBankServiceOffline(url, response) ) {
				continue;
			}
			
			// convert our response from bank to transmitter object
			transmitter = gson.fromJson(response, Transmitter.class);
			
			// bank send failed
			if ( transmitter.getTwoPhaseCommitProtocolIdentifier().compareTo(TwoPhaseCommitProtocol.FAILED) == 0 ) {
				bankServiceNotFailed = false;
			}
		}
				
		// set operation of successful / true
		transmitter.setOperationIsSuccessful(bankServiceNotFailed);
		return transmitter;
	}
	
	/**
	 * Method initiated the commit-request-phase 1
	 * if all bank's answered with ready, then return the method true, else false
	 * @param urlList - url from all bank services
	 * @return boolean
	 */
	private Transmitter startCommitRequestPhase(List<String> urlList, Transmitter transmitter) {				
		return phaseImplementation(urlList, transmitter, TwoPhaseCommitProtocol.PREPARE);
	}
	
	/**
	 * Method initiated the commit-phase 2
	 * if all bank's answered with acknowledgment, then return the method true, else false
	 * @param urlList - url from all bank services
	 * @return boolean
	 */
	private Transmitter startCommitPhase(List<String> urlList, Transmitter transmitter) {		
		return phaseImplementation(urlList, transmitter, TwoPhaseCommitProtocol.COMMIT);
	}

	/**
	 * Method send on all bank services abort
	 * @param urlLIst
	 * @return
	 */
	private Transmitter abortPhase(List<String> urlList, Transmitter transmitter) {
		return phaseImplementation(urlList, transmitter, TwoPhaseCommitProtocol.ABORT);
	}
	
	/**
	 * Method checked with help from a response result, if a bank offline
	 * @param url - 
	 * @param response - 
	 * @return boolean
	 */
	private boolean isBankServiceOffline(String url, String response) {		
		String offlineMessage = MESSAGE_BANK_WAS_OFFLINE + url;		
		if ( offlineMessage.compareTo(response) == 0 ) {
			return true;
		}		
		return false;
	}
	
	/**
	 * Method get the url from our service
	 * @return String
	 */
	public String getHost() {		
		return getHost(spark.Spark.SPARK_DEFAULT_PORT);
	}		
	
	/**
	 * Method get the url from our service
	 * @param port - port from our service
	 * @return String
	 */
	public String getHost(int port) {
		String protocol = "http://";
		String ip = InetAddress.getLoopbackAddress().getHostAddress();		
		String result = protocol + ip + ":" + port;
		return result;
	}
	
	public static void main(String[] args) {
		// create a object from our transaction service TS-Server / TwoPhaseCommitProtocl
		TransactionService transactionService = new TransactionService(5);
		transactionService.bully.setCoordinatorFlag(true);
		transactionService.bully.setCoordinatorUrl(transactionService.getHost() + BullyAlgorithm.RESOURCE_PATH_2);
		
		/*
		 *  here can a bank register by our transaction service
		 *  starting test service
		 */
		transactionService.startBullyService();
		transactionService.startCreatePlayerAccountService();		
		transactionService.startCallAccountBalanceService();		
		transactionService.startBankTransferToPlayerService();		
		transactionService.startBankTransferFromPlayerService();		
		transactionService.startPlayerTransferToPlayerService();
	}
}
