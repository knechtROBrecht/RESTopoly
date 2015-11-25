package implementation;

import java.io.Serializable;

import com.google.gson.Gson;

/**
 * Our Roll implementation class
 * @author foxhound
 *
 */
public class Roll implements Serializable {
    private static final long serialVersionUID = 1337L;
    private int number;
    
    /**
     * Constructor
     * @param number
     */
    public Roll(int number) {
    	this.number = number;
    }
    
    /**
     * Getter for our number
     * @return
     */
    public int getNumber() {
    	return this.number;
    }

	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
    
 }