package gamesService;

import static spark.Spark.*;
import static spark.SparkBase.port;


import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


import javax.net.ssl.SSLContext;


import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;


import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;


import implementation.Boards;
import implementation.Components;
import implementation.Game;
import implementation.GameResponse;
import implementation.Player;
import implementation.ServiceDescription;

@SuppressWarnings("unused")
public class GamesService {

	static List<Game> gameList = new ArrayList<Game>();
	
	static String serviceUri = "http://localhost:4560";
//*
	private static String yellowPageUri = "http://vs-docker.informatik.haw-hamburg.de:8053/services";
/*/		
	private static String yellowPageUri = "http://vs-docker.informatik.haw-hamburg.de/ports/8053/services";
*/	
	private static ServiceDescription service = new ServiceDescription("GamesRFYD", "Games Service", "games", serviceUri);

	public static void main(String[] args) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		
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
		
		Gson gson = new Gson();

		before(("/games/:gameid/*"), (req, res) -> {
			String gameID = req.params(":gameid");

			if (null == findGame(gameID))
				halt(404, "Spiel existiert nicht!");
		});

		before(("/games/:gameid/players/:playerid/*"), (req, res) -> {
			Game game = findGame(req.params(":gameid"));

			if (null == game.getPlayerByID(req.params(":playerid")))
				halt(404, "Player existiert nicht!");
		});
		
		/**
		 * Description:
		 * 		returns all available games 
		 */
		get("/games", (req, res) -> {
			res.type("application/json");
			GameResponse gR = new GameResponse();
			gR.gameList = gameList;
			return gson.toJson(gR);
		});

		/**
		 * Description:
		 * 		creates a new game
		 */
		post("/games", (req, res) -> {
			Game newGame = new Game();
			newGame.uri = serviceUri + "/" + newGame.getID();
			gameList.add(newGame);
			
			Components components = gson.fromJson(req.body(), Components.class);
			newGame.setComponents(components);
			
			newGame.getComponents().boards = ServiceApi.newBoard(newGame, components);
			
			
//			ServiceApi.newBank();
//			ServiceApi.newBroker(newGame, components);

			res.status(201);
			res.type("application/json");
			return gson.toJson(newGame);
		});
		
		/**
		 * Description:
		 * 		returns the current game status 
		 */
		get("/games/:gameid", (req, res) -> {
			Game game = findGame(req.params(":gameid"));
			return gson.toJson(game);
		});
		
		/**
		 * Description:
		 * 		returns all joined players 
		 */
		get("/games/:gameid/players", (req, res) -> {
			Game game = findGame(req.params(":gameid"));			
			return gson.toJson(game.getPlayersList());
		});
		
		/**
		 * Description:
		 * 		Gets a players
		 */
		get("/games/:gameid/players/:playerid", (req, res) -> {
			Game game = findGame(req.params(":gameid"));			 
			Player player = game.getPlayerByID(req.params(":playerid"));
			player.readyUp(player.getUri() + "/ready");
			return gson.toJson(player);
		});
		
		/**
		 * Description:
		 * 		joins the player to the game 
		 */
		put("/games/:gameid/players/:playerid", (req, res) -> {
			String gameID = req.params(":gameid");
			Game game = findGame(gameID);

			String playerID = req.params(":playerid");

			if (game.doesPlayerExists(playerID)) {
				res.status(500);
				return "Player existiert bereits";
			}

			Player player = new Player(playerID, req.queryParams("name"), req.queryParams("uri"));
			player.setUri(serviceUri + "/games/" + gameID + "/players/" + player.getID());
			game.addPlayer(player);
			
			ServiceApi.addPlayerToBoard(game, playerID);

			return player.getUri();
		});
		
		/**
		 * Description:
		 * 		Removes the player from the game  
		 */
		delete("/games/:gameid/players/:playerid", (req, res) -> {
			String gameID = req.params(":gameid");
			Game game = findGame(gameID);

			String playerID = req.params(":playerid");

			Player player = new Player(playerID, req.queryParams("name"), req.queryParams("uri"));
			game.removePlayer(player);
			
			ServiceApi.removePlayerFromBoard(game, playerID);

			return gson.toJson(player);			
		});
		
		/**
		 * Description:
		 * 		signals that the player is ready to start the game / is finished with his turn
		 */
		put("/games/:gameid/players/:playerid/ready", (req, res) -> {
			Game game = findGame(req.params(":gameid"));
			Player player = game.getPlayerByID(req.params(":playerid"));
			
			game.releaseMutex();
			player.readyUp();
			return true;
		});
		
		/**
		 * Description:
		 * 		tells if the player is ready to start the game 
		 */
		get("/games/:gameid/players/:playerid/ready", (req, res) -> {
			Game game = findGame(req.params(":gameid"));
			Player player = game.getPlayerByID(req.params(":playerid"));
			
			return player.getReady();
		});
		
		/**
		 * Description:
		 * 		gets the currently active player that shall take action 
		 */
		get("/games/:gameid/players/current", (req, res) -> {
			Game game = findGame(req.params(":gameid"));			
			return game.getMutex();
		});

		/**
		 * Description:
		 * 		gets the currently active player that shall take action
		 */
		get("/games/:gameid/players/turn", (req, res) -> {
			Game game = findGame(req.params(":gameid"));

			Player player = game.getMutex();

			if (null == player) {
				res.status(404);
				return "No Player holds the Mutex";
			}
			
			return gson.toJson(player);			
		});
		
		/**
		 * Description:
		 * 		tries to aquire the turn mutex
		 */
		put("/games/:gameid/players/turn", (req, res) -> {
			Game game = findGame(req.params(":gameid"));
			Player player = gson.fromJson(req.body(), Player.class);

			if (game.acquireMutex(player)) {
				res.status(201);
				return "aquired the mutex";
			} else if (game.getMutex().equals(player)) {
				return "already holding the mutex";
			} 

			res.status(409);
			return "already acquired by an other player";
		});

		/**
		 * Description:
		 * 		releases the mutex 
		 */
		delete("/games/:gameid/players/turn", (req, res) -> {
			Game game = findGame(req.params(":gameid"));
			game.releaseMutex();
			return gson.toJson(game);
		});
		
		// register();
	}
	
	public static void register() {
		Gson gson = new Gson();
		try {
			String json = gson.toJson(service);
			System.out.println(json);
			HttpResponse<JsonNode> response = Unirest
					.post(yellowPageUri)
					.header("accept", "application/json")
					.header("content-type", "application/json")
					.body(json).asJson();

			System.out.println(yellowPageUri);
			System.out.println("Status: " + response.getStatus() + " Body:" + response.getBody().toString());
		} catch (UnirestException e) {
			e.printStackTrace();
		}
	}


	private static Game findGame(String id) {
		for (Game game : gameList) {
			if (game.getID().equals(id))
				return game;
		}

		return null;
	}
}
