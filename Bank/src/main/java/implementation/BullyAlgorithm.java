package implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import static spark.Spark.*;

/**
 * Here is the implementation from our Bully-Algorithm
 * election -> Wahl
 * @author foxhound
 *
 */
public class BullyAlgorithm {
		
	// if the flag is of true, then is the transaction services with same id the coordinator
	private boolean coordinator = false;
	
	// here we save all our transaction services with our id
	private Map<String, Integer> processMap = new HashMap<String, Integer>();
	
	// rest communication
	private IO io = new IO();
	
	// save current id from this object
	private int id;
	
	// resource from this bully server
	private String resource = "";
	
	// resource
	public static final String RESOURCE_PATH_2 = "/transactionService/bully";
	
	// current coordinator url
	private String coordinatorUrl = "http://localhost:4567" + RESOURCE_PATH_2;
	
	// communication protocol
	public static final String MESSAGE_ALIVE = "are you alive";
	public static final String MESSAGE_YES_I_ALIVE = "yes bro, i am alive";
	private static final String MESSAGE_ELECTOIN = "start election";
	private static final String MESSAGE_OK = "ok bro, i take on this election";
	private static final String MESSAGE_NO = "i am not the coordinator my friend";
	private static final String MESSAGE_COORDINATOR = "i am the coordinator and your bully! capitsche!!!";
	private static final String MESSAGE_ARE_YOU_THE_COORDINATOR = "are you the coordinator my friend?";
	
	

	/**
	 * Constructor
	 * set all resources from our transaction services
	 * @param current id
	 */
	public BullyAlgorithm(int id, String resource) {
		processMap.put("http://localhost:4567" + RESOURCE_PATH_2, 5);
		processMap.put("http://localhost:4700" + RESOURCE_PATH_2, 4);
//		processMap.put("http://localhost:4701" + RESOURCE_PATH_2, 3);
//		processMap.put("http://localhost:4702" + RESOURCE_PATH_2, 2);
//		processMap.put("http://localhost:4703" + RESOURCE_PATH_2, 1);
		this.id = id;
		this.resource = resource + RESOURCE_PATH_2;
	}
//================================================================================================
//											GETTER
//================================================================================================
	public boolean getCoordinatorFlag() {
		return coordinator;
	}
	
	public String getCoordinatorUrl() {
		return coordinatorUrl;
	}
//================================================================================================
//											SETTER
//================================================================================================
	public void setCoordinatorUrl(String coordinatorUrl) {
		this.coordinatorUrl = coordinatorUrl;
	}
	
	public void setCoordinatorFlag(boolean coordinator) {
		this.coordinator = coordinator;
	}
//================================================================================================
//									ALGORITHM IMPLEMENTATION	
//================================================================================================
	/**
	 * Method make a election
	 */
	public void holdElection() {
		// response buffer
		List<String> responseList = new ArrayList<String>();
		
		// 1. P sends an ELECTION message to all processes with higher numbers.
		for (Entry<String, Integer> map : processMap.entrySet()) {
			if (id < map.getValue()) {
				String response = io.request(map.getKey(), MESSAGE_ELECTOIN);
				responseList.add(response);								
			}
		}
		
		// 2. If no one responds, P wins the election and becomes coordinator.
		if ( !responseList.contains(MESSAGE_OK) ) {
			// we was the new coordinator
			setCoordinatorFlag(true);
			setCoordinatorUrl(resource);
			setToCoordinatorToAllBullies();			
			return;
		}
		setCoordinatorFlag(false);
		// 3. If one of the higher-ups answers, it takes over. P's job is done.
	}
	
	/**
	 * Send a answer(OK) back and do a new elections with higher id's
	 * Start the bully service
	 * @param url
	 */
	public void conintueElection(String url) {
		
		System.out.println("start bully service: " + url);
		
		post(url, (req, res) -> {
			
			String inputMessage = req.body();
			
			System.out.println("input message from client: " + inputMessage);
			
			// we do a election
			if ( BullyAlgorithm.MESSAGE_ELECTOIN.compareTo(inputMessage) == 0 ) {				
				new Thread() {
					public void run() {
						holdElection();
					}
				};				
				return BullyAlgorithm.MESSAGE_OK;
			} 
			
			// we have a new coordinator
			if ( BullyAlgorithm.MESSAGE_COORDINATOR.compareTo(inputMessage) == 0 ) {
				setCoordinatorFlag(false);
				setCoordinatorUrl(req.uri());
				return "we have a new coordinator: " + req.uri();
			}
			
			// checked if the coordinator service was online
			if ( BullyAlgorithm.MESSAGE_ALIVE.compareTo(inputMessage) == 0 ) {
				return BullyAlgorithm.MESSAGE_YES_I_ALIVE;
			}			
			
			if ( BullyAlgorithm.MESSAGE_ARE_YOU_THE_COORDINATOR.compareTo(inputMessage) == 0 ) {
				if ( getCoordinatorFlag() ) {
					return BullyAlgorithm.MESSAGE_COORDINATOR;
				}
				return BullyAlgorithm.MESSAGE_NO;
			}
			
			return "this is not our protocol ....";
		});	
	}
	
	/**
	 * Say all clients, we are the new bully!
	 */
	private void setToCoordinatorToAllBullies() {
		for (Entry<String, Integer> map : processMap.entrySet()) {
			io.request(map.getKey(), BullyAlgorithm.MESSAGE_COORDINATOR);
		}
	}
	
}
