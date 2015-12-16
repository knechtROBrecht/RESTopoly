package gamesService;

import static spark.Spark.*;
import static spark.SparkBase.port;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import implementation.Game;
import implementation.Player;

@SuppressWarnings("unused")
public class GamesService {

	static List<Game> gameList = new ArrayList<Game>();

	private static Game findGame(String id) {
		for (Game game : gameList) {
			if (game.getID().equals(id))
				return game;
		}

		return null;
	}

	public static void main(String[] args) {
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
			return gson.toJson(gameList);
		});

		/**
		 * Description:
		 * 		creates a new game
		 */
		post("/games", (req, res) -> {
			Game newGame = new Game();
			gameList.add(newGame);
			ServiceApi.newBoard(newGame.getID());

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
			game.addPlayer(player);
			
			ServiceApi.addPlayerToBoard(gameID, playerID);

			return gson.toJson(player);
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
			
			ServiceApi.removePlayerFromBoard(gameID, playerID);

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
			return player.readyUp();
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
	}

}
