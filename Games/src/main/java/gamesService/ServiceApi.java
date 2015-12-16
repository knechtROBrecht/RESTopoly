package gamesService;

import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import resources.Constants;

public class ServiceApi {

	public static JSONObject newBoard(String gameid) throws UnirestException{
		 HttpResponse<JsonNode> response = Unirest.put(Constants.gamesUri + "/{gameid}")
	                .header("accept", "application/json")
	                .routeParam("gameid", gameid)
	                .asJson();
	        return response.getBody().getObject();
	}
	
	public static JSONObject addPlayerToBoard(String gameid, String playerid)throws UnirestException{
		HttpResponse<JsonNode> response = Unirest.put(Constants.boardUri + "/{gameid}/players/{playerid}")
				.header("accept","application/json")
				.routeParam("gameid", gameid)
				.routeParam("playerid",playerid)
				.asJson();
		return response.getBody().getObject();
	}
}
