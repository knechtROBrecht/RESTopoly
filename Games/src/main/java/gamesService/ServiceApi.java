package gamesService;

import implementation.Boards;
import implementation.Components;
import implementation.Game;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import resources.Config;

public class ServiceApi {

	public static String newBoard(Game game, Components components) throws UnirestException{
		
		System.out.println(new Gson().toJson(game).toString());
		
		 HttpResponse<JsonNode> response = Unirest.put(game.getComponents().boards + "/boards/{gameid}")
            .header("accept", "application/json")
            .routeParam("gameid", game.getID())
            .body(new Gson().toJson(game))
            .asJson();
		 
		 
		 Boards board = new Gson().fromJson(response.getBody().toString(), Boards.class);

	     return board.uri;
	}
	
	public static JSONObject newBroker(Game game, Components components) throws UnirestException{
		 HttpResponse<JsonNode> response = Unirest.put(game.getComponents().broker + "/broker/{gameid}")
	                .header("accept", "application/json")
	                .routeParam("gameid", game.getID())
	                .body(new Gson().toJson(components))
	                .asJson();
	        return response.getBody().getObject();
	}
	
	public static JSONObject addPlayerToBoard(Game game, String playerID)throws UnirestException{
		System.out.println(game.getComponents().boards + "/players/{playerid}");
		HttpResponse<JsonNode> response = Unirest.put(game.getComponents().boards + "/players/{playerid}")
				.header("accept","application/json")
				.routeParam("playerid",playerID)
				.asJson();
		return response.getBody().getObject();
	}

	public static JSONObject removePlayerFromBoard(Game game, String playerID) throws UnirestException {
		HttpResponse<JsonNode> response = Unirest.delete(game.getComponents().boards + "/boards/{gameid}/players/{playerid}")
				.header("accept","application/json")
				.routeParam("gameid", game.getID())
				.routeParam("playerid",playerID)
				.asJson();
		return response.getBody().getObject();
	}	
}
