package implementation;

public class Subscription {

	private String id;
	private String gameid;
	private String uri;
	private String callbackuri;
	private Event event;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getGameid() {
		return gameid;
	}
	public void setGameid(String gameid) {
		this.gameid = gameid;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getCallbackuri() {
		return callbackuri;
	}
	public void setCallbackuri(String callbackuri) {
		this.callbackuri = callbackuri;
	}
	public Event getEvent() {
		return event;
	}
	public void setEvent(Event event) {
		this.event = event;
	}
	

}
