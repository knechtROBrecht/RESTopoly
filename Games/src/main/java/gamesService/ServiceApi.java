package gamesService;

import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import resources.Config;

public class ServiceApi {

	public static JSONObject newBoard(String gameID) throws UnirestException{
		 HttpResponse<JsonNode> response = Unirest.put(Config.getBoardURI("/{gameid}"))
	                .header("accept", "application/json")
	                .routeParam("gameid", gameID)
	                .asJson();
	        return response.getBody().getObject();
	}
	
	public static JSONObject addPlayerToBoard(String gameID, String playerID)throws UnirestException{
		HttpResponse<JsonNode> response = Unirest.put(Config.getBoardURI("/{gameid}/players/{playerid}"))
				.header("accept","application/json")
				.routeParam("gameid", gameID)
				.routeParam("playerid",playerID)
				.asJson();
		return response.getBody().getObject();
	}

	public static JSONObject removePlayerFromBoard(String gameID, String playerID) throws UnirestException {
		HttpResponse<JsonNode> response = Unirest.delete(Config.getBoardURI("/{gameid}/players/{playerid}"))
				.header("accept","application/json")
				.routeParam("gameid", gameID)
				.routeParam("playerid",playerID)
				.asJson();
		return response.getBody().getObject();
	}	
}
