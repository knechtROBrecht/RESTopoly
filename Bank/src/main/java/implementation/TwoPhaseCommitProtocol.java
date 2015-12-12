package implementation;

/**
 * Two-Phase-Commit-Protocol
 * @author foxhound
 *
 */
public class TwoPhaseCommitProtocol {

	/*
	 * key words
	 */
	public static final String PREPARE = "prepare";
	public static final String READY = "ready";
	public static final String FAILED = "failed";
	public static final String COMMIT = "commit";
	public static final String ACKNOWLEDGMENT = "acknowledgment";
	public static final String ABORT = "abort";
	
	/*
	 * bank commands
	 */
	
	/**
	 * ein Konto erstellt werden kann mit
     * post /banks/{gameid}/players
	 */
	public static final String SERVICE_IDENT_CREATE_PLAYER_ACCOUNT = "create player account";
	
	/**
	 * call account balance / kontostand abfragen
	 * der Kontostand abgefragt werden kann mit
	 * get /banks/{gameid}/players/{playerid}
	 */
	public static final String SERVICE_IDENT_CALL_ACCOUNT_BALANCE = "call account balance";
	
	/**
	 * Geld von der Bank überwiesen werden kann mit
	 * post /banks/{gameid}/transfer/to/{to}/{amount}
	 */
	public static final String SERVICE_IDENT_BANK_TRANSFER_MONEY_TO_PLAYER = "bank transfer money to a player";
		
	/**
	 * Geld eingezogen werden kann mit
	 * post /banks/{gameid}/transfer/from/{from}/{amount}
	 */
	public static final String SERVICE_IDENT_BANK_TRANSFER_MONEY_FROM_PLAYER = "bank transfer money from a player";
	
	/**
	 * Geld von einem zu anderen Konto übertragen werden kann mit
	 * post /banks/{gameid}/transfer/from/{from}/to/{to}/{amount}	
	 */
	public static final String SERVICE_IDENT_PLAYER_TRANSFER_TO_PLAYER = "a player transfer a amount to a other player";
	
}
