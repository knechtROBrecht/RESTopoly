package services;

import implementation.IO;

/**
 * replication service of transaction
 * @author foxhound
 *
 */
public class TransactionService2 {

	public static void main(String[] args) throws InterruptedException {
		
		IO io = new IO();
		
		TransactionService transactionService = new TransactionService(4, 4700);
		transactionService.startBullyService();
		transactionService.startCreatePlayerAccountService();		
		transactionService.startCallAccountBalanceService();		
		transactionService.startBankTransferToPlayerService();		
		transactionService.startBankTransferFromPlayerService();		
		transactionService.startPlayerTransferToPlayerService();
		
		boolean coordinatorIsAlive = true;
//		while ( coordinatorIsAlive ) {
//			
//			Thread.sleep(5000);
//			
//			transactionService.bully.holdElection();
//			
//			if ( transactionService.bully.getCoordinatorFlag() ) {
//				coordinatorIsAlive = false;
//			}			
//		}
		
		// we are the new coordinator, start all transaction services
	}
}
