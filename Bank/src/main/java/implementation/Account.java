package implementation;

/**
 * This class implemented a account to a bank
 * playerID have a association with a account
 * @author foxhound
 *
 */
public class Account implements GameComponent{

	private Player player;
	private int saldo = 4000;
	
	/**
	 * Constructor
	 * @param playerID - a player id
	 */
	public Account(Player player) {
		this.player = player;
	}
	
	/**
	 * Getter
	 * @return
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Method return the account id
	 * account id is equal to playerID
	 */
	@Override
	public String getID() {
		return player.getID();
	}
	
	/**
	 * Method return the saldo to a account
	 * @return int
	 */
	public int getSaldo() {
		return saldo;
	}
	
	/**
	 * Method set a new saldo, by successful set return our method true, else false
	 * @param saldo - 
	 * @return boolean
	 */
	public boolean setSaldo(int saldo) {
		try {
			this.saldo = saldo;
			return true;
		} catch (Exception e) {
			return false;
		}		
	}
	
}
