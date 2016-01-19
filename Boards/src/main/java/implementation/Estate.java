package implementation;

import java.util.ArrayList;
import java.util.List;

public class Estate {

    private String place;
    private String owner;
    private Integer value;
    private List<Integer> rent = new ArrayList<>();
    private List<Integer> cost = new ArrayList<>();
    private Integer houses = 0;
    private String visit;
    private String hypocredit;

    public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getVisit() {
		return visit;
	}

	public void setVisit(String visit) {
		this.visit = visit;
	}

	public String getHypocredit() {
		return hypocredit;
	}

	public void setHypocredit(String hypocredit) {
		this.hypocredit = hypocredit;
	}

	public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public List<Integer> getRent() {
        return rent;
    }

    public void setRent(List<Integer> rent) {
        this.rent = rent;
    }

    public List<Integer> getCost() {
        return cost;
    }

    public void setCost(List<Integer> cost) {
        this.cost = cost;
    }

    public Integer getHouses() {
        return houses;
    }

    public void setHouses(Integer houses) {
        this.houses = houses;
    }

}
