package bankService;

import static spark.Spark.post;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import static spark.Spark.get;
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
	
	public static String MESSAGE_PLAYER_IS_EMPTY = "Player in body is empty";
	public static String MESSAGE_CREATE_ACCOUNT = "bank account has been created";
	public static String MESSAGE_PLAYER_EXIST = "player already got a bank account";
	public static String MESSAGE_ACCOUNT_NOT_EXIST = "account not exist to this player id";
	public static String MESSAGE_BANK_NOT_FOUND = "Bank not exist to this game ID";
	
	/**
	 * Service starter
	 * @param args
	 * @throws UnknownHostException 
	 */
	public static void main(String[] args) throws UnknownHostException {
		
		// gson object 
		Gson gson = new Gson();
		
		int sparkPort = spark.Spark.SPARK_DEFAULT_PORT;
		
		String host = "http://" + InetAddress.getLocalHost() + ":" + sparkPort;
		
		
//========================================================================
		/**
		 * Service create a account
		 * ein Konto erstellt werden kann mit
		 * post /banks/{gameid}/players
		 */
		post("/banks/:gameID/players", (req, res) -> {
			
			// get gameID from client input
			String gameID = req.params("gameID");
			
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
			
			System.out.println(playerAsGson);
			
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
//========================================================================
		
//========================================================================		
		/**
		 * call account balance / kontostand abfragen
		 * der Kontostand abgefragt werden kann mit
		 * get /banks/{gameid}/players/{playerid}
		 */
		get("/banks/:gameID/players/:playerID", (req, res) -> {
			
			// get game id from client input
			String gameID = req.params("gameID");
			
			// get bank to game id
			Bank bank = BankUtil.getBank(gameID);

			// Check if the bank exist to in param gameID
			if (bank == null) {
				res.status(400);
				return MESSAGE_BANK_NOT_FOUND;
			}
			
			// get player id from client input
			String playerID = req.params("playerID");
						
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
		}) ;
//========================================================================
		
//========================================================================
		/**
		 * Geld von der Bank überwiesen werden kann mit post
		 * /banks/{gameid}/transfer/to/{to}/{amount}
		 */
		post("/banks/:gameID/transfer/to/:to/:amount", (req, res) -> {
			
			// get user input value
			String gameID = req.params("gameID");
			
			// player id 
			String playerID = req.params("to");
			
			// amount to tranfer
			int amount = Integer.parseInt(req.params("amount"));
			
			// transaction description
			String reason = req.body();
			
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
			bank.transferPush(playerID, amount, reason);
			
			String resource = "/banks/" + gameID + "/transfer/to/" + account.getPlayer().getID() + "/" + amount;
			
			// create event object
			Event event = new Event("TODO: type"
							 	  , bank.getTransaction().getTo()
							 	  , reason
							 	  , host + resource
							 	  , account.getPlayer());
						
			
			// add event in our bank
			bank.addEvent(event);
			
			res.status(201);		
			return gson.toJson(new ArrayList<Event>(Arrays.asList(event)));
		});
//========================================================================	
		
	
//========================================================================
		/**
		 * Geld eingezogen werden kann mit
		 * post /banks/{gameid}/transfer/from/{from}/{amount}
		 */
		post("/banks/:gameID/transfer/from/:from/:amount", (req, res) -> {
			
			// get user input value
			String gameID = req.params("gameID");
			String playerID = req.params("from");
			int amount = Integer.parseInt(req.params("amount")); 
			
		
			// TODO: 
			return "TODO";
		});
//========================================================================		
		
//========================================================================		
	/**
	 * Geld von einem zu anderen Konto übertragen werden kann mit
	 * post /banks/{gameid}/transfer/from/{from}/to/{to}/{amount}	
	 */
		post("/banks/:gameid/transfer/from/:from/to/:to/:amount", (req, res) -> {
			
			// get user input value
			String gameID = req.params("gameID");
			String playerIDTo = req.params("to");
			String playerIDFrom = req.params("from");
			int amount = Integer.parseInt(req.params("amount")); 
			

			// TODO: 
			return "TODO";
		});
//========================================================================
		
		
	}
}











