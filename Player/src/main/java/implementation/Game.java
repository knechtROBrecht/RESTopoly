package implementation;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the Game-Scheme
 * 
 * @author Flah
 * @see <a href=
 *      "https://pub.informatik.haw-hamburg.de/home/pub/prof/kossakowski_klaus-peter/wise2015/verteiltesysteme/step2.raml">
 *      API</a>
 */
public class Game {

	private static int gamesCounter = 0;

	private String gameID;
	
	private String uri;

	private List<Player> players;
	
	Components components;
	
	private boolean started = false;
	
	private Player mutex = null;
	
	public String toString(){
		return gameID + "//" + uri; 
	}

	/**
	 * Public constructor Assigns a unique identifier for a object Initializes
	 * the players collection
	 */
	public Game() {
		this.gameID = Integer.toString(gamesCounter);
		gamesCounter++;
		this.players = new ArrayList<Player>();
	}

	/**
	 * Adds a player to a game
	 * 
	 * @param p
	 *            - a player object
	 */
	public boolean addPlayer(Player p) {
		return this.players.add(p);
	}

	/**
	 * Removes a player from a game
	 * 
	 * @param p
	 *            - a player object
	 */
	public boolean removePlayer(Player p) {
		return this.players.remove(p);
	}

	/**
	 * @return List of all assgined players to this game
	 */
	public List<Player> getPlayersList() {
		return this.players;
	}

	/**
	 * Method return a player by id By success return the method a player, else
	 * null
	 * 
	 * @param id
	 *            - player id
	 * @return Player
	 */
	public Player getPlayerByID(String id) {
		for (Player player : players)
			if (id.equals(player.getID()))
				return player;
		return null;
	}
	
	/**
	 * @ return Unique identifier for this game
	 */
	public String getID() {
		return this.gameID;
	}
	
	public void setReady(){
		started = true;
	}
	
	public boolean getStatus(){
		return started;
	}
	
	public Player getMutex() {
        return mutex;
    }

    public void releaseMutex() {
    	mutex = null;
    }

    public boolean acquireMutex(Player player) {
        if(mutex == null && player != null) {
        	mutex = player;
            return true;
        }
        return false;
    }

	public boolean doesPlayerExists(String playerID) {
		return getPlayerByID(playerID) != null; 
	}
}
