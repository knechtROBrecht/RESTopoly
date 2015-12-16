package brokerService;

import static spark.Spark.*;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import implementation.Broker;
import implementation.Estate;

public class BrokerService {

	static Map<String, Broker> brokers = new HashMap<>();

	public static void main(String[] args) {

		Gson gson = new Gson();

		before(("/brokers/:gameid/*"), (req, res) -> {
			String gameID = req.params(":gameid");

			if (200 != ServiceApi.checkGame(gameID))
				halt(404, "Spiel existiert nicht!");
		});

		put("/brokers/:gameid", (req, res) -> {
			String gameID = req.params(":gameid");
			Broker broker = new Broker();
			brokers.put(gameID, broker);

			res.status(201);
			res.type("application/json");
			return gson.toJson(broker);
		});

		put("/brokers/:gameid/places/:placeid", (req, res) -> {
			Estate estate = gson.fromJson(req.body(), Estate.class);
			Broker broker = brokers.get(req.params(":gameid"));
			
			if (estate == null) {
				res.status(400);
				return false;
			}
			
			if (broker.hasEstate(req.params(":placeid"))){
				res.status(200);
				//TODO hier egtl mit der uri antworten
				return broker.getEstate(req.params(":placeid"));
			}
			
			broker.addEstate(req.params(":placeid"), estate);
			
			res.status(201);
			res.type("application/json");
			//TODO mit der uri antworten
			return gson.toJson(estate);
		});
		
		put("/brokers/:gameid/places/:placeid/visit/:playerid", (req, res) -> {
			Broker broker = brokers.get(req.params(":gameid"));
			Estate estate = broker.getEstate(req.params(":placeid"));
			//TODO
			
			return gson.toJson(estate);
		});
		
		put("/brokers/:gameid/places/:placeid/owner" , (req, res) -> {
			Broker broker = brokers.get(req.params(":gameid"));
			Estate estate = broker.getEstate(req.params(":placeid"));
			//TODO
			
			return gson.toJson(estate);
		});
		
	}

}
