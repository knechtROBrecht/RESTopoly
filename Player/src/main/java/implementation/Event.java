package implementation;

public class Event {

	private String id;
	private String uri;
	private String type;
	private String name;
	private String reason;
	private String resource;
	private String player;
	
	
	public Event(String uri, String type, String name, String reason, String resource, String player){
		this.uri = uri;
		this.type = type;
		this.name = name;
		this.reason = reason;
		this.resource = resource;
		this.player = player;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getPlayer() {
		return player;
	}

	public void setPlayer(String player) {
		this.player = player;
	}		
}
