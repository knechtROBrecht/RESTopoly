package eventsService;

import static spark.Spark.*;
import implementation.Event;
import implementation.Subscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

public class EventsService {
	
	// Uniqueidentifier fuer ein event was per REST reinkommt
	private static int eventID = 0;
	
	public static void main(String[] args) {
		// Key = EventID; Value = EventObject
		Map<Integer, Event> eventsMap = new HashMap<>();
		
		// Speichert alle subscriptions
		List<Subscription> subscricptionsList = new ArrayList<>();
		
		Gson gson = new Gson();
		
		// List of all available events
		get("/events", (req, res) -> {
			//String gameID = req.queryParams("gameid");			
			//System.out.println("GameID GET: " + gameID);			
			
			if(eventsMap.isEmpty()) {
				return "Es existieren keine Events";
			} else {
				return gson.toJson(eventsMap.values());
			}
		});		
		
		// Add new Event for a game
		post("/events", (req, res) -> {
			//String gameID = req.queryParams("gameid");
			//System.out.println("GameID POST: " + gameID);
			
			// Event Objekt aus Json Body erstellen
			Event event = gson.fromJson(req.body(), Event.class);	
			
			// Event mit identifier abspeichern
			eventsMap.put(eventID, event);
			
			// ID des gespeicherten events zurueckgeben und internen zaehler inkrementieren
			return "/events/" + eventID++;
		});
		
		// Gibt das Event als JSon zurueck
		get("/events/:eventid", (req, res) -> {
			String eventID = req.params("eventid");
			
			if(eventsMap.containsKey(eventID)) {
				res.status(200);
				return gson.toJson(eventsMap.get(eventID));
			} else {
				res.status(404);
				return "Event not found";
			}
		});		
		
		get("/events/subscriptions", (req, res) -> {
			if(subscricptionsList.isEmpty()) {
				return "No Subscriptions";
			} else {
				return gson.toJson(subscricptionsList);
			}
		});		
		
		post("/events/subscriptions", (req, res) -> {
			// Event Objekt aus Json Body erstellen
			Subscription subscription = gson.fromJson(req.body(), Subscription.class);
			subscricptionsList.add(subscription);
			return "";
		});
	}
}
