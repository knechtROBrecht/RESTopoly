package serviceRequest;

public class Service {
	public String service;
	public String name;
	public String description;
	public String uri;
	public String status;

	@Override
	public String toString() {
		return "Service{" + "service='" + service + '\'' + ", name='" + name
				+ '\'' + ", description='" + description + '\'' + ", uri='"
				+ uri + '\'' + ", status='" + status + '\'' + '}';
	}
}
