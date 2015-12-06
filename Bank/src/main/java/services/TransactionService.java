package services;

import static spark.Spark.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;

import implementation.Event;
import implementation.IO;
import implementation.Player;
import implementation.TwoPhaseCommitProtocol;
import resourceManagment.ResourceManager;

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

	// get the resource manager
	ResourceManager resourceManager = new ResourceManager();
	
	// get http io object
	IO io = new IO();
	
	// gson parser
	Gson gson = new Gson();
	
	// here we save the url's from our bank's
	private List<String> bankServiceList = new ArrayList<String>(
			
			Arrays.asList(
						  "http://localhost:4569/banks/0/players",
					 	  "http://localhost:4568/banks/0/players")			
			);
	
	
	/**
	 * Service create a account
	 * ein Konto erstellt werden kann mit
	 * post /banks/{gameid}/players
	 */
	synchronized public void startCreatePlayerService() {						
		// get the resource to this service
		String currentResource = resourceManager.getTransactionResources().getCreatePlayerResourceService();		
		
		// bind our service to our resource
		post(currentResource, (req, res) -> {
			
			// send all banks prepare and wait of his answer
			boolean commitRequestPhaseSuccessful = startCommitRequestPhase(bankServiceList, req.body());

			if ( commitRequestPhaseSuccessful) {
				// successful case, all online bank's send ready
				
				// start commit phase
				boolean commitPhaseSuccessful = startCommitPhase(bankServiceList, "empty");
				
				if ( commitPhaseSuccessful ) {
					// close successful the transaction
					
					// return result
					res.status(201);
					return MESSAGE_CREATE_ACCOUNT;
				} else {
					// TODO: commit phase not successful -> process: I dont know
					System.err.println("commit phase not successful");
					res.status(400);
					return "commit phase not successful";
				}
				
			} else {				
				// a bank send failed -> wee need a roll back
				boolean abortPhaseSuccess = abortPhase(bankServiceList, "empty");
				
				if ( abortPhaseSuccess ) {
					// abort phase success, roll back here in close transaction					
					res.status(400);
					return "abort phase success";
				} else {					
					// TODO: Abort phase failed ......
					System.err.println("Abort phase failed ......");
					return "Abort phase failed ......";
				}
			}
		});
	}
	
//================================================================================================
//		PRIVATE HELPER METHOD'S		
//================================================================================================	
	/**
	 * Method initiated the commit-request-phase 1
	 * if all bank's answered with ready, then return the method true, else false
	 * @param urlList - url from all bank services
	 * @return boolean
	 */
	private boolean startCommitRequestPhase(List<String> urlList, String data) {
		
		// flag for not failed from a bank service
		boolean bankServiceNotFailed = true; 
		
		// json player to player Object
		Player player = gson.fromJson(data, Player.class);
		
		// send to all bank's prepare 
		for (String url : urlList) {			
			// generate event object
			String event = generateEvent(TwoPhaseCommitProtocol.PREPARE, player, "whatherver");
			String response = io.request(url, event);
			
			// bank send failed
			if ( response.compareTo(TwoPhaseCommitProtocol.FAILED) == 0 ) {
				bankServiceNotFailed = false;
			}
		}
		return bankServiceNotFailed;
	}
	
	/**
	 * Method initiated the commit-phase 2
	 * if all bank's answered with acknowledgment, then return the method true, else false
	 * @param urlList - url from all bank services
	 * @return boolean
	 */
	private boolean startCommitPhase(List<String> urlList, String data) {
		
		// flag for acknowledgment answer from a bank
		boolean bankAsweredSuccess = true;
		
		Player player = null;
		
		for (String url : urlList) {			
			// generate event object
			String event = generateEvent(TwoPhaseCommitProtocol.COMMIT, player, "whatherver");
			String response = io.request(url, event);
			
			// what happend, if once bank go offline -> then we can not transaction 
			if ( response.compareTo(TwoPhaseCommitProtocol.ACKNOWLEDGMENT) != 0 ) {
				bankAsweredSuccess = false;
			}			
		}		
		return bankAsweredSuccess;
	}

	/**
	 * TODO: IN WORK
	 * Method send on all bank services abort
	 * @param urlLIst
	 * @return
	 */
	private boolean abortPhase(List<String> urlList, String data) {
		
		// flag for acknowledgment answer from a bank
		boolean bankAsweredSuccess = true;

		Player player = null;
		
		for (String url : urlList) {			
			// generate event object
			String event = generateEvent(TwoPhaseCommitProtocol.ABORT, player, "whatherver");
			String response = io.request(url, event);
			
			if ( response.compareTo(TwoPhaseCommitProtocol.ACKNOWLEDGMENT) != 0 ) {
				bankAsweredSuccess = false;
			}			
		}		
		return bankAsweredSuccess;
	}
	
	/**
	 * Method generate a event object and parse him to a json object
	 * @param reason - 
	 * @param player - 
	 * @param resource - 
	 * @return String
	 */
	private String generateEvent(String name, Player player, String resource) {
		Event event = new Event("twoCommitProtocol", name, "", resource, player);
		String result = gson.toJson(event);
		return result;
	}
	
	public static void main(String[] args) {
		
		TransactionService transactionService = new TransactionService();
		
		transactionService.startCreatePlayerService();
		
		// here can a bank register by our transaction service
		
		
		// starting test service
		
		
		
	}
}
