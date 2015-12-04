package resourceManagment;

/**
 * In this Object wie define, our Bank resources
 * @author foxhound
 *
 */
public class BankResources {

	/*
	 * Resources
	 */
	public String resourceMain = "/banks";
	public String resourcePlayer = "/players";
	public String resourceTransfer = "/transfer";
	public String resourceTo = "/to";
	public String resourceFrom = "/from";
	public String resourceCoordinator = "/coordinator";
	
	public String resourceParamGameID = "/:gameID";
	public String resourceParamTo = "/:to";
	public String resourceParamFrom = "/:from";
	public String resourceParamAmount = "/:amount";
	public String resourceParamPlayer = "/:playerID";
	
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
	public String getCreatePlayerResourceService() {		
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
