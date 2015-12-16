package brokerService;

import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import resources.Constants;

public class ServiceApi {
	
	public static int checkGame(String gameid) throws UnirestException{
		 HttpResponse<JsonNode> response = Unirest.get(Constants.gamesUri + "/{gameid}")
	                .header("accept", "application/json")
	                .routeParam("gameid", gameid)
	                .asJson();
	        return response.getStatus();
	}

}
