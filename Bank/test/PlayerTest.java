import static org.junit.Assert.*;

import org.junit.Test;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;

import bankService.BankService;
import implementation.Bank;
import implementation.Player;

/**
 * Precondtiion, start our BankService
 * @throws UnirestException
 */
public class PlayerTest {

	Gson gson = new Gson();
		
	static final String URL = "http://localhost:4567";
	
	// services
	static final String CREATE_ACCOUNT = "/banks/0/players";
	static final String CALL_BALANCE = "/banks/0/players/";
	static final String BANK_TRANSFER_TO = "/banks/0/transfer/to/";
	
	static final int DEFAULT_ACCOUNT_AMOUNT = 4000;
	
	/**
	 * Test create a bank account for a player
	 * @throws UnirestException
	 */
	@Test
	public void createAccountTest() throws UnirestException {		
		String serverResponse = createPlayer("Many");		
		assertEquals(serverResponse, BankService.MESSAGE_CREATE_ACCOUNT);
	}
	
	/**
	 * Call a amount from player
	 * @throws UnirestException
	 */
	@Test 
	public void getAmountFromPlayer() throws UnirestException {
		
		String playerName = "Peter";
		
		// create player for this test
		createPlayer(playerName);
		
		// call player amount
		int amount = getAmount(playerName);				
		
		// assert with default money
		assertEquals(amount, DEFAULT_ACCOUNT_AMOUNT);		
	}
	
	@Test
	public void bankTransferToPlayerTest() {
		// TODO:
	}
	
	/**
	 * Method create a account for a player 
	 * @param playerName
	 * @return
	 * @throws UnirestException
	 */
	private String createPlayer(String playerName) throws UnirestException {
		String player = "{'id':" + playerName + ",'position':0,'ready':false}";

		// request with post and result type is string
		HttpResponse request = Unirest.post(URL + CREATE_ACCOUNT).body(player).asString();

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
		HttpResponse request = Unirest.get(URL + CALL_BALANCE + playerName).asString();
		String serverResponse = request.getBody().toString();
		return Integer.parseInt(serverResponse);		
	}
	
	private String bankTransferTo(Bank bank, String playerName) {
		return "TODO";
	}

}
