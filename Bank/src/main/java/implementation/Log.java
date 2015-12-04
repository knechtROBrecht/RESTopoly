package implementation;

/**
 * This is our log class for our bank's
 * @author foxhound
 *
 */
public class Log {

	private String info = "";
	private int amountBefore = 0;
	private int amountAfter = 0;
	
	/**
	 * Constructor
	 * @param info - a info
	 * @param amountBefore - current amount
	 * @param amountAfter - amount after the transaction
	 */
	public Log(String info, int amountBefore, int amountAfter) {
		this.info = info;
		this.amountBefore = amountBefore;
		this.amountAfter = amountAfter;
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
	 * @return int
	 */
	public int getAmountBefore() {
		return amountBefore;
	}

	/**
	 * Getter
	 * @return int
	 */
	public int getAmountAfter() {
		return amountAfter;
	}
	
	
}
