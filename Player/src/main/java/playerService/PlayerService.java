package playerService;

import static spark.Spark.*;

public class PlayerService {
	
	public static void main(String[] args) {
		
		post("/player/turn", (req, res) -> {
			System.out.println("Du bist dran");
			return "";
		});
		
		post("/player/event", (req, res) -> {
			System.out.println(req.body());
			return "";
		});		
	}
}
