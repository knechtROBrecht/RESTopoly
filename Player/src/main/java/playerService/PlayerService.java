package playerService;

import static spark.Spark.*;

public class PlayerService {
	public static void main(String[] args) {
		post("/player/turn", (req, res) -> {
			return "NOT IMPLEMENTED YET";
		});
		
		post("/player/event", (req, res) -> {
			return "NOT IMPLEMENTED YET";
		});		
	}
}
