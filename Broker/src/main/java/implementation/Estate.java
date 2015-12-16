package implementation;

import java.util.*;

public class Estate {
	
	private String place;
	//the playerid of the owner of the place
    private String owner = "";
    //The value of the place, i.e. for how much it may be bought or sold
    private Integer value = 0;
    //Rent at current level
    private List<Integer> rent = new ArrayList<>();
    //Cost for house upgrade
    private List<Integer> cost = new ArrayList<>();
    //amount of houses set on the estate	
    private Integer houses = 0;
	
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
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
