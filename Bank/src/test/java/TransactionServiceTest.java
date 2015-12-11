import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import services.BankService;

/**
 * Precondtiion, start our Transmission service and your bank services
 * @throws UnirestException
 */
public class TransactionServiceTest {

	
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
		String serverResponse = createPlayer("peter");
		System.out.println(serverResponse);
		assertEquals(serverResponse, BankService.MESSAGE_CREATE_ACCOUNT);
	}
	

//========================================================================
//		HELPER METHOD'S	
//========================================================================	

	/**
	 * Method create a account for a player
	 * 
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
}
