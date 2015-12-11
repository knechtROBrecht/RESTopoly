import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;

import implementation.Bank;
import implementation.Player;
import services.BankService;

/**
 * Precondtiion, start our BankService
 * @throws UnirestException
 */
public class BankServiceTest {

	Gson gson = new Gson();
		
	static final String URL = "http://localhost:4567";
	//static final String URL = "http://192.168.99.100:4567";
	
	// services
	private static final String CREATE_ACCOUNT = "/banks/0/players";
	private static final String CALL_BALANCE = "/banks/0/players/";
	private static final String BANK_TRANSFER_TO = "/banks/0/transfer/to/";
	private static final String BANK_TRANSFER_FROM = "/banks/0/transfer/from/";
	
	static final int DEFAULT_ACCOUNT_AMOUNT = 4000;
	
	/**
	 * Test create a bank account for a player
	 * @throws UnirestException
	 */
	@Test
	public void createAccountTest() throws UnirestException {		
		String serverResponse = createPlayer("many");		
		assertEquals(serverResponse, BankService.MESSAGE_CREATE_ACCOUNT);
	}
	
	/**
	 * Call a amount from player
	 * @throws UnirestException
	 */
	@Test 
	public void getAmountFromPlayer() throws UnirestException {
		
		String playerName = "peter";
		
		// create player for this test
		createPlayer(playerName);
		
		// call player amount
		int amount = getAmount(playerName);				
		
		// assert with default money
		assertEquals(amount, DEFAULT_ACCOUNT_AMOUNT);		
	}
	
	/**
	 * Bank transfer a amount to a player Test
	 * @throws UnirestException
	 */
	@Test
	public void bankTransferToPlayerTest() throws UnirestException {
		// player name
		String player = "robbie";
		
		// amount which the bank want tranfer our player
		int amount = 70;
		
		// create new player and maybe a new bank
		createPlayer(player);
		
		// save old amound
		int oldAmount = getAmount(player);
		
		// bank push a amount to our player robbie (posts)
		bankTransferTo(player, amount);
		
		// current / new amount from player robbie
		int currentAmount = getAmount(player);
		
		// test
		assertEquals(oldAmount + amount, currentAmount);
	}
	
	/**
	 * This test transfer a amount from a player account to a bank
	 * @throws UnirestException
	 */
	@Test
	public void bankTransferFromTest() throws UnirestException {
		// player name
		String player = "asmael";
		
		// amount which the bank want tranfer our player
		int amount = 70;

		// create new player and maybe a new bank
		createPlayer(player);

		// save old amound
		int oldAmount = getAmount(player);

		// bank push a amount to our player robbie (posts)
		bankTransferFrom(player, amount);

		// current / new amount from player robbie
		int currentAmount = getAmount(player);

		// test
		assertEquals(oldAmount - amount, currentAmount);			
	}
	
	/**
	 * In this test transfer a player a amount to a other play 
	 * @throws UnirestException
	 */
	@Ignore @Test
	public void playerTransferToPlayerTest() throws UnirestException {
		// player names
		String playerFrom = "arlong";
		String playerTo = "luffy";
		
		// define amount 
		int amount = 170;		
					
		// create to players
		createPlayer(playerFrom);
		createPlayer(playerTo);
		
		// get current account amount
		int playerFromOldAmount = getAmount(playerFrom);
		int playerToOldAmount = getAmount(playerTo);
		
		// start transfer from player, to player
		transferFromTo(playerFrom, playerTo, amount);
		
		// get current amounts from players
		int playerFromCurrentAmount = getAmount(playerFrom);
		int playerToCurrentAmount = getAmount(playerTo);
		
		// start assertion
		assertEquals(playerFromCurrentAmount, (playerFromOldAmount - amount));
		assertEquals(playerToCurrentAmount, (playerToOldAmount + amount));
	}
	
//========================================================================
// 							HELPER METHOD'S	
//========================================================================	
	
	/**
	 * Method create a account for a player 
	 * @param playerName
	 * @return
	 * @throws UnirestException
	 */
	private String createPlayer(String playerName) throws UnirestException {
		String player = "{'id':" + playerName + ",'position':0,'ready':false}";

		// request with post and result type is string
		HttpResponse<String> request = Unirest.post(URL + CREATE_ACCOUNT).body(player).asString();

		// server response
		String serverResponse = request.getBody().toString();

		// result
		return serverResponse;
	}
	
	/**
	 * Method get the amount from a player account
	 * @param playerName
	 * @return
	 * @throws UnirestException
	 */
	private int getAmount(String playerName) throws UnirestException {		
		// call player amount
		HttpResponse<String> request = Unirest.get(URL + CALL_BALANCE + playerName).asString();
		String serverResponse = request.getBody().toString();
		return Integer.parseInt(serverResponse);		
	}
	
	/**
	 * Method transfer a amount from a bank to a player
	 * @param playerName - 
	 * @param amount - 
	 * @return String
	 * @throws UnirestException 
	 */
	private String bankTransferTo(String playerName, int amount) throws UnirestException {
		// request with post and result type is string
		HttpResponse<String> request = Unirest.post(URL + BANK_TRANSFER_TO + playerName + "/" + amount).body("bank tranfer to player").asString();

		// server response
		String serverResponse = request.getBody().toString();
		return serverResponse;
	}
	
	
	/**
	 * Method transfer a amount from a player to a bank
	 * @param playerName
	 * @return String
	 * @throws UnirestException 
	 */
	private String bankTransferFrom(String playerName, int amount) throws UnirestException {
		// request with post and result type is string
		HttpResponse<String> request = Unirest.post(URL + BANK_TRANSFER_FROM + playerName + "/" + amount).body("Bank transfer from player").asString();

		// server response
		String serverResponse = request.getBody().toString();
		return serverResponse;
	}
	
	/**
	 * Method does a transfer from a player, to other player
	 * @param playerFrom
	 * @param playerTo
	 * @return String
	 * @throws UnirestException 
	 */
	private String transferFromTo(String playerFrom, String playerTo, int amount) throws UnirestException {		
		// get the full resource -> /banks/:gameid/transfer/from/:from/to/:to/:amount
		String getResource = getTransferFromToResource(playerFrom, playerTo, amount);
		
		// request with post and result type is string
		HttpResponse<String> request = Unirest.post(URL + getResource).body("Rent for Badstrasse").asString();
		
		// server response
		String serverResponse = request.getBody().toString();		
		return serverResponse;
	}
	
	/**
	 * Method return the full resource for transaction from player, to player
	 * @param playerFrom
	 * @param playerTo
	 * @param amount
	 * @return String
	 */
	private String getTransferFromToResource(String playerFrom, String playerTo, int amount) {		
		String resource = BANK_TRANSFER_FROM + playerFrom + "/to/" + playerTo + "/" + amount;
		return resource;
	}

}
