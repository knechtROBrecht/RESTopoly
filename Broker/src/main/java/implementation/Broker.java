package implementation;

import java.util.*;

public class Broker {

	private Map<String, Estate> estates = new HashMap<>();

	private Map<String, Player> players = new HashMap<>();
	
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
