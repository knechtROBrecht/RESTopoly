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
import implementation.TwoPhaseCommitProtocol;
import resourceManagment.ResourceManager;
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
	 * Service create a account
	 * ein Konto erstellt werden kann mit
	 * post /banks/{gameid}/players
	 */
	public void startCreatePlayerService() {				
		
		// get the resource to this service
		String currentResource = resourceManager.getBankResources().getCreatePlayerResourceService();
		
		System.out.println("start create player service: " + host);
		
		// bind our service to our resource
		post(currentResource, (req, res) -> {						
			
			// get transmission object (Event-Object) 
			String eventObjectasJson = req.body();
			
			// parese the event object
			Event event = gson.fromJson(eventObjectasJson, Event.class);
			
			// get the transmission command
			String transactionCommand = event.getName();
			
			// first step from our two-commit-protocol
			if ( transactionCommand.compareTo(TwoPhaseCommitProtocol.PREPARE) == 0 ) {
				
				// create a log for this process
				currentLog = new Log();
				
				// get gameID from client input
				String gameID = req.params(resourceManager.getBankResources().paramGameID);
				
				// get bank to game id
				Bank bank = getBank(gameID);
				
				// add new bank, if the bank not exist under client gameID
				if ( bank == null ) {
					bank = new Bank(gameID);
					add(bank);
				}
				
				// log current db object
				currentLog.setUndo(bank);
				
				// parse gson from body to our player object
				Player player = event.getPlayer();									
				
				// create accounts for current player
				boolean successAddAccount = bank.addAccount(player);
				
				// save changes in log
				currentLog.setRedo(bank);				

				// capsulate this part
				if ( successAddAccount ) {
					// Send ready to transaction service back
					return TwoPhaseCommitProtocol.READY;
								
				} else {
					// send Failed to transaction service back					
					return TwoPhaseCommitProtocol.FAILED;
				}
			}
			
			// commit phase
			if ( transactionCommand.compareTo(TwoPhaseCommitProtocol.COMMIT) == 0 ) {
				// clear og 
				currentLog = new Log();
				
				// release resource 
				
				System.out.println("Player create");
				
				// send acknowledgemt to ts back 
				return TwoPhaseCommitProtocol.ACKNOWLEDGMENT;
			}
			
			// abort phase
			if ( transactionCommand.compareTo(TwoPhaseCommitProtocol.ABORT) == 0 ) {
				// do a undo
				undo(bankList, currentLog);				
				
				// clear log
				currentLog = new Log();
				
				// release resource
				
				// send acknowledgemt to ts back 
				return TwoPhaseCommitProtocol.ACKNOWLEDGMENT;
			}
			
			return "TODO: Result";

//			// get gameID from client input
//			String gameID = req.params(resourceManager.getBankResources().paramGameID);
//			
//			// get bank to game id
//			Bank bank = getBank(gameID);
//			
//			// add new bank, if the bank not exist under client gameID
//			if ( bank == null ) {
//				bank = new Bank(gameID);
//				add(bank);
//			}
//			
//			// a player from client
//			String playerAsGson = req.body();
//			
//			// precondition
//			if ( playerAsGson.isEmpty() ) {
//				res.status(400);
//				return MESSAGE_PLAYER_IS_EMPTY;
//			}
//			
//			// parse gson from body to our player object
//			Player player = gson.fromJson(playerAsGson, Player.class);									
//			
//			// create accounts for current player
//			boolean successAddAccount = bank.addAccount(player);
//			
//			// precondtion
//			if ( successAddAccount ) {
//				res.status(201);
//				return MESSAGE_CREATE_ACCOUNT;
//			} else {
//				res.status(409);
//				return MESSAGE_PLAYER_EXIST;
//			}
		});
	}
	
	/**
	 * call account balance / kontostand abfragen
	 * der Kontostand abgefragt werden kann mit
	 * get /banks/{gameid}/players/{playerid}
	 */
	public void startCallAcountBalanceService() {
		
		// get current service resource
		String currentServiceResource = resourceManager.getBankResources().getCallAcountBalanceResourceService();
		
		// bind current service resource
		get(currentServiceResource, (req, res) -> {
			
			// get game id from client input
			String gameID = req.params(resourceManager.getBankResources().paramGameID);
			
			// get bank to game id
			Bank bank = getBank(gameID);

			// Check if the bank exist to in param gameID
			if (bank == null) {
				res.status(400);	
				return MESSAGE_BANK_NOT_FOUND;
			}
			
			// get player id from client input
			String playerID = req.params(resourceManager.getBankResources().paramPlayerID);
						
			// get account object from a player id
			Account account = bank.getAccountBy(playerID);
			
			// precondition			
			if ( account == null ) {
				// define status
				res.status(400);	
				return MESSAGE_ACCOUNT_NOT_EXIST;
			} else {
				// get the account balance of this bank to a player id
				int playerMount = account.getSaldo();
				
				// define status
				res.status(200);		
				
				// return result
				return gson.toJson(playerMount);
			}									
		});
	}
	
	/**
	 * Geld von der Bank überwiesen werden kann mit post
	 * /banks/{gameid}/transfer/to/{to}/{amount}
	 */
	public void startBankTransferToPlayerService() {
		
		// get current service resource
		String currentServiceResource = resourceManager.getBankResources().getBankTransferToPlayerResourceService();
		
		post(currentServiceResource, (req, res) -> {
			
			// get user input value
			String gameID = req.params(resourceManager.getBankResources().paramGameID);
			
			// player id 
			String playerID = req.params(resourceManager.getBankResources().paramTo);
			
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
			
			// get player from id
			Account account = bank.getAccountBy(playerID);
			
			// precondtion: account not exist to this player id
			if ( account == null ) {
				res.status(400);
				return MESSAGE_ACCOUNT_NOT_EXIST;
			}
			
			// transaction			
			boolean transferSuccessful = bank.transferPush(playerID, amount, reason);
			
			// precondition
			if ( !transferSuccessful ) {
				res.status(400);
				return MESSAGE_TRANSACTION_FAIL;
			}
			
			// get resource
			String resource = "/banks/" + gameID + "/transfer/to/" + account.getPlayer().getID() + "/" + amount;
			
			// create event object
			Event event = new Event("TODO: type", bank.getTransaction().getTo(), reason, host + resource, account.getPlayer());
										
			// add event in our bank
			bank.addEvent(event);
			
			res.status(201);		
			return gson.toJson(new ArrayList<Event>(Arrays.asList(event)));			
		});
	}
	
	/**
	 * Geld eingezogen werden kann mit
	 * post /banks/{gameid}/transfer/from/{from}/{amount}
	 */
	public void startBankTransferFromPlayerService() {
		
		// get current service resource
		String currentServiceResource = resourceManager.getBankResources().getBankTransferFromPlayerResourceService();
				
		// bind current service resource
		post(currentServiceResource, (req, res) -> {
			// get user input value
			String gameID = req.params(resourceManager.getBankResources().paramGameID);
			
			// player id 
			String playerID = req.params(resourceManager.getBankResources().paramFrom);
			
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
			
			// get player from id
			Account account = bank.getAccountBy(playerID);
			
			// precondtion: account not exist to this player id
			if ( account == null ) {
				res.status(400);
				return MESSAGE_ACCOUNT_NOT_EXIST;
			}
			
			// transaction			
			boolean transferSuccessful = bank.transferPull(playerID, amount, reason);
			
			// precondition
			if ( !transferSuccessful ) {
				res.status(400);
				return MESSAGE_TRANSACTION_FAIL;
			}
			
			// get resource
			String resource = "/banks/" + gameID + "/transfer/to/" + account.getPlayer().getID() + "/" + amount;
			
			// create event object
			Event event = new Event("TODO: type", bank.getTransaction().getTo(), reason, host + resource, account.getPlayer());
										
			// add event in our bank
			bank.addEvent(event);
			
			res.status(201);		
			return gson.toJson(new ArrayList<Event>(Arrays.asList(event)));
		});
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
// 									PRIVATE HELPER METHOD'S		
//================================================================================================
	/**
	 * Method delete redo bank, and add undo bank in the banklist
	 * @param bankList - 
	 * @param currentLog - undo and redo banks
	 */
	private void undo(List<Bank> bankList, Log currentLog) {
		Bank undoBank = currentLog.getUndo();
		Bank redoBank = currentLog.getRedo();
		delete(redoBank);
		add(undoBank);
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
			if ( bank.getID().compareTo(gameID) == 0) {
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
		bankService.startCreatePlayerService();
		
		/**
		 * call account balance / kontostand abfragen
		 * der Kontostand abgefragt werden kann mit
		 * get /banks/{gameid}/players/{playerid}
		 */
		//bankService.startCallAcountBalanceService();
		
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












