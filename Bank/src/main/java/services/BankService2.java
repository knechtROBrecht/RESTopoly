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
		bankService.startCreatePlayerService();
		
	}
}
