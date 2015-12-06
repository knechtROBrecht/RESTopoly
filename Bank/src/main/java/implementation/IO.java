package implementation;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class IO {
	
	private static final int STATUS_NOT_FOUND = 404;
	private static final String MESSAGE_NOT_FOUND = "Request can not be send to this url: ";

	/**
	 * Method do a post request on a url 
	 * and become a response back as result
	 * @param url - target
	 * @return String
	 */
	public String request(String url, String data) {
		
		// request with post and result type is string
		HttpResponse<String> request = null;
		
		try {			
			request = Unirest.post(url).body(data).asString();	
			
			if (request.getStatus() == STATUS_NOT_FOUND) {
				return MESSAGE_NOT_FOUND + url;
			}
			
		} catch (UnirestException e) {
			System.err.println(MESSAGE_NOT_FOUND + url);
			return MESSAGE_NOT_FOUND + url;
		}

		// server response
		String serverResponse = request.getBody().toString();
		return serverResponse;
	}
}
