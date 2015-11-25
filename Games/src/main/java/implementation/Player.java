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
	private Boolean ready;
	
	/**
	 * Public constructor
	 * Assigns a unique identifier for a object
	 * Initializes the attributes
	 * @param Unique identifier for a object
	 */
	public Player(String playerID) {
		this.playerID = playerID;
		this.ready = false;
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

	public Boolean getReady() {
		return ready;
	}

	public void setReady(Boolean ready) {
		this.ready = ready;
	}
}