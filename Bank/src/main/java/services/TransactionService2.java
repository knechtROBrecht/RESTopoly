package services;

/**
 * replication service of transaction
 * @author foxhound
 *
 */
public class TransactionService2 {

	public static void main(String[] args) throws InterruptedException {
		TransactionService transactionService = new TransactionService(4, 4700);
		
		transactionService.startCreatePlayerAccountService();		
		transactionService.startCallAccountBalanceService();		
		transactionService.startBankTransferToPlayerService();		
		transactionService.startBankTransferFromPlayerService();		
		transactionService.startPlayerTransferToPlayerService();
		transactionService.startBullyService();
		
		transactionService.bully.holdElection();
		
		while ( true ) {
			
			Thread.sleep(5000);
			
			// new election
			transactionService.bully.holdElection();
			
			if ( transactionService.bully.getCoordinatorFlag() ) {
				System.out.println("TransactionService 2 is the current coordinator");
			} else {
				System.out.println("TransactionService 2 is not the coordinator");
			}
			
			// now we are the new coordinator or not
		}
	}
}
