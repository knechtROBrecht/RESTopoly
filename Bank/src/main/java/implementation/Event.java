package implementation;

/**
 * Our Event class
 * @author foxhound
 * "properties": {
 * 	  "type": { "type": "string", "required": true , "description":"internal type of the event (e.g bank transfer, rent, got to jail, estate transfer)" },
 *    "name": { "type": "string", "required": true, "description":"human readable name for this event"  },
 *    "reason": { "type": "string", "required": true, "description":"a description why this event occured"  },
 *    "resource": {"type": "string", "description": "the uri of the resource related to this event where more information may be found (e.g. an uri to a transfer or similar)" },
 *    "player": { "$ref": "player", "description": "The player issued this event" }
 */
public class Event {
	
	// raml properties
	private String type = "";
	private String name = "";
	private String reason = "";
	private String resource = "";
	private Player player;
	
	/**
	 * Constructor
	 * @param type
	 * @param name
	 * @param reason
	 * @param resource
	 * @param player
	 */
	public Event(String type, String name, String reason, String resource, Player player) {
		this.type = type;
		this.name = name;
		this.reason = reason;
		this.resource = resource;
		this.player = player;
	}
	
	/**
	 * Getter
	 * @return String
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Setter
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * Getter 
	 * @return String
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Setter
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Getter
	 * @return String
	 */
	public String getReason() {
		return reason;
	}
	
	/**
	 * Setter
	 * @param reason
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}
	
	/**
	 * Getter
	 * @return String
	 */
	public String getResource() {
		return resource;
	}
	
	/**
	 * Setter
	 * @param resource
	 */
	public void setResource(String resource) {
		this.resource = resource;
	}
	
	/**
	 * Getter
	 * @return Player
	 */
	public Player getPlayer() {
		return player;
	}
	
	/**
	 * Setter
	 * @param player
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}
}
