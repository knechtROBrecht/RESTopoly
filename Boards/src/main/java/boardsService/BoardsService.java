package boardsService;

import static spark.Spark.before;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.post;
import static spark.Spark.put;
import static spark.SparkBase.port;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import implementation.Board;
import implementation.Components;
import implementation.Field;
import implementation.Game;
import implementation.Place;
import implementation.Player;
import implementation.Roll;
import implementation.RollResponse;
import implementation.ServiceDescription;
import implementation.Throw;

/**
 * our Boards service
 * 
 * @author robert.bernhof
 *
 */

public class BoardsService {

	private static Map<String, Board> boards = new HashMap<>();
	// private static Components bc;
	private static String serviceUri = "http://localhost:4561";
	private static String yellowPageUri = "http://vs-docker.informatik.haw-hamburg.de:8053/services";
	private static ServiceDescription service = new ServiceDescription("BoardsRFYD", "Boards Service", "boards", serviceUri);

	private static Board findBoard(String gameID) {
		return boards.get(gameID);
	}

	/**
	 * Service starter
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length > 0) {
			port(Integer.valueOf(args[0]));
		}

		// gson object
		final Gson gson = new Gson();

		before(("/boards/:gameid/*"), (req, res) -> {
			String gameID = req.params(":gameid");

			if (null == findBoard(gameID))
				halt(404, "Board existiert nicht!");
		});

		before(("/boards/:gameid/players/:playerid"), (req, res) -> {
			String gameID = req.params(":gameid");
			Board board = findBoard(gameID);
			board.hasPlayer(req.params(":playerid"));

			if (null == findBoard(gameID))
				halt(404, "Player existiert nicht!");
		});

		/**
		 * Description: returns all active games (both running and joinable)
		 */
		get(("/boards"), (req, res) -> {
			return gson.toJson(boards);
		});

		/**
		 * Description: gets the board belonging to the game
		 */
		get(("/boards/:gameid"), (req, res) -> {
			return gson.toJson(boards.get(req.params(":gameid")));
		});

		/**
		 * Description: makes sure there is a board for the gameid with the
		 * right game settings
		 */
		put(("/boards/:gameid"), (req, res) -> {
			// bc = gson.fromJson(req.body(), Components.class);
				Game game = gson.fromJson(req.body(), Game.class);
				System.out.println(game.getGameid());
				Board board = new Board();
				board.setGame(game);
				board.setUri(serviceUri + "/boards/" + game.getGameid()); 
				boards.put(req.params(":gameid"), board);

				res.header("Location", board.getUri());
				return gson.toJson(board);
			});

		/**
		 * Description: deletes the board to the game, effectivly ending the
		 * game
		 */
		delete(("/boards/:gameid"), (req, res) -> {
			return gson.toJson(boards.remove(req.params(":gameid")));
		});

		/**
		 * Description: returns a list of all player positions
		 */
		get(("/boards/:gameid/players"), (req, res) -> {
			Board board = boards.get(req.params(":gameid"));
			// TODO playerpositions oder player?
				return gson.toJson(board.getPositions());
			});

		/**
		 * Description: Gets a players
		 */
		get(("/boards/:gameid/players/:playerid"), (req, res) -> {
			Board board = boards.get(req.params(":gameid"));
			return gson.toJson(board.getPlayer(req.params(":playerid")));
		});

		/**
		 * Description: places a players
		 */
		put(("/boards/:gameid/players/:playerid"),
				(req, res) -> {
					Board board = boards.get(req.params(":gameid"));
					//Player player = gson.fromJson(req.body(), Player.class);
					Player player = new Player(req.params(":playerid"));
					player.setUri(serviceUri + "/" + req.params(":gameid")
							+ "/" + player.getId());

					if (!board.hasPlayer(player.getId()))
						board.addPlayer(player);

//					board.updatePosition(player, player.getPosition());

					res.header("Location", player.getUri());
					return gson.toJson(player);
				});

		/**
		 * Description: removes a player from the board
		 */
		delete(("/boards/:gameid/players/:playerid"), (req, res) -> {
			Board board = boards.get(req.params(":gameid"));
			return gson.toJson(board.removePlayer(req.params(":playerid")));
		});

		/**
		 * Description: moves a player relative to its current position
		 */
		post(("/boards/:gameid/players/:playerid/move"),
				(req, res) -> {
					Board board = boards.get(req.params(":gameid"));
					Player player = board.getPlayer(req.params(":playerid"));

					board.updatePosition(
							player,
							player.getPosition()
									+ gson.fromJson(req.body(), Integer.class));
					return "";
				});

		/**
		 * Description: gives a throw of dice from the player to the board
		 */
		post("/boards/:gameid/players/:playerid/roll",
				(req, res) -> {
					String gameID = req.params("gameID");
					String playerID = req.params("playerID");

					Throw tr = gson.fromJson(req.body(), Throw.class);

					// Precondition
					if (tr != null) {
						Roll roll1 = tr.roll1;
						Roll roll2 = tr.roll2;

						Board board = findBoard(gameID);
						Player player = board.getPlayer(playerID);

						board.updatePosition(player, player.getPosition()
								+ roll1.getNumber() + roll2.getNumber());

						// return result
						res.type("application/json");
						res.status(200);
						return gson.toJson(new RollResponse(player, board));
					} else {
						res.status(500);
						return "keine Rolls erhalten";
					}
				});

		/**
		 * Description: List of available place
		 */
		get(("/boards/:gameid/places"), (req, res) -> {
			return gson.toJson(boards.get(req.params(":gameid")).getPlaces());
		});

		/**
		 * Description: Gets a places
		 */
		get(("/boards/:gameid/places/:placeid"), (req, res) -> {
			return gson.toJson(boards.get(req.params(":gameid")).getPlace(
					req.params(":placeid")));
		});

		/**
		 * Description: places a places
		 */
		put(("/boards/:gameid/places/:place"),
				(req, res) -> {
					Board board = boards.get(req.params(":gameid"));
					Place place = gson.fromJson(req.body(), Place.class);

					// bestehendes "place" in fields wird umbenannt in neuen
					// namen.
					Field field = new Field(place);
					field.setUri(serviceUri + "/" + req.params(":gameid")
							+ "/places/" + req.params(":place"));
					board.getFields().add(field);

					res.header("Location", field.getUri());
					return gson.toJson(field);
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


}
