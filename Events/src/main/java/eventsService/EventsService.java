package eventsService;

import static spark.Spark.*;
import static spark.SparkBase.port;
import implementation.Event;
import implementation.ServiceDescription;
import implementation.Subscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class EventsService {
	
	private static String serviceUri = "https://vs-docker.informatik.haw-hamburg.de/ports/17473/event";
	private static String yellowPageUri = "http://vs-docker.informatik.haw-hamburg.de:8053/services";
	private static ServiceDescription service = new ServiceDescription("EventsRFYD", "Events Service", "events", serviceUri);

	
	private static Map<String, List<Event>> events = new HashMap<String, List<Event>>();
	private static List<Subscription> subscriptions = new ArrayList<>();
	
	public static void main(String[] args) {
		
		if(args.length > 0){
			port(Integer.valueOf(args[0]));
		}
		
		Gson gson = new Gson();
		
		get("/events", (req, res) -> {
			String gameID = req.queryParams("gameid");
			if(gameID != null){
				List<Event> GameEvents = events.get(gameID);
				if(events == null){
					res.status(404);
					return null;
				}else{
					res.status(200);
					return gson.toJson(GameEvents);
				}
			}
			res.status(404);
			return null;
		});		
		
		post("/events", (req, res) -> {
			String gameID = req.queryParams("gameid");
			if(gameID != null){
				Event event = gson.fromJson(req.body(), Event.class);
				if(event == null){
					res.status(404);
					return null;
				}else{
					event.setId(UUID.randomUUID().toString());
					event.setUri(serviceUri + "/" + event.getId());
					addEvent(gameID, event);
					
					res.status(201);
					res.header("Location", event.getUri());
					return gson.toJson(event);
				}
			}
			res.status(404);
			return null;
		});
		
		delete("/events", (req, res) -> {
			String gameID = req.queryParams("gameid");
			if(gameID != null){
				events.put(gameID, new ArrayList<Event>());
				res.status(200);
				return true;
			}
			res.status(404);
			return null;
		});	
		
		get("/events/:eventid", (req, res) -> {
			String gameID = req.queryParams("gameid");
			String eventid = req.params(":eventid");
			if(gameID != null){
				List<Event> EventList = events.get(gameID);
				for (Event event : EventList) {
					if(event.getId().equals(eventid)){
						res.status(200);
						return gson.toJson(event);
					}
				}
				res.status(404);
				return null;
			}
			res.status(404);
			return null;
		});		
		
		get("/events/subscriptions", (req, res) -> {
			String gameID = req.queryParams("gameid");
			if(gameID != null){
				List<Subscription> subs = new ArrayList<>();
				for (Subscription subscription : subscriptions) {
					if(subscription.getGameid().equals(gameID)){
						subs.add(subscription);
					}
				}
				res.status(200);
				return gson.toJson(subs); 
			}
			res.status(404);
			return null;
		});		
		
		post("/events/subscriptions", (req, res) -> {
			String gameID = req.queryParams("gameid");
			if(gameID != null){
				Subscription subscription = gson.fromJson(req.body(), Subscription.class);
				subscription.setGameid(gameID);
				subscription.setId(UUID.randomUUID().toString());
				subscription.setUri(serviceUri + "/subscription/" + subscription.getId());
				
				subscriptions.add(subscription);
				
				res.header("Location", subscription.getUri());
				res.status(200);
				return gson.toJson(subscription);
			}
			res.status(404);
			return null;
		});
		
		delete("/events/subscriptions/subscriptions/:subscription", (req, res) -> {
			String gameID = req.queryParams("gameid");
			if(gameID != null){
				for (Subscription subscription : subscriptions) {
					if(subscription.getId().equals(req.params(":subscription"))){
						subscriptions.remove(subscription);
						return true;
					}
				}
			}
			res.status(404);
			return null;
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
	

	private static void addEvent(String gameID, Event event) {
		List<Event> eventList = events.get(gameID);
		if(eventList == null){
			eventList = new ArrayList<Event>();
		}
		eventList.add(event);
		events.put(gameID, eventList);
	}
}
