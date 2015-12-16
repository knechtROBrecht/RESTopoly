package implementation;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Board {

	private String uri;
	private List<Field> fields = new ArrayList<>();
	private Map<String, Integer> positions = new HashMap<String, Integer>();
	private List<Player> players = new ArrayList<>();
		
	public Board() {
		fields.add(new Field(new Place("Los")));
		fields.add(new Field(new Place("Badstraße")));
		fields.add(new Field(new Place("Gemeinschaftsfeld")));
		fields.add(new Field(new Place("Turmstraße")));
		fields.add(new Field(new Place("Einkommensteuer")));
		fields.add(new Field(new Place("Südbahnhof")));
		fields.add(new Field(new Place("Chausseestraße")));
		fields.add(new Field(new Place("Ereignisfeld")));
		fields.add(new Field(new Place("Elisenstraße")));
		fields.add(new Field(new Place("Poststraße")));
		fields.add(new Field(new Place("Gefängnis.Besucher")));
		fields.add(new Field(new Place("Seestraße")));
		fields.add(new Field(new Place("Elektrizitätswerk")));
		fields.add(new Field(new Place("Hafenstraße")));
		fields.add(new Field(new Place("Neue Straße")));
		fields.add(new Field(new Place("Westbahnhof")));
		fields.add(new Field(new Place("Münchener Straße")));
		fields.add(new Field(new Place("Wiener Straße")));
		fields.add(new Field(new Place("Berliner Straße")));
		fields.add(new Field(new Place("Frei parken")));
		fields.add(new Field(new Place("Theaterstraße")));
		fields.add(new Field(new Place("Museumsstraße")));
		fields.add(new Field(new Place("Opernplatz")));
		fields.add(new Field(new Place("Nordbahnhof")));
		fields.add(new Field(new Place("Lessingstraße")));
		fields.add(new Field(new Place("Schillerstraße")));
		fields.add(new Field(new Place("Wasserwerk")));
		fields.add(new Field(new Place("Goethestraße")));
		fields.add(new Field(new Place("Gehe ins Gefängnis")));
		fields.add(new Field(new Place("Rathausplatz")));
		fields.add(new Field(new Place("Hauptstraße")));
		fields.add(new Field(new Place("Bahnhofstraße")));
		fields.add(new Field(new Place("Parkstraße")));
		fields.add(new Field(new Place("Zusatzsteuer")));
		fields.add(new Field(new Place("Schlossallee")));
	}

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public void setPlayers(List<Player> players) {
		this.players = players;
	}

	public Map<String, Integer> getPositions() {
		return positions;
	}

	public void setPositions(Map<String, Integer> positions) {
		this.positions = positions;
	}

	public void updatePosition(Player player, int newPosition) {
		int oldPosition = player.getPosition();

		if (!fields.get(oldPosition).removePlayer(player.getId())) {
			//TODO throw exception
		}

		int position = newPosition % fields.size();
		//TODO wenn über los dann geld
		player.setPosition(position);
		fields.get(position).addPlayer(player.getId());
		positions.put(player.getId(), position);
	}

	public boolean removePlayer(String playerID) {
		return positions.remove(playerID) != null;
	}

	public boolean hasPlayer(String playerID) {
		return positions.containsKey(playerID);
	}

	public Player getPlayer(String playerID) {
		for (Player player : players) {
			if(player.equals(playerID)){
				return player;
			}
		}
		return null;
	}

	public void addPlayer(Player player) {		
		players.add(player);		
	}
	
	public List<Place> getPlaces() {
		return fields.stream().map(p -> p.getPlace()).collect(Collectors.toList());
	}
	
	public Place getPlace(String placeID) {
		return fields.stream().filter(f -> f.getPlace().getName() == placeID).findFirst().get().getPlace();
	}

	public Place updatePlace(Place place) {
		
		return null;
	}
}
