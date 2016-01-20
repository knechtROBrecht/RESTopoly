package implementation;

/**
 * Implementation of the Player-Scheme
 * 
 * @author Flah
 * @see <a href="https://pub.informatik.haw-hamburg.de/home/pub/prof/kossakowski_klaus-peter/wise2015/verteiltesysteme/step2.raml">API</a>
 */
public class Player {
	
	/**
	 * Unique identifier of a Player-Object
	 */
	private String playerID;
	
	/**
	 * Playername
	 */
	private String name;
	
	/**
	 * Full Uri to a player object
	 */
	private String uri;
	
	/**
	 * Indicates the playerpostion on a board
	 */
	private int position;
	
	/**
	 * Indicates if the player object is ready for playing
	 */
	private String ready;

	private boolean readyBol;
	
	

	public Player(String playerID, String name, String uri) {
		this.playerID = playerID;
		this.name = name;
		this.uri = uri;
		this.readyBol = false;
	}
	
	public String getID() {
		return this.playerID;		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getReady() {
		return ready;
	}
	
	public String readyUp(String str) {
		return this.ready = str;
	}

	public void readyUp() {
		this.readyBol = true;
	}
}