package implementation;

/**
 * This class is our transaction 
 * @author foxhound
 *
 */
public class Transfer {

	private String from = "undef";
	private String to = "undef";
	private String reason = "undef";
	
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	
	/**
	 * Bank pull a amount from a player
	 * @param bank - a bank object from a game
	 * @param account - a player account
	 * @param amount - transfer amount
	 * @param reason - why you do a transaction?
	 * @return boolean
	 */
	public boolean transferPull(Bank bank, Account account, int amount, String reason) {
		
		// get money from bank and a account
		int bankMoney = bank.getBankAmount();		
		int playerMoney = account.getSaldo();
		
		// pull money from a player account
		boolean successSetPlayerMoney = account.setSaldo(playerMoney - amount);
		
		// condition
		if ( !successSetPlayerMoney ) {
			return false;
		}
		
		// push the money to our bank
		boolean successSetBankMoney = bank.setBankAmount(bankMoney + amount);
		
		// condition
		if ( !successSetBankMoney ) {
			return false;
		}
		
		// set description
		setProperties(account.getID(), "bank pull money from a player", reason);
		return true;
	}

	/**
	 * Method push money from the bank to a player
	 * @param playerID -  id from a player 
	 * @param amount - tranfer amount
	 * @param reason - what ever
	 * @return boolean
	 */
	public boolean transferPush(Bank bank, Account account, int amount, String reason) {
		// get money from bank and a account
		int bankMoney = bank.getBankAmount();
		int playerMoney = account.getSaldo();

		// pull money from our bank
		boolean successSetBankMoney = bank.setBankAmount(bankMoney - amount);

		// condition
		if (!successSetBankMoney) {
			return false;
		}

		// push the money to our player
		boolean successSetPlayerMoney = account.setSaldo(playerMoney + amount);

		// condition
		if (!successSetPlayerMoney) {
			return false;
		}

		// set description
		setProperties(account.getID(), "bank push money to a player", reason);
		return true;
	}
	
	/**
	 * Method transfer a mount from a account to a other account
	 * @param accountFrom - sub money
	 * @param accountTo - add money
	 * @param amount - +- money value
	 * @param reason - why you do why you do a transaction?
	 * @return boolean 
	 */
	public boolean transfer(Account accountFrom, Account accountTo, int amount, String reason) {
		
		// get money from accounts
		int moneyFromPlayer = accountFrom.getSaldo();
		int moneyToPlayer = accountTo.getSaldo();
		
		// sub money from accountFrom
		boolean successSub = accountFrom.setSaldo(moneyFromPlayer - amount);
		
		// condition
		if ( !successSub ) {
			return false;
		}
		
		// add money to, accountTo
		boolean successAdd = accountTo.setSaldo(moneyToPlayer + amount); 
		
		// condition
		if ( !successAdd ) {
			return false;
		}
		
		// set description
		setProperties(accountFrom.getID(), accountTo.getID(), reason);
		return true;
	}
	
	/**
	 * Helper method for setting properties
	 */
	private void setProperties(String from, String to, String reason) {
		this.from = from;
		this.to = to;
		this.reason = reason;
	}
}
