package implementation;

import java.util.ArrayList;
import java.util.List;

public class BankUtil {
	
	public static List<Bank> bankList = new ArrayList<Bank>();
	
	/**
	 * Method add a bank in our bankList
	 * @param bank - 
	 * @return boolean
	 */
	public static boolean add(Bank bank) {
		return bankList.add(bank);
	}
	
	/**
	 * Method get a bank by gameID or null if the bank not exist
	 * @param gameID - a game ID
	 * @return Bank v null
	 */
	public static Bank getBank(String gameID) {		
		for (Bank bank : bankList) {
			if ( bank.getID().compareTo(gameID) == 0) {
				return bank;
			}
		}
		return null;
	}
	
}
