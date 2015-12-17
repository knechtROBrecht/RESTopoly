package services;

import java.security.KeyStore.Entry;

import implementation.BullyAlgorithm;
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
		
		// first step, where is the coordinator?
		String coordinatorUrl = "";
		
		transactionService.bully.holdElection();
		
		coordinatorUrl = transactionService.bully.getCoordinatorUrl();
		
		boolean coordinatorIsAlive = true;
		
		boolean weAreTheCoordinator = transactionService.bully.getCoordinatorFlag(); 
		
		// only we are not the coordinator, else we start all our services
		if ( !weAreTheCoordinator ) {
			
			while ( coordinatorIsAlive ) {
				Thread.sleep(5000);
				
				String response = io.request(coordinatorUrl, BullyAlgorithm.MESSAGE_ALIVE);
				
				// coordinator is alife
				if ( response.compareTo(BullyAlgorithm.MESSAGE_YES_I_ALIVE) == 0) {
					continue;
				}
				
				// coordinator is not alive, do a new election
				transactionService.bully.holdElection();
				
				// if we won, then start the transaction service
				
				boolean weAreTheWinner = transactionService.bully.getCoordinatorFlag();

				// check if we have won
				if ( weAreTheWinner ) {
					System.out.println("We are the winner : )");
					coordinatorIsAlive = false;
				} else {
					System.out.println("we are not the winner : (");
				}
			}
		}
		
		
		System.out.println("start all services, we was the coordinator");
		
		// we are the new coordinator, start all transaction services
		transactionService.startCreatePlayerAccountService();		
		transactionService.startCallAccountBalanceService();		
		transactionService.startBankTransferToPlayerService();		
		transactionService.startBankTransferFromPlayerService();		
		transactionService.startPlayerTransferToPlayerService();
	}
}
