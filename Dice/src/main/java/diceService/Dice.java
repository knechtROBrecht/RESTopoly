package diceService;

import static spark.Spark.get;

import java.util.Random;

import com.google.gson.Gson;
import implementation.Roll;

public class Dice {
	public static void main(String[] args) {
		Gson gson = new Gson();
		Random random = new Random();
		
		// Gives a single dice roll
		get("/dice", (req, res) -> {
			res.type("application/json");
			res.status(200);
			return gson.toJson(new Roll(random.nextInt(6) + 1));
		});
	}
}
