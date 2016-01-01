package boardsService;

import static spark.Spark.post;

import java.util.*;

import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.port;
import com.google.gson.Gson;
import implementation.*;

/**
 * our Boards service
 * 
 * @author robert.bernhof
 *
 */
public class BoardsService {

//	private static Map<String, Board> boards = new HashMap<String, Board>();
//	
//	private static Board findBoard(String gameID) {
//		return boards.get(gameID);
//	}
	
	static List<Board> boardsList = new ArrayList<>();

	private static Board findBoard(String gameID) {
		for (Board board : boardsList) {
			if (board.getGameID() == gameID) {
				return board;
			}
		}

		return null;
	}

	/**
	 * Service starter
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		// gson object
		final Gson gson = new Gson();

		// delete this, is only for testing
		port(4569);
		
		before(("/boards/:gameid/*"), (req, res) -> {
			String gameID = req.params(":gameid");
			
			if (null == findBoard(gameID))
				halt(404, "Board existiert nicht!");
		});
		
		// Retrieves every existing board
		get("/boards" , (req, res) -> {
			List<String> uris = new ArrayList<>();
			
			for(Board b : boardsList) {
				uris.add("/games/" + b.getID());
			}
			
			return gson.toJson(uris);			
		});

		// Creates a new Board
		post("/boards/:gameid" , (req, res) -> {
			String gameID = req.params(":gameid");
			
			Board board = new Board(gameID);
			boardsList.add(board);
			return "/boards/" + board.getID();
		});		
		
		get("/boards/:gameid/players", (req, res) -> {
			String gameID = req.params(":gameid");
			
			Board board = findBoard(gameID);
			
			List<String> uris = new ArrayList<>();
			
			for(Player player : board.getPlayers()) {
				uris.add("/games/" + gameID + "/players/" + player.getId());
			}
			
			return gson.toJson(uris);
		});
		
		/**
		 * 
		 * 
		 */
		post("/boards/:gameid/players/:playerid/roll", (req, res) -> {
			// get game id/player id from client input
			String gameID = req.params("gameID");
			String playerID = req.params("playerID");

			Throw tr = gson.fromJson(req.body(), Throw.class);

			// Precondition
			if (tr != null) {
				Roll roll1 = tr.roll1;
				Roll roll2 = tr.roll2;

				Board board = findBoard(gameID);
				Player player = board.getPlayer(playerID);

				board.updatePosition(player, player.getPosition() + roll1.getNumber() + roll2.getNumber());

				// return result
				res.type("application/json");
				res.status(200);
				//geht das schöner?
				return new RollResponse(player, board);
			} else {
				res.status(500);
				return "keine Rolls erhalten";
			}
		});
	}

}
