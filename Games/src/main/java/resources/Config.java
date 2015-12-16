package resources;

public class Config {
	
	public enum Service {
		DICE,
		GAME,
		BOARD,
		BANK,
		BROKER,
		PLAYER,
		EVENT
	}
	
	public static String[] URIs = {
		"https://vs-docker.informatik.haw-hamburg.de/ports/17470/dice",
		"https://vs-docker.informatik.haw-hamburg.de/ports/17471/games",
		"https://vs-docker.informatik.haw-hamburg.de/ports/17472/boards",
		"https://vs-docker.informatik.haw-hamburg.de/ports/17473/banks",
		"https://vs-docker.informatik.haw-hamburg.de/ports/17474/broker",
		"https://vs-docker.informatik.haw-hamburg.de/ports/17475/player",
		"https://vs-docker.informatik.haw-hamburg.de/ports/17476/events"		
	};

	static boolean initialized = false;
	
	private static void initialize() {
		if (initialized)
			return;
		
		// call yellowpages and fill URIs here
		// TODO ...
		
		initialized = true;
	}
	
	private static String getServiceURI(Service service) {
		initialize();
		return URIs[service.ordinal()]; 
	}
	
	public static String getBoardURI(String s) {
		return getServiceURI(Service.BOARD) + s;
	}
	
	public static String getBoardURI() {
		return getBoardURI("");
	}
}
