package implementation;

import java.util.ArrayList;
import java.util.List;

import implementation.Components;

public class Game {

	private static int gamesCounter = 0;

	private String gameID;
	
	public String uri;

	private List<Player> players;
	
	Components components;
	
	
	
	public String toString(){
		return gameID + "//" + uri; 
	}
	
	public Components getComponents() {
		return components;
	}

	public void setComponents(Components components) {
		this.components = components;
	}

	private boolean started = false;
	
	private Player mutex = null;

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
