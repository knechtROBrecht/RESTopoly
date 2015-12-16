package gamesService;

import static spark.Spark.*;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import implementation.Game;
import implementation.Player;

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

		// Starts a new Game
		post("/games", (req, res) -> {
			Game newGame = new Game();
			gameList.add(newGame);
			ServiceApi.newBoard(newGame.getID());

			res.status(201);
			res.type("application/json");
			return gson.toJson(newGame);
		});

		// Adds a new player to a existing game
		put("/games/:gameid/players/:playerid", (req, res) -> {
			Game game = findGame(req.params(":gameid"));

			String playerID = req.params(":playerid");

			if (game.getPlayerByID(playerID) != null) {
				res.status(500);
				return "Player existiert bereits";
			}

			Player player = new Player(playerID, req.queryParams("name"), req.queryParams("uri"));
			game.addPlayer(player);
			ServiceApi.addPlayerToBoard(req.params(":gameid"), req.params(":playerid"));

			res.status(200);
			return gson.toJson(player);
		});

		get("/games/:gameid/players/:playerid", (req, res) -> {
			Game game = findGame(req.params(":gameid"));

			String playerID = req.params(":playerid");
			Player player = game.getPlayerByID(playerID);

			res.status(200);
			return gson.toJson(player);
		});

		get("/games/:gameid", (req, res) -> {
			Game game = findGame(req.params(":gameid"));

			res.status(200);
			return gson.toJson(game);
		});

		get("/games/:gameid/players/:playerid/ready", (req, res) -> {
			Game game = findGame(req.params(":gameid"));
			Player player = game.getPlayerByID(req.params(":playerid"));

			res.status(200);
			return player.readyUp();
		});

		// get the Mutex of the game
		put("/games/:gameid/players/turn", (req, res) -> {
			Game game = findGame(req.params(":gameid"));
			Player player = gson.fromJson(req.body(), Player.class);

			// precondition
			if (game.acquireMutex(player)) {
				res.status(201);
				return "aquired the mutex";
			} else if (game.getMutex().equals(player)) {
				res.status(200);
				return "already holding the mutex";
			} else {
				res.status(409);
				return "already aquired by an other player";
			}
		});

		get("/games/:gameid/players/turn", (req, res) -> {
			Game game = findGame(req.params(":gameid"));

			Player player = game.getMutex();

			if (player != null) {
				res.status(200);
				return gson.toJson(player);
			} else {
				res.status(404);
				return "Resource could not be found";
			}
		});

		delete("/games/:gameid/players/turn", (req, res) -> {
			Game game = findGame(req.params(":gameid"));
			game.releaseMutex();

			res.status(200);
			// soll hier wirklich kein response kommen?
			return "";
		});

		get("/games/:gameid/players/:playerid/ready", (req, res) -> {
			Game game = findGame(req.params(":gameid"));
			Player player = game.getPlayerByID(req.params(":playerid"));

			res.status(200);
			return player.getReady();
		});

		put("/games/:gameid/players/:playerid/ready", (req, res) -> {
			Game game = findGame(req.params(":gameid"));
			Player player = game.getPlayerByID(req.params(":playerid"));
			game.releaseMutex();

			res.status(200);
			return player.readyUp();
		});
	}

}
