package diceService;

import static spark.Spark.get;

import java.util.Random;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import implementation.Roll;
import implementation.ServiceDescription;

public class Dice {
	
	static String serviceUri = "https://vs-docker.informatik.haw-hamburg.de/ports/17472/dice";
	private static String yellowPageUri = "http://vs-docker.informatik.haw-hamburg.de:8053/services";
	private static ServiceDescription service = new ServiceDescription("DiceRFYD", "Boards Service", "boards", serviceUri);
	
	public static void main(String[] args) {
		Gson gson = new Gson();
		Random random = new Random();
		
		// Gives a single dice roll
		get("/dice", (req, res) -> {
			res.type("application/json");
			res.status(200);
			return gson.toJson(new Roll(random.nextInt(6) + 1));
		});
		
	// 	register();
	}
	
	public static void register() {
		Gson gson = new Gson();
		try {
			HttpResponse<JsonNode> response = Unirest
					.post(yellowPageUri)
					.header("accept", "application/json")
					.header("content-type", "application/json")
					.body(gson.toJson(service)).asJson();
			System.out.println("Status: " + response.getStatus() + " Body:"
					+ response.getBody().toString());
		} catch (UnirestException e) {
			e.printStackTrace();
		}
	}
}
