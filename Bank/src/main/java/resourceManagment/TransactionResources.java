package resourceManagment;

/**
 * In this object we define the resources from our transaction / coordinator service
 * @author foxhound
 *
 */
public class TransactionResources {

	/*
	 * Resources
	 */
	private String resourceMain = "/banks";
	private String resourcePlayer = "/players";
	private String resourceTransfer = "/transfer";
	private String resourceTo = "/to";
	private String resourceFrom = "/from";
	private String resourceCoordinator = "/coordinator";
	
	private String resourceParamGameID = "/:gameID";
	private String resourceParamTo = "/:to";
	private String resourceParamFrom = "/:from";
	private String resourceParamAmount = "/:amount";
	private String resourceParamPlayer = "/:playerID";
	
	/*
	 * Param to resources
	 */
	public String paramGameID = resourceParamGameID.replace("/:", "");
	public String paramPlayerID = resourceParamPlayer.replace("/:", "");
	public String paramAmount = resourceParamAmount.replace("/:", "");
	public String paramTo = resourceParamTo.replace("/:", "");
	public String paramFrom = resourceParamFrom.replace("/:", "");
	
	/**
	 * Method get the playerResource service url, without http://ip:port
	 * @return String
	 */
	public String getCreatePlayerAccountResourceService() {		
		return resourceMain + resourceParamGameID + resourcePlayer;
	}
	
	/**
	 * Method get the call account balance service url, without http://ip:port
	 * @return String
	 */
	public String getCallAcountBalanceResourceService() {
		return resourceMain + resourceParamGameID + resourcePlayer + resourceParamPlayer;
	}

	/**
	 * Method get the bank transfer to player service url, without http://ip:port
	 * @return String
	 */
	public String getBankTransferToPlayerResourceService() {
		return resourceMain + resourceParamGameID + resourceTransfer + resourceTo + resourceParamTo + resourceParamAmount;
	}

	/**
	 * Method get the bank transfer from player service url, without http://ip:port
	 * @return String
	 */
	public String getBankTransferFromPlayerResourceService() {
		return resourceMain + resourceParamGameID + resourceTransfer + resourceFrom + resourceParamFrom + resourceParamAmount;
	}
	
	/**
	 * Method get the player transfer to player service url, without http://ip:port
	 * @return String
	 */
	public String getPlayerTransferToPlayerResourceService() {
		return resourceMain + resourceParamGameID + resourceTransfer + resourceFrom + resourceParamFrom + 
				resourceTo + resourceParamTo + resourceParamAmount;
	}
	
	/**
	 * Method get the commit protocol service url, without http://ip:port
	 * @return String
	 */
	public String getCommitProtocolResourceService() {
		return resourceMain + resourceCoordinator;
	}
}
