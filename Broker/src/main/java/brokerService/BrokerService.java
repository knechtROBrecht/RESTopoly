package brokerService;

import static spark.Spark.*;
import static spark.SparkBase.port;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;

import implementation.Broker;
import implementation.Estate;
import implementation.Event;
import implementation.Player;

public class BrokerService {

	static Map<String, Broker> brokers = new HashMap<>();
	// Games 0, Boards 1, Dice 2, Bank 3, Events 4, Brokers 5
	static String serviceUri = "https://vs-docker.informatik.haw-hamburg.de/ports/17474/brokers";

	public static void main(String[] args) {
		
		if(args.length > 0){
			port(Integer.valueOf(args[0]));
		}

		Gson gson = new Gson();

		before(("/brokers/:gameid/*"), (req, res) -> {
			if (200 != ServiceApi.checkGame(req.params(":gameid")))
				halt(404, "Spiel existiert nicht!");
		});

		before(("/brokers/:gameid/places/:placeid/*"), (req, res) -> {
			if (!brokers.get(req.params(":gameid")).hasEstate(req.params(":placeid")))
				halt(404, "Place existiert nicht!");
		});

		get("/brokers/:gameid", (req, res) -> {
			String gameID = req.params(":gameid");
			Broker broker = brokers.get(gameID);

			res.status(200);
			res.type("application/json");
			return gson.toJson(broker);
		});

		put("/brokers/:gameid", (req, res) -> {
			String gameID = req.params(":gameid");
			Broker broker = new Broker();
			brokers.put(gameID, broker);

			res.status(201);
			res.type("application/json");
			return gson.toJson(broker);
		});

		get("/brokers/:gameid/places", (req, res) -> {
			res.status(200);
			res.type("application/json");
			// TODO hier die estates zurück? aufgabe steht places aber ein
			// estate hat ja nen place
			return gson.toJson(brokers.get(req.params(":gameid")).getEstates());
		});

		get("/brokers/:gameid/places/:placeid", (req, res) -> {
			res.status(200);
			res.type("application/json");
			return gson.toJson(brokers.get(req.params(":gameid")).getEstate(req.params(":placeid")));
		});

		put("/brokers/:gameid/places/:placeid", (req, res) -> {
			Estate estate = gson.fromJson(req.body(), Estate.class);
			Broker broker = brokers.get(req.params(":gameid"));

			if (estate == null) {
				res.status(400);
				return false;
			}

			if (broker.hasEstate(req.params(":placeid"))) {
				res.status(200);
				// TODO hier egtl mit der uri antworten
				String response = serviceUri + "/" + req.params(":gameid") + "/places/" + req.params(":placeid");
				return gson.toJson(response);
			}

			broker.addEstate(req.params(":placeid"), estate);

			res.status(201);
			res.type("application/json");
			// TODO mit der uri antworten macht das sinn?
			String response = serviceUri + "/" + req.params(":gameid") + "/places/" + req.params(":placeid");
			return gson.toJson(response);
		});

		get("/brokers/:gameid/places/:placeid/owner", (req, res) -> {
			Broker broker = brokers.get(req.params(":gameid"));
			Estate estate = broker.getEstate(req.params(":placeid"));
			return gson.toJson(estate.getOwner());
		});

		put("/brokers/:gameid/places/:placeid/owner", (req, res) -> {
			Broker broker = brokers.get(req.params(":gameid"));
			Estate estate = broker.getEstate(req.params(":placeid"));
			Player player = gson.fromJson(req.body(), Player.class);

			estate.setOwner(player.getId());
			broker.addPlayer(player);

			List<Event> events = new ArrayList<>();
			Event event = new Event("", "owner-changed", "Owner Changed",
					"Owner of Place change to " + req.params(":playerid"),
					broker.getEstate(req.params(":placeid")).getPlace(), player.getId());
			events.add(event);

			return gson.toJson(events);
		});

		post("/brokers/:gameid/places/:placeid/owner", (req, res) -> {
			Broker broker = brokers.get(req.params(":gameid"));
			Estate estate = broker.getEstate(req.params(":placeid"));
			// TODO
			// kommt der Player der das kauft im Body oder woher?

			return gson.toJson(estate);
		});

		put("/brokers/:gameid/places/:placeid/hypothecarycredit ", (req, res) -> {
			Broker broker = brokers.get(req.params(":gameid"));
			Estate estate = broker.getEstate(req.params(":placeid"));
			if (broker.placeHasHypothecaryCredit(req.params(":placeid"))) {
				res.status(400);
				return "Hypothecary credit already acquired.";
			}

			if (transferMoneyFromBank(broker, broker.getPlayer(estate.getOwner()).getId(), estate.getValue(), "")) {
				List<Event> events = new ArrayList<>();
				Event event = new Event("", "got-credit", "Got Credit",
						"Credit was taken for Place " + req.params(":placeid"),
						broker.getEstate(req.params(":placeid")).getPlace(), broker.getPlayer(estate.getOwner()).getId());
				events.add(event);
				postEvent(broker, event);

				return gson.toJson(events);
			}
			return "";
		});

		delete("/brokers/:gameid/places/:placeid/hypothecarycredit ", (req, res) -> {
			Broker broker = brokers.get(req.params(":gameid"));
			List<String> hypothecaryCredits = broker.getHypothecaryCredits();
			for (String place : hypothecaryCredits) {
				if(place.equals(req.params(":placeid"))){
					Estate estate = broker.getEstate(place);
					if(transferMoneyToBank(broker, estate.getOwner(), (int) Math.round(estate.getValue() * 1.1), "")){
						List<Event> events = new ArrayList<>();
						Event event = new Event("", "repayed-credit", "Repayed Credit",
								"Credit was repayed for Place " + req.params(":placeid"),
								broker.getEstate(req.params(":placeid")).getPlace(), broker.getPlayer(estate.getOwner()).getId());
						events.add(event);
						postEvent(broker, event);

						return gson.toJson(events);
					}
				}
			}

			return "";
		});

		post("/brokers/:gameid/places/:placeid/visit/:playerid", (req, res) -> {
			Broker broker = brokers.get(req.params(":gameid"));
			Estate estate = broker.getEstate(req.params(":placeid"));
			// TODO
			if (estate.hasOwner() && !estate.getOwner().equals(req.params(":playerid"))) {
				if (playersTransferMoney(broker, estate.getCurrentRent(), req.params(":playerid"), estate.getOwner(),
						"Rent for " + estate.getPlace())) {
					Player player = broker.getPlayer(req.params(":playerid"));
					Event event = new Event("", "rent-paid", "Rent Paid",
							"Rent was paid from " + req.params(":playerid") + "for Estate " + req.params(":placeid"),
							broker.getEstate(req.params(":placeid")).getPlace(), player.getId());
					event.setUri("");// TODO	

					List<Event> events = new ArrayList<>();
					events.add(event);
					postEvent(broker, event);

					return gson.toJson(events);
				}
			}

			return "";
		});

	}

	private static boolean transferMoneyToBank(Broker broker, String playerid, int amount, String body) {
		HttpRequestWithBody request = null;
		request = Unirest.post(broker.getGame().getComponents().bank + "/transfer/from/{player}/{amount}");

		try {
			HttpResponse<JsonNode> bankResponse = request.routeParam("player", playerid)
					.routeParam("amount", Integer.toString(amount)).body(body).asJson();

			if (bankResponse.getStatus() == 201) {
				return true;
			}
		} catch (UnirestException e) {
			e.printStackTrace();
		}

		return false;
	}

	private static boolean transferMoneyFromBank(Broker broker, String playerid, int amount, String body) {
		HttpRequestWithBody request = null;
		request = Unirest.post(broker.getGame().getComponents().bank + "/transfer/to/{player}/{amount}");

		try {
			HttpResponse<JsonNode> bankResponse = request.routeParam("player", playerid)
					.routeParam("amount", Integer.toString(amount)).body(body).asJson();

			if (bankResponse.getStatus() == 201) {
				return true;
			}
		} catch (UnirestException e) {
			e.printStackTrace();
		}

		return false;
	}

	private static boolean playersTransferMoney(Broker broker, Integer amount, String from, String to, String body) {
		try {
			HttpResponse<JsonNode> bankResponse = Unirest
					.post(broker.getGame().getComponents().bank + "/transfer/from/{from}/to/{to}/{amount}")
					.routeParam("from", from).routeParam("to", to).routeParam("amount", amount.toString()).body(body)
					.asJson();

			if (bankResponse.getStatus() == 201) {
				return true;
			}
		} catch (UnirestException e) {
			e.printStackTrace();
		}

		return false;
	}

	private static Boolean postEvent(Broker broker, Event event) {
		try {
			HttpResponse<JsonNode> response = Unirest.post(broker.getGame().getComponents().events)
					.queryString("gameid", broker.getGame().getGameid()).body(event).asJson();

			if (response.getStatus() == 201) {
				return true;
			}
		} catch (UnirestException e) {
			e.printStackTrace();
		}

		return false;
	}

}
