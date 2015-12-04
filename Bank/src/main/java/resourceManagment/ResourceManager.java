package resourceManagment;

public class ResourceManager {

	private TransactionResources transactionResources = new TransactionResources();
	private BankResources bankResources = new BankResources();	
	
	/**
	 * Manager get you the bank resources
	 * @return
	 */
	public BankResources getBankResources() {
		return bankResources;
	}
	
	/**
	 * Manager get you the transaction / coordinator resources
	 * @return
	 */
	public TransactionResources getTransactionResources() {
		return transactionResources;
	}	
}
