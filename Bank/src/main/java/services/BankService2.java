package services;

public class BankService2 {

	public static void main(String[] args) {
		
		// create a bank service object
		BankService bankService = new BankService(4569);

		/**
		 * Service create a account
		 * ein Konto erstellt werden kann mit
		 * post /banks/{gameid}/players
		 */
		//bankService.startCreatePlayerAccountService();
		bankService.twoPhaseCommitProtocol();
		
		/**
		 * call account balance / kontostand abfragen
		 * der Kontostand abgefragt werden kann mit
		 * get /banks/{gameid}/players/{playerid}
		 */
		//bankService.callAccountBalanceService();
		
	}
}
