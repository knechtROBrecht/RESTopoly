package implementation;

public class ServiceDescription {
	
	private String name;
	private String description;
	private String service;
	private String uri;
	
	public ServiceDescription(String name, String description, String service, String uri){
		this.name = name;
		this.description = description;
		this.service = service;
		this.uri = uri;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
	
	

}
