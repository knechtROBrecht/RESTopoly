package implementation;

/**
 * This is our log class for our bank's
 * @author foxhound
 *
 */
public class Log {

	private String info = "";
	private Bank undoBank = null;
	private Bank redoBank = null;
	
	/**
	 * Default Constructor
	 */
	public Log() {

	}
	
	/**
	 * Method set a info for this object
	 * @param info
	 */
	public void setInfo(String info) {
		this.info = info;
	}

	/**
	 * Method saved a bank object in a undo variable
	 * @param bank
	 */
	public void setUndo(Bank bank) {
		this.undoBank = bank;
	}
	
	/**
	 * Method saved a bank object in a redo variable
	 * @param bank
	 */
	public void setRedo(Bank bank) {
		this.redoBank = bank;
	}
	
	/**
	 * Getter
	 * @return String
	 */
	public String getInfo() {
		return info;
	}
	
	/**
	 * Getter
	 * @return Bank
	 */
	public Bank getUndo() {
		return undoBank;
	}
	
	/**
	 * Getter
	 * @return Bank
	 */
	public Bank getRedo() {
		return redoBank;
	}
	
}
