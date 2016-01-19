package implementation;

import java.util.*;

public class Broker {
	
	private Game game;
	
	private String uri;

	private Map<String, Estate> estates = new HashMap<>();

	private Map<String, Player> players = new HashMap<>();
	
	private List<String> hypothecaryCredits = new ArrayList<String>();
	
	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public Map<String, Player> getPlayers() {
		return players;
	}

	public void setPlayers(Map<String, Player> players) {
		this.players = players;
	}
	
	public void addPlayer(Player player){
		players.put(player.getName(), player);
	}
	
	public Player getPlayer(String playerID){
		for (String player : players.keySet()) {
			if(player.equals(playerID)){
				return players.get(player);
			}
		}
		return null;
	}

	public List<String> getHypothecaryCredits() {
		return hypothecaryCredits;
	}

	public void setHypothecaryCredits(List<String> hypothecaryCredits) {
		this.hypothecaryCredits = hypothecaryCredits;
	}
	
    public boolean placeHasHypothecaryCredit(String placeid) {
        boolean result = false;

        for (String place : hypothecaryCredits) {
            if(place.equals(placeid)) {
                result = true;
            }
        }

        return result;
    }

	public Map<String, Estate> getEstates() {
        return estates;
    }

    public void setEstates(Map<String, Estate> estates) {
        this.estates = estates;
    }

    public Estate addEstate(String placeid, Estate estate) {
        this.estates.put(placeid, estate);
        return estate;
    }

    public boolean hasEstate(String placeid) {
        return this.estates.get(placeid) != null;
    }

    public Estate getEstate(String placeid) {
        return this.estates.get(placeid);
    }
    


}
