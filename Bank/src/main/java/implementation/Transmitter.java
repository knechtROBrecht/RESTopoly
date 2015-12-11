package implementation;

public class Transmitter {

	/*
	 * attributes
	 */
	private String serviceIdent = "";
	private String phase = "";
	private Player player;
	private String gameID = "";
	private String to = "";
	private String from = "";
	private int amount = 0;
	private String playerID = "";
	private boolean operationFlag = false;
	private String resultMessage = "";
	private String reason = "";

	/**
	 * Constructor
	 * @param player - 
	 * @param gameID - 
	 * @param playerID - 
	 * @param from - 
	 * @param to - 
	 * @param amount - 
	 */
	public Transmitter(String serviceIdent, Player player, String gameID, String playerID, String from, String to, int amount) {
		this.serviceIdent = serviceIdent;
		this.player = player;
		this.gameID = gameID;
		this.playerID = playerID;
		this.from = from;
		this.to = to;
		this.amount = amount;
	}
	
	/**
	 * Setter
	 * @param phase
	 */
	public void setTwoPhaseCommitProtocolIdentifier(String phase) {
		this.phase = phase;
	}
	
	public String getServiceIdent() {
		return serviceIdent;
	}

	public String getPhase() {
		return phase;
	}

	public Player getPlayer() {
		return player;
	}

	public String getGameID() {
		return gameID;
	}

	public String getTo() {
		return to;
	}

	public String getFrom() {
		return from;
	}

	public int getAmount() {
		return amount;
	}

	public String getPlayerID() {
		return playerID;
	}
	
	public String getResultMessage() {
		return this.resultMessage;
	}
	
	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}	
	
	/**
	 * Operation getter
	 * @return
	 */
	public boolean getOperationIsSuccessful() {
		return operationFlag;
	}
	
	/**
	 * Operation setter
	 * @param operationFlag
	 */
	public void setOperationIsSuccessful(boolean operationFlag) {
		this.operationFlag = operationFlag;
	}
	
	public String getReason() {
		return this.reason;
	}
	
	public void setReason(String reason) {
		this.reason = reason;
	}

	/**
	 * Getter
	 * @return String
	 */
	public String getTwoPhaseCommitProtocolIdentifier() {
		return phase;
	}
}
