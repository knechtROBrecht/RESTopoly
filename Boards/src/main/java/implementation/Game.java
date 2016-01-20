package implementation;

public class Game {

	private String uri;
    private String gameID;
    private Components components;
    
    public String getGameid() {
        return gameID;
    }

    public void setGameid(String gameid) {
        this.gameID = gameid;
    }

    public Components getComponents() {
        return components;
    }

    public void setComponents(Components components) {
        this.components = components;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
