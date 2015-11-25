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

		// Starts a new Game
		post("/games", (req, res) -> {

			res.type("application/json");
			res.status(201);

			Game newGame = new Game();
			gameList.add(newGame);

			return gson.toJson(newGame);
		});

		// Adds a new player to a existing game
		put("/games/:gameid/players/:playerid", (req, res) -> {
			String gameID = req.params(":gameid");
			String playerID = req.params(":playerid");
			req.queryParams("name");

			Game game = findGame(gameID);

			if (null == game) {
				res.status(404);
				return "Spiel existiert nicht!";
			}

			if (game.getPlayerByID(playerID) != null) {
				res.status(500);
				return "Player existiert bereits";
			}

			Player player = new Player(playerID);
			game.addPlayer(player);

			res.status(200);
			return gson.toJson(player);
		});

		get("/games/:gameid/players/:playerid/ready", (req, res) -> {
			String gameID = req.params(":gameid");
			String playerID = req.params(":playerid");

			Game game = findGame(gameID);
			
			// check if game exist
			if (null == game) {
				res.status(404);
				return "Spiel existiert nicht!";
			}
		
			Player player = game.getPlayerByID(playerID);
			if(null == player)
				res.status(404);
				return "Spieler existiert nicht!";
			}

			res.status(200);
			return player.getReady();	
		});

	// // Changes the readystate of a player
	// put("/games/:gameid/players/:playerid/ready", (req, res) -> {
	// String gameID = req.params(":gameid");
	// String playerID = req.params(":playerid");
	//
	// // get the game as gson from our db
	// String gameGson = DataBase.read(DefaultConfiguration.DB_URL_READ,
	// gameID);
	//
	// // Precondition
	// if (gameGson != null) {
	//
	// // parse json to game object
	// Game game = gson.fromJson(gameGson, Game.class);
	//
	// // get player by id from our game object
	// Player player = game.getPlayerByID(playerID);
	//
	// // precondition
	// if (player != null) {
	//
	// // Toggled den Status
	// player.setReady(!player.getReady());
	//
	// // Mutex freigeben
	// game.setMutex(true);
	//
	// // save modify game object
	// DataBase.write(DefaultConfiguration.DB_URL_WRITE, game);
	//
	// return player.getReady();
	// } else {
	// res.status(404);
	// return "Spieler mit dieser ID existiert nicht!";
	// }
	// } else {
	// res.status(404);
	// return "Spiel existiert nicht!";
	// }
	// });
	//
	// // get the Mutex of the game
	// put("/games/:gameid/turn", (req, res) -> {
	// String gameID = req.params(":gameid");
	//
	// // get the game as gson from our db
	// String gameGson = DataBase.read(DefaultConfiguration.DB_URL_READ,
	// gameID);
	//
	// // Precondition
	// if (gameGson != null) {
	//
	// // parse json to game object
	// Game game = gson.fromJson(gameGson, Game.class);
	//
	// // get mutex from our game object
	// Boolean mutex = game.getMutex();
	//
	// // precondition
	// if (mutex == true) {
	//
	// // Toggled den Status
	// game.setMutex(false);
	//
	// // save modify game object
	// DataBase.write(DefaultConfiguration.DB_URL_WRITE, game);
	//
	// // was returnen? :S
	// res.status(200);
	// return true;
	// } else {
	// res.status(404);
	// return "Ein anderer Spieler ist am Zug!";
	// }
	// } else {
	// res.status(404);
	// return "Spiel existiert nicht!";
	// }
	// });
	//
	// // get status of the Mutex
	// get("/games/:gameid/turn", (req, res) -> {
	// String gameID = req.params(":gameid");
	//
	// // get the game as gson from our db
	// String gameGson = DataBase.read(DefaultConfiguration.DB_URL_READ,
	// gameID);
	//
	// // Precondition
	// if (gameGson != null) {
	// // parse json to game object
	// Game game = gson.fromJson(gameGson, Game.class);
	//
	// // get mutex from our game object
	// Boolean mutex = game.getMutex();
	//
	// if (mutex == true) {
	// res.status(200);
	// return "Kein Spieler hält den Mutex";
	// } else {
	// res.status(200);
	// return "Ein Spieler hält den Mutex";
	// }
	// } else {
	// res.status(404);
	// return "Spiel existiert nicht!";
	// }
	// });
	//
	// // set the Mutex free
	// delete("/games/:gameid/turn", (req, res) -> {
	// String gameID = req.params(":gameid");
	//
	// // get the game as gson from our db
	// String gameGson = DataBase.read(DefaultConfiguration.DB_URL_READ,
	// gameID);
	//
	// // Precondition
	// if (gameGson != null) {
	//
	// // parse json to game object
	// Game game = gson.fromJson(gameGson, Game.class);
	//
	// // get mutex from our game object
	// Boolean mutex = game.getMutex();
	//
	// // precondition
	// if (mutex == false) {
	//
	// // Toggled den Status
	// game.setMutex(true);
	//
	// // save modify game object
	// DataBase.write(DefaultConfiguration.DB_URL_WRITE, game);
	//
	// // was returnen? :S
	// res.status(200);
	// return "Mutex wurde freigegeben!";
	// } else {
	// res.status(404);
	// return "Mutex war schon freigegeben!";
	// }
	// } else {
	// res.status(404);
	// return "Spiel existiert nicht!";
	// }
	// });
	//
	// // get the position of a player
	// get("/games/:gameid/players/:playerid", (req, res) -> {
	// String gameID = req.params(":gameid");
	// String playerID = req.params(":playerid");
	//
	// // get the game as gson from our db
	// String gameGson = DataBase.read(DefaultConfiguration.DB_URL_READ,
	// gameID);
	//
	// // Precondition
	// if (gameGson != null) {
	//
	// // parse json to game object
	// Game game = gson.fromJson(gameGson, Game.class);
	//
	// // get player
	// Player player = game.getPlayerByID(playerID);
	//
	// // precondition
	// if (player != null) {
	//
	// // Position of the player
	// int pos = player.getPosition();
	//
	// res.status(200);
	// return pos;
	// } else {
	// res.status(404);
	// return "Spieler existiert nicht!";
	// }
	// } else {
	// res.status(404);
	// return "Spiel existiert nicht!";
	// }
	// });
	//
	// get("/games/:gameid", (req, res) -> {
	// String gameID = req.params(":gameid");
	//
	// // get the game as gson from our db
	// String gameGson = DataBase.read(DefaultConfiguration.DB_URL_READ,
	// gameID);
	//
	// // Precondition
	// if (gameGson != null) {
	//
	// // parse json to game object
	// Game game = gson.fromJson(gameGson, Game.class);
	//
	// res.status(200);
	// return gson.toJson(game.getStatus());
	// } else {
	// res.status(404);
	// return "Spiel existiert nicht!";
	// }
	// });
}

}
