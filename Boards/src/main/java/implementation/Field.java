package implementation;

import java.util.ArrayList;
import java.util.List;

public class Field {
	
	private Place place;
	private List<String> players = new ArrayList<String>();

	public Field(Place place){
		this.place = place;
	}
	
	public Place getPlace(){
		return place;
	}
	
	public List<String> getPlayers(){
		return players;
	}
	
	public void addPlayer(String player){
		players.add(player);
	}
	
	public boolean removePlayer(String player){
		for (int i = 0; i < players.size(); i++) {
			if(players.get(i).equals(player)){
				players.remove(i);
				return true;
			}
		}
		return false;
	}
}
