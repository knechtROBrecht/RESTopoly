package playerService;

import static spark.Spark.*;
import static spark.SparkBase.port;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.jetty.util.log.Log;

import serviceRequest.Service;
import serviceRequest.Services;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;

import implementation.Components;
import implementation.Event;
import implementation.Game;
import implementation.GameResponse;
import implementation.Player;

public class PlayerService {
	
	private static Player player;
	static Components components;
	
	public static void main(String[] args) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, UnirestException {
		
		SSLContext sslcontext = SSLContexts.custom()
				.loadTrustMaterial(null, new TrustSelfSignedStrategy())
				.build();
				SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext);
				CloseableHttpClient httpclient = HttpClients.custom()
				.setSSLSocketFactory(sslsf)
				.build();
				Unirest.setHttpClient(httpclient);
		
		if(args.length > 0){
			port(Integer.valueOf(args[0]));
		}
		
		// gson object
		final Gson gson = new Gson();
		
		get("/player", (req, res) -> {
			res.status(200);
			return gson.toJson(player);
		});
		
		post("/player/turn", (req, res) -> {
			player.setTurn(true);
			return true;
		});
		
		post("/player/event", (req, res) -> {
			Event[] events = gson.fromJson(req.body().toString(), Event[].class);
			if(events != null){
				for (Event event : events) {
					//hier in GUI zeigen
				}
				return true;
			}
			res.status(404);
			return false;
		});		
		
		//gui starten
		components = new Components();
		
/*		
		Service gameService = Services.getServiceByName("GamesRFYD");
		Service boardService = Services.getServiceByName("BoardsRFYD");
		Service eventService = Services.getServiceByName("EventsRFYD");
		Service diceService = Services.getServiceByName("DiceRFYD");
		Service brokerService = Services.getServiceByName("BrokerRFYD");
		components.games = gameService.uri;
		components.boards = boardService.uri;		
		components.events = eventService.uri;
		components.dice = diceService.uri;
		components.broker = brokerService.uri;	
/*/				
		components.games = "http://localhost:4560";
		components.boards = "http://localhost:4561"; 		
		components.dice = "http://localhost:4562";
		components.events = "http://localhost:4563";
		components.broker = "http://localhost:4564";		
//*/		
		
		
		HttpResponse<JsonNode> response = Unirest
			// .post(gameService.uri)
			.post(components.games + "/games")
			.header("accept", "application/json")
			.header("content-type", "application/json")
			.body(gson.toJson(components)).asJson();
				
		List<Game> gameURIs = getGames();
//		for (Game game : gameURIs) {
//			System.out.println(game.toString());
//		}
		
		Game game = getGame(0);
		String ourUri = addPlayerToGame(game.getID(), "Kevin");
		System.out.println(ourUri);
		Player player = getPlayer(ourUri);
		System.out.println(player);
		
		readyUp(player);
		
	}
	
	private static void readyUp(Player player) throws UnirestException {
		String uri = player.getReady();
		System.out.println(uri);
		HttpResponse<String> response = Unirest
				.put(uri)
				.header("accept", "application/json")
				.header("content-type", "application/json").asString();		
	}

	public static List<Game> getGames() throws UnirestException {
		HttpResponse<JsonNode> response = Unirest
			.get(components.games + "/games")
			.header("accept", "application/json")
			.header("content-type", "application/json").asJson();	
			
		GameResponse gameRes =  new Gson().fromJson(response.getBody().toString(), GameResponse.class);
		
		return gameRes.gameList;
	}
	
	public static String addPlayerToGame(String gameID, String playerName) throws UnirestException {
		HttpResponse<String> response = Unirest
			.put(components.games + "/games/" + gameID + "/players/" + playerName)
			.header("accept", "application/json")
			.header("content-type", "application/json")
			.asString();
		
		return response.getBody().toString();
	}

	public static Player getPlayer(String uri) throws UnirestException {
		System.out.println(uri);
		HttpResponse<String> response = Unirest
			.get(uri)
			.header("accept", "application/json")
			.header("content-type", "application/json")
			.asString();
		return new Gson().fromJson(response.getBody(), Player.class);
	}
	
	
	
	public static Game getGame(int x) throws UnirestException {
		HttpResponse<JsonNode> response = Unirest
			.get(components.games + "/games/" + x)
			.header("accept", "application/json")
			.header("content-type", "application/json").asJson();	
			
		Game game =  new Gson().fromJson(response.getBody().toString(), Game.class);
		
		return game;
	}

	
}
