package services;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.SparkBase.port;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;

import implementation.Account;
import implementation.Bank;
import implementation.Event;
import implementation.IO;
import implementation.Log;
import implementation.Player;
import implementation.Transmitter;
import implementation.TwoPhaseCommitProtocol;
import resourceManagment.BankResources;
import resourceManagment.ResourceManager;
import spark.Request;
/**
 * Our Bank service
 * @author foxhound
 */
public class BankService {
	
	// list with all banks in this bank service
	private List<Bank> bankList = new ArrayList<Bank>();
	
	// get the resource manager
	ResourceManager resourceManager = new ResourceManager();
	
	// undo redo log
	private Log currentLog = new Log();
	
	// get http io object
	IO io = new IO();
	
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

	
	/*
	 * Global-Variabls
	 */ 
	Gson gson = new Gson();
	String host = "";
		
	/**
	 * Default Constructor
	 * Saved current host ip
	 */
	public BankService() {		
		this.host = getHost();
	}
	
	/**
	 * Constructor
	 * @param port - port from our service
	 */
	public BankService(int port) {
		port(port);
		this.host = getHost(port);
	}
	
	/**
	 * full two phase commit protocol implementation for client/Bank site
	 * @return
	 */
	synchronized public String twoPhaseCommitProtocol() {
				
		String currentResource = resourceManager.getBankResources().resourceMain;
		
		System.out.println("start bank: " + host + currentResource);
		
		post(currentResource, (req, res) -> {
			
			// get body from request (Event as String)
			String body = req.body();
		
			Transmitter transmitter = gson.fromJson(body, Transmitter.class);
			
			// get the transmission command
			String transactionPhase = transmitter.getPhase();	

			// first step from our two-commit-protocol
			if (transactionPhase.compareTo(TwoPhaseCommitProtocol.PREPARE) == 0) {

				// take service info
				String serviceIdent = transmitter.getServiceIdent();

				/*
				 * all legitim services processor
				 * insert here your services
				 * Code from a specific service
				 */
				switch (serviceIdent) {

				// create a account for a player
				case TwoPhaseCommitProtocol.SERVICE_IDENT_CREATE_PLAYER_ACCOUNT:
					transmitter = createPlayerAccount(transmitter);
					
					// send ready or failed back
					return resultFromFirstStepOfTwoPhaseCommitProtocol(transmitter);
					
				// call acount balance
				case TwoPhaseCommitProtocol.SERVICE_IDENT_CALL_ACCOUNT_BALANCE:
					transmitter = callAccountBalanceService(transmitter);
					
					return resultFromFirstStepOfTwoPhaseCommitProtocol(transmitter);
					
				// do a transfer from the bank to a player account
				case TwoPhaseCommitProtocol.SERVICE_IDENT_BANK_TRANSFER_MONEY_TO_PLAYER:					
					transmitter = doBankTransferToPlayerService(transmitter);
					
					// send ready or failed back
					return resultFromFirstStepOfTwoPhaseCommitProtocol(transmitter);
					
				// bank pull money from a player 	
				case TwoPhaseCommitProtocol.SERVICE_IDENT_BANK_TRANSFER_MONEY_FROM_PLAYER:
					transmitter = doBankTransferFromPlayer(transmitter); 
					
					// send ready or failed back
					return resultFromFirstStepOfTwoPhaseCommitProtocol(transmitter);
				

				// TODO: player transfer money to other player
				}

				
			}

			// commit phase
			if (transactionPhase.compareTo(TwoPhaseCommitProtocol.COMMIT) == 0) {
				return commitPhase(transmitter);
			}

			// abort phase
			if (transactionPhase.compareTo(TwoPhaseCommitProtocol.ABORT) == 0) {
				return abortPhase(transmitter);
			}
			
			return "unexpected case";			
		
		});
		return "unexpected case";		
	}
	
	
	

	/**
	 * Geld von einem zu anderen Konto übertragen werden kann mit
	 * post /banks/{gameid}/transfer/from/{from}/to/{to}/{amount}	
	 */
	public void startPlayerTransferToPlayerService() {
		
		// get current service resource
		String currentServiceResource = resourceManager.getBankResources().getPlayerTransferToPlayerResourceService();

		// bind current service resource		
		post(currentServiceResource, (req, res) -> {
			// get user input value
			String gameID = req.params(resourceManager.getBankResources().paramGameID);
			
			// player id's
			String playerIDFrom = req.params(resourceManager.getBankResources().paramFrom);
			String playerIDTo = req.params(resourceManager.getBankResources().paramTo);			
			
			// amount to tranfer
			int amount = Integer.parseInt(req.params(resourceManager.getBankResources().paramAmount));
			
			// transaction description
			String reason = req.body();
			
			// precondition
			if ( reason.isEmpty() ) {
				res.status(400);
				return MESSAGE_BODY_IS_EMPTY;
			}
			
			// get bank to game id
			Bank bank = getBank(gameID);

			// Check if the bank exist to in param gameID
			if (bank == null) {
				res.status(400);
				return MESSAGE_BANK_NOT_FOUND;
			}
			
			// get players from id
			Account accountFrom = bank.getAccountBy(playerIDFrom);
			Account accountTo = bank.getAccountBy(playerIDTo);
			
			// precondtion: account not exist to this player id
			if ( accountFrom == null || accountTo == null ) {
				res.status(400);
				return MESSAGE_ACCOUNT_NOT_EXIST;
			}
			
			// transaction			
			boolean transferSuccessful = bank.transfer(playerIDFrom, playerIDTo, amount, reason);
			
			// precondition
			if ( !transferSuccessful ) {
				res.status(400);
				return MESSAGE_TRANSACTION_FAIL;
			}
			
			// get resource
			String resource = "/banks/" + gameID + "/transfer/from/" + accountFrom.getPlayer().getID() + "/to" + accountTo.getPlayer().getID() + "/" + amount;
			
			// create event object
			Event event = new Event("TODO: type", bank.getTransaction().getFrom(), reason, host + resource, accountFrom.getPlayer());										
			Event event_2 = new Event("TODO: type", bank.getTransaction().getTo(), reason, host + resource, accountTo.getPlayer());			
			
			// add event in our bank
			bank.addEvent(event);
			bank.addEvent(event_2);
			
			res.status(201);		
			return gson.toJson(new ArrayList<Event>(Arrays.asList(event, event_2)));
		});		
	}

//================================================================================================
// 									PRIVATE SERVICE METHOD'S		
//================================================================================================
	
	/**
	 * Service create a account
	 * ein Konto erstellt werden kann mit
	 * post /banks/{gameid}/players
	 */
	private Transmitter createPlayerAccount(Transmitter transmitter) {				 
		// create a new log
		currentLog = new Log();
		
		// get bank to game id
		Bank bank = getBank(transmitter.getGameID());

		// add new bank, if the bank not exist under client gameID
		if (bank == null) {
			bank = new Bank(transmitter.getGameID());
			add(bank);
		}

		// log current db object
		currentLog.setUndo(bank);

		// create accounts for current player
		boolean successAddAccount = bank.addAccount(transmitter.getPlayer());

		// save changes in log
		currentLog.setRedo(bank);

		// return true by success create from a player account
		if (successAddAccount) {
			transmitter.setOperationIsSuccessful(true);
			transmitter.setResultMessage(MESSAGE_CREATE_ACCOUNT);
			return transmitter;
		}
		
		transmitter.setOperationIsSuccessful(false);
		return transmitter;
	}
	
	/**
	 * call account balance / kontostand abfragen
	 * der Kontostand abgefragt werden kann mit
	 * get /banks/{gameid}/players/{playerid}
	 */
	private Transmitter callAccountBalanceService(Transmitter transmitter) {
			
		// get game id from client input
		String gameID = transmitter.getGameID();

		// get bank to game id
		Bank bank = getBank(gameID);

		// Check if the bank exist to in param gameID
		if (bank == null) {
			transmitter.setResultMessage(MESSAGE_BANK_NOT_FOUND);
			transmitter.setOperationIsSuccessful(false);
			return transmitter;
		}

		// get player id from client input
		String playerID = transmitter.getPlayerID();

		// get account object from a player id
		Account account = bank.getAccountBy(playerID);

		// precondition
		if (account == null) {
			transmitter.setResultMessage(MESSAGE_ACCOUNT_NOT_EXIST);
			transmitter.setOperationIsSuccessful(false);
		} else {
			// delegate account saldo
			transmitter.setResultMessage(gson.toJson(account.getSaldo()));
			transmitter.setOperationIsSuccessful(true);
		}						
		return transmitter;
	}
	
	/**
	 * Geld von der Bank überwiesen werden kann mit post
	 * /banks/{gameid}/transfer/to/{to}/{amount}
	 */
	private Transmitter doBankTransferToPlayerService(Transmitter transmitter) {
			// get bank to game id
			Bank bank = getBank(transmitter.getGameID());

			// Check if the bank exist to in param gameID
			if (bank == null) {
				transmitter.setOperationIsSuccessful(false);
				transmitter.setResultMessage(MESSAGE_BANK_NOT_FOUND);
				return transmitter;
			}
			
			// create a new log
			currentLog = new Log();
			
			// save current state
			currentLog.setUndo(bank);
			
			// get player from id
			Account account = bank.getAccountBy(transmitter.getTo());
			
			// precondtion: account not exist to this player id
			if ( account == null ) {
				transmitter.setOperationIsSuccessful(false);
				transmitter.setResultMessage(MESSAGE_ACCOUNT_NOT_EXIST);
				return transmitter;
			}
			
			// transaction			
			boolean transferSuccessful = bank.transferPush(transmitter.getTo(), transmitter.getAmount(), transmitter.getReason());
			
			// precondition
			if ( !transferSuccessful ) {
				transmitter.setOperationIsSuccessful(false);
				transmitter.setResultMessage(MESSAGE_TRANSACTION_FAIL);
				return transmitter;
			}
			
			BankResources b = resourceManager.getBankResources();
			
			// get resource
			String resource = b.resourceMain + transmitter.getGameID() + b.resourceTransfer + b.resourceTo + "/" + account.getPlayer().getID() + transmitter.getAmount(); 
			
			// create event object
			Event event = new Event("TODO: type", bank.getTransaction().getTo(), transmitter.getReason(), host + resource, account.getPlayer());
										
			// add event in our bank
			bank.addEvent(event);
			
			// save modify bank in redo 
			currentLog.setRedo(bank);
			
			// result
			transmitter.setResultMessage(gson.toJson(new ArrayList<Event>(Arrays.asList(event))));
			transmitter.setOperationIsSuccessful(true);
			return transmitter;
	}
	
	/**
	 * Geld eingezogen werden kann mit
	 * post /banks/{gameid}/transfer/from/{from}/{amount}
	 */
	private Transmitter doBankTransferFromPlayer(Transmitter transmitter) {
		// get user input value
		String gameID = transmitter.getGameID();

		// player id
		String playerID = transmitter.getFrom();

		// amount to tranfer
		int amount = transmitter.getAmount();

		// transaction description
		String reason = transmitter.getReason();

		// get bank to game id
		Bank bank = getBank(gameID);

		// Check if the bank exist to in param gameID
		if (bank == null) {
			transmitter.setOperationIsSuccessful(false);
			transmitter.setResultMessage(MESSAGE_BANK_NOT_FOUND);
			return transmitter;
		}

		// get player from id
		Account account = bank.getAccountBy(playerID);

		// precondtion: account not exist to this player id
		if (account == null) {
			transmitter.setOperationIsSuccessful(false);
			transmitter.setResultMessage(MESSAGE_ACCOUNT_NOT_EXIST);
			return transmitter;
		}

		// transaction
		boolean transferSuccessful = bank.transferPull(playerID, amount, reason);

		// precondition
		if (!transferSuccessful) {
			transmitter.setOperationIsSuccessful(false);
			transmitter.setResultMessage(MESSAGE_TRANSACTION_FAIL);
			return transmitter;
		}

		// get resource
		String resource = "/banks/" + gameID + "/transfer/to/" + account.getPlayer().getID() + "/" + amount;

		// create event object
		Event event = new Event("TODO: type", bank.getTransaction().getTo(), reason, host + resource, account.getPlayer());

		// add event in our bank
		bank.addEvent(event);

		transmitter.setOperationIsSuccessful(true);
		transmitter.setResultMessage(gson.toJson(new ArrayList<Event>(Arrays.asList(event))));
		return transmitter;
	}
	
//================================================================================================
//		PRIVATE HELPER METHOD'S		
//================================================================================================	
	/**
	 * Method return the transmitter as json format
	 * @param operationSuccess - a service process
	 * @return String
	 */
	private String resultFromFirstStepOfTwoPhaseCommitProtocol(Transmitter transmitter ) {
		if ( transmitter.getOperationIsSuccessful() ) {
			// Send ready to transaction service back
			transmitter.setTwoPhaseCommitProtocolIdentifier(TwoPhaseCommitProtocol.READY);
		} else {
			// send Failed to transaction service back
			transmitter.setTwoPhaseCommitProtocolIdentifier(TwoPhaseCommitProtocol.FAILED);
		}									
		String transmitterAsJson = gson.toJson(transmitter);
		return transmitterAsJson;
	}
	
	/**
	 * Method create a new log, and return a acknowledgment in a transmitter
	 * @param info - system out consol info
	 * @return String
	 */
	private String commitPhase(Transmitter transmitter) {
		// clear og 
		currentLog = new Log();
		
		// TODO: release resource 
		
		// set the protocl answer for our server
		transmitter.setTwoPhaseCommitProtocolIdentifier(TwoPhaseCommitProtocol.ACKNOWLEDGMENT);
		
		// convert transmitter-object to json string
		String transmitterAsJson = gson.toJson(transmitter);
		
		// send acknowledgemt to ts back 
		return transmitterAsJson;
	}
	
	/**
	 * Method do a undo, cleared the current log and return a acknowledgment
	 * @return String 
	 */
	private String abortPhase(Transmitter transmitter) {
		// do a undo
		boolean successfulUndo = undo(bankList, currentLog);
		transmitter.setOperationIsSuccessful(successfulUndo);
		
		// clear log
		currentLog = new Log();
		
		// TODO: release resource
		
		// convert transmitter-object to json string
		String transmitterAsJson = gson.toJson(transmitter);
				
		// send acknowledgemt to ts back 
		return transmitterAsJson;
	}
	
	/**
	 * Method delete redo bank, and add undo bank in the banklist
	 * @param bankList - 
	 * @param currentLog - undo and redo banks
	 */
	private boolean undo(List<Bank> bankList, Log currentLog) {
		Bank undoBank = currentLog.getUndo();
		Bank redoBank = currentLog.getRedo();
		boolean successfulDelete = delete(redoBank);
		
		if ( successfulDelete ) {
			return add(undoBank);
		}
		return false;
	}
	
	/**
	 * Method get the url from our service
	 * @return String
	 */
	private String getHost() {		
		return getHost(spark.Spark.SPARK_DEFAULT_PORT);
	}		
	
	/**
	 * Method get the url from our service
	 * @param port - port from our service
	 * @return String
	 */
	private String getHost(int port) {
		String protocol = "http://";
		String ip = InetAddress.getLoopbackAddress().getHostAddress();		
		String result = protocol + ip + ":" + port;
		return result;
	}
	
	/**
	 * Method add a bank in our bankList
	 * @param bank - 
	 * @return boolean
	 */
	private boolean add(Bank bank) {
		return bankList.add(bank);
	}
	
	/**
	 * Method delete a bank from our banklist
	 * @param bank - 
	 * @return boolean
	 */
	private boolean delete(Bank bank) {
		return bankList.remove(bank);
	}
	
	/**
	 * Method get a bank by gameID or null if the bank not exist
	 * @param gameID - a game ID
	 * @return Bank v null
	 */
	private Bank getBank(String gameID) {		
		for (Bank bank : bankList) {
			if ( bank.getID().compareTo(gameID) == 0 ) {
				return bank;
			}
		}
		return null;
	}
	
	/**
	 * Service starter
	 * @param args
	 * @throws UnknownHostException 
	 */
	public static void main(String[] args) throws UnknownHostException {

		// create a bank service object
		BankService bankService = new BankService(4568);

		/**
		 * Service create a account
		 * ein Konto erstellt werden kann mit
		 * post /banks/{gameid}/players
		 */
		//bankService.startCreatePlayerAccountService();
		bankService.twoPhaseCommitProtocol();
		
		/**
		 * call account balance / kontostand abfragen
		 * der Kontostand abgefragt werden kann mit
		 * get /banks/{gameid}/players/{playerid}
		 */
		//bankService.callAccountBalanceService();
		
		/**
		 * Geld von der Bank überwiesen werden kann mit post
		 * /banks/{gameid}/transfer/to/{to}/{amount}
		 */
		//bankService.startBankTransferToPlayerService();

		/**
		 * Geld eingezogen werden kann mit
		 * post /banks/{gameid}/transfer/from/{from}/{amount}
		 */
		//bankService.startBankTransferFromPlayerService();

		/**
		 * Geld von einem zu anderen Konto übertragen werden kann mit post
		 * /banks/{gameid}/transfer/from/{from}/to/{to}/{amount}
		 */
		//bankService.startPlayerTransferToPlayerService();		
	}
}












