package boardsService;

import static spark.Spark.post;
import static spark.Spark.port;
import com.google.gson.Gson;

import implementation.Roll;
import implementation.Throw;

/**
 * our Boards service
 * 
 * @author robert.bernhof
 *
 */
public class BoardsService {

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

		// ========================================================================
		/**
		 * 
		 * 
		 */
		post("/boards/:gameid/players/:playerid/roll", (req, res) -> {
			// get game id/player id from client input
			String gameID = req.params("gameID");
			String playerID = req.params("playerID");
		
			//*						
			
			Throw tr = gson.fromJson(req.body(), Throw.class);			
			
			/*/
			
			Roll[] rolls = new Roll[2];
			
			rolls[0] = new Roll(13);
			rolls[1] = new Roll(37);
			
			String json = gson.toJson(rolls);
			System.out.println(json);
			
			//*/

			// Precondition
			if (tr != null) {
				Roll roll1 = tr.roll1;
				Roll roll2 = tr.roll2;
				
				// Todo: continue with rolls here..
				
				// return result
				res.type("application/json");
				res.status(200);
				return "rolls richtig übergeben";
			} else {
				res.status(500);
				return "keine Rolls erhalten";
			}
		});
	}

}
