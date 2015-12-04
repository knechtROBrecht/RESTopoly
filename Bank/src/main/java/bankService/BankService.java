package bankService;

import static spark.Spark.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import com.google.gson.Gson;

import implementation.Account;
import implementation.Bank;
import implementation.BankUtil;
import implementation.Event;
import implementation.Player;
/**
 * Our Bank service
 * @author foxhound
 */
public class BankService {
	
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
	 * Resources
	 */
	private String resourceMain = "/banks";
	private String resourcePlayer = "/players";
	private String resourceTransfer = "/transfer";
	private String resourceTo = "/to";
	private String resourceFrom = "/from";
	
	private String resourceParamGameID = "/:gameID";
	private String resourceParamTo = "/:to";
	private String resourceParamFrom = "/:from";
	private String resourceParamAmount = "/:amount";
	private String resourceParamPlayer = "/:playerID";
	
	/*
	 * Param to resources
	 */
	private String paramGameID = resourceParamGameID.replace("/:", "");
	private String paramPlayerID = resourceParamPlayer.replace("/:", "");
	private String paramAmount = resourceParamAmount.replace("/:", "");
	private String paramTo = resourceParamTo.replace("/:", "");
	private String paramFrom = resourceParamFrom.replace("/:", "");
	
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
		String createPlayerResource= getCreatePlayerResourceService();
		
		// bind our service to our resource
		post(createPlayerResource, (req, res) -> {
			
			// get gameID from client input
			String gameID = req.params(paramGameID);
			
			// get bank to game id
			Bank bank = BankUtil.getBank(gameID);
			
			// add new bank, if the bank not exist under client gameID
			if ( bank == null ) {
				bank = new Bank(gameID);
				BankUtil.add(bank);
			}
			
			// a player from client
			String playerAsGson = req.body();
			
			// precondition
			if ( playerAsGson.isEmpty() ) {
				res.status(400);
				return MESSAGE_PLAYER_IS_EMPTY;
			}
			
			// parse gson from body to our player object
			Player player = gson.fromJson(playerAsGson, Player.class);									
			
			// create accounts for current player
			boolean successAddAccount = bank.addAccount(player);
			
			// precondtion
			if ( successAddAccount ) {
				res.status(201);
				return MESSAGE_CREATE_ACCOUNT;
			} else {
				res.status(409);
				return MESSAGE_PLAYER_EXIST;
			}
		});
	}
	
	/**
	 * call account balance / kontostand abfragen
	 * der Kontostand abgefragt werden kann mit
	 * get /banks/{gameid}/players/{playerid}
	 */
	public void startCallAcountBalanceService() {
		
		// get current service resource
		String currentServiceResource = getCallAcountBalanceResourceService();
		
		// bind current service resource
		get(currentServiceResource, (req, res) -> {
			
			// get game id from client input
			String gameID = req.params(paramGameID);
			
			// get bank to game id
			Bank bank = BankUtil.getBank(gameID);

			// Check if the bank exist to in param gameID
			if (bank == null) {
				res.status(400);	
				return MESSAGE_BANK_NOT_FOUND;
			}
			
			// get player id from client input
			String playerID = req.params(paramPlayerID);
						
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
		String currentServiceResource = getBankTransferToPlayerResourceService();
		
		post(currentServiceResource, (req, res) -> {
			
			// get user input value
			String gameID = req.params(paramGameID);
			
			// player id 
			String playerID = req.params(paramTo);
			
			// amount to tranfer
			int amount = Integer.parseInt(req.params(paramAmount));
			
			// transaction description
			String reason = req.body();
			
			// precondition
			if ( reason.isEmpty() ) {
				res.status(400);
				return MESSAGE_BODY_IS_EMPTY;
			}
			
			// get bank to game id
			Bank bank = BankUtil.getBank(gameID);

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
		String currentServiceResource = getBankTransferFromPlayerResourceService();
				
		// bind current service resource
		post(currentServiceResource, (req, res) -> {
			// get user input value
			String gameID = req.params(paramGameID);
			
			// player id 
			String playerID = req.params(paramFrom);
			
			// amount to tranfer
			int amount = Integer.parseInt(req.params(paramAmount));
			
			// transaction description
			String reason = req.body();
			
			// precondition
			if ( reason.isEmpty() ) {
				res.status(400);
				return MESSAGE_BODY_IS_EMPTY;
			}
			
			// get bank to game id
			Bank bank = BankUtil.getBank(gameID);

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
		String currentServiceResource = getPlayerTransferToPlayerResourceService();

		// bind current service resource		
		post(currentServiceResource, (req, res) -> {
			// get user input value
			String gameID = req.params(paramGameID);
			
			// player id's
			String playerIDFrom = req.params(paramFrom);
			String playerIDTo = req.params(paramTo);			
			
			// amount to tranfer
			int amount = Integer.parseInt(req.params(paramAmount));
			
			// transaction description
			String reason = req.body();
			
			// precondition
			if ( reason.isEmpty() ) {
				res.status(400);
				return MESSAGE_BODY_IS_EMPTY;
			}
			
			// get bank to game id
			Bank bank = BankUtil.getBank(gameID);

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
	 * Method get the playerResource service url, without http://ip:port
	 * @return String
	 */
	private String getCreatePlayerResourceService() {		
		return resourceMain + resourceParamGameID + resourcePlayer;
	}
	
	/**
	 * Method get the call account balance service url, without http://ip:port
	 * @return String
	 */
	private String getCallAcountBalanceResourceService() {
		return resourceMain + resourceParamGameID + resourcePlayer + resourceParamPlayer;
	}

	/**
	 * Method get the bank transfer to player service url, without http://ip:port
	 * @return String
	 */
	private String getBankTransferToPlayerResourceService() {
		return resourceMain + resourceParamGameID + resourceTransfer + resourceTo + resourceParamTo + resourceParamAmount;
	}

	/**
	 * Method get the bank transfer from player service url, without http://ip:port
	 * @return String
	 */
	private String getBankTransferFromPlayerResourceService() {
		// post /banks/{gameid}/transfer/from/{from}/{amount}
		return resourceMain + resourceParamGameID + resourceTransfer + resourceFrom + resourceParamFrom + resourceParamAmount;
	}
	
	/**
	 * Method get the player transfer to player service url, without http://ip:port
	 * @return String
	 */
	private String getPlayerTransferToPlayerResourceService() {
		return resourceMain + resourceParamGameID + resourceTransfer + resourceFrom + resourceParamFrom + 
				resourceTo + resourceParamTo + resourceParamAmount;
	}
	
	/**
	 * Service starter
	 * @param args
	 * @throws UnknownHostException 
	 */
	public static void main(String[] args) throws UnknownHostException {
		
		// create a bank service object
		BankService bankService = new BankService();

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
		bankService.startCallAcountBalanceService();
		
		/**
		 * Geld von der Bank überwiesen werden kann mit post
		 * /banks/{gameid}/transfer/to/{to}/{amount}
		 */
		bankService.startBankTransferToPlayerService();

		/**
		 * Geld eingezogen werden kann mit
		 * post /banks/{gameid}/transfer/from/{from}/{amount}
		 */
		bankService.startBankTransferFromPlayerService();

		/**
		 * Geld von einem zu anderen Konto übertragen werden kann mit post
		 * /banks/{gameid}/transfer/from/{from}/to/{to}/{amount}
		 */
		bankService.startPlayerTransferToPlayerService();
		
	}
}












