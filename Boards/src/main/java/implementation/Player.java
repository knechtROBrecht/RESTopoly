package implementation;

public class Player {
	
	private String id;
	private String uri;
	private Place place;
	private int position;
	
	public Player(String id){
		this.id = id;
		this.position = 0;
	}
	
	public String getId(){
		return id;
	}
	
	public String getUri(){
		return uri;
	}
	
	public Place getPlace(){
		return place;
	}

	public void setPlace(Place place){
		this.place = place;
	}
	
	public int getPosition(){
		return position;
	}
	
	public void setPosition(int position){
		this.position = position;
	}
}
