package implementation;

public class Subscription {
	
	/**
	 * Holds the uniqeidentifier for a gameobject
	 */
	private String gameID;
	
	/**
	 * The resource intetested in the event
	 */
	private String uri;
	
	/**
	 * Eventobject
	 */
	private Event event;
	
	public Subscription(String gameID) {
		this.gameID = gameID;
	}
	
	//=======================================================
	//		  GENERATED GETTER AND SETTER METHODS
	//=======================================================	

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public String getGameID() {
		return gameID;
	}
		
}
