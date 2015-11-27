package eventsService;

import static spark.Spark.*;
import implementation.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventsService {
	
	/**
	 * Unique identifier for a eventlist
	 */
	private static int eventsCounter = 0;
	
	/**
	 * Holds the created eventlists in a dictionary
	 */
	private static Map<Integer, List<Event>> eventsMap = new HashMap<>();
	
	/**
	 * Holds the subscriptors in a dictionary
	 */
	private static Map<Integer, List<Object>> subscriptorsMap = new HashMap<>();
	
	/**
	 * Creates a new EventList
	 * @return unique identifier for the new eventlist
	 */
	private static int newEventType() {
		eventsMap.put(eventsCounter, new ArrayList<>());
		return eventsCounter++;		
	}
	
	public static void main(String[] args) {
		/*
		 * Erzeugt eine neue Eventliste, in die sich Interessenten einschreiben können
		 * und bei einem neuen event, in der liste, benachrichtigt werden
		 */
		post("/events", (req, res) -> {
			return newEventType();
		});
		
		post("/events/subscriptions", (req, res) -> {
			return "";
		});
		
		post("", (req, res) -> {
			return "";
		});		
	}
}
