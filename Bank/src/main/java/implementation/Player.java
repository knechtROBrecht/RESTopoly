package implementation;

/**
 * Our Bank player to raml specification
 * @author foxhound
 *
 */
public class Player {
	
	// raml specification properties 
	private String id;
	private String name;
	private String uri;
	private Place place;
	private int position;
	private boolean ready;
	
	/**
	 * Public constructor
	 * Assigns a unique identifier for a object
	 * Initializes the attributes
	 * @param Unique identifier for a object
	 */
	public Player(String playerID) {
		this.id = playerID;
		this.ready = false;
	}
	
	/**
	 * Default constructor
	 */
	public Player() {
		this.ready = false;
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
	public String getUri() {
		return uri;
	}

	/**
	 * Setter
	 * @param uri - url as string
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}

	/**
	 * Getter
	 * @return Place
	 */
	public Place getPlace() {
		return place;
	}

	/**
	 * Setter
	 * @param place - place object
	 */
	public void setPlace(Place place) {
		this.place = place;
	}

	/**
	 * Getter
	 * @return int
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * Setter
	 * @param position - position of player in a board
	 */
	public void setPosition(int position) {
		this.position = position;
	}

	/**
	 * Getter
	 * @return
	 */
	public String getID() {
		return id;
	}

	/**
	 * Getter
	 * @return boolean
	 */
	public boolean getReady() {
		return ready;
	}

	/**
	 * Setter
	 * @param ready -  
	 */
	public void setReady(boolean ready) {
		this.ready = ready;
	}
	
}