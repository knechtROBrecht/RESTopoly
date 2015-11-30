package implementation;

/**
 * Implementation of the Event-Scheme
 * 
 * @author Flah
 * @see <a href="https://pub.informatik.haw-hamburg.de/home/pub/prof/kossakowski_klaus-peter/wise2015/verteiltesysteme/step3.raml">API</a>
 */
public class Event {
	
	/**
	 * Internal type of the event (e.g. bank transfer, rent)
	 */
	private String type;
	
	/**
	 * Human readable name for this event
	 */
	private String name;
	
	/**
	 * Reason for this event
	 */
	private String reason;
	
	/**
	 * The uri of the resource related to this event where more information may be found (e.g. an uri to a transfer or similar)
	 */
	private String resource;
	
	/**
	 * The player who issued this event
	 */
	private Player player;
	
	/**
	 * Public constructor
	 * this class can only be initiated with the following params (all required, see SPEC)
	 * 
	 * @param type
	 * @param name
	 * @param reason
	 */
	public Event(String type, String name, String reason) {
		this.type = type;
		this.name = name;
		this.reason = reason;
	}
	
	//=======================================================
	//		sGENERATED GETTER AND SETTER METHODS
	//=======================================================

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public String getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public String getReason() {
		return reason;
	}
}
