package serviceRequest;

import org.json.JSONArray;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.GetRequest;

public class Services {

	private static String yellowPageUri = "http://vs-docker.informatik.haw-hamburg.de/ports/8053";

	public static Service getServiceByName(String name) {

		try {
			GetRequest request = Unirest
					.get(yellowPageUri + "/services/of/name/{name}")
					.routeParam("name", name);

			HttpResponse<JsonNode> response = request.asJson();
			JSONArray serviceUris = response.getBody().getObject().getJSONArray("services");
		
			String lastServiceUri = serviceUris.get(serviceUris.length()-1).toString();
			return getService(lastServiceUri);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	private static Service getService(String uri) {
		Service service = null;
		try {
			GetRequest request = Unirest.get(yellowPageUri + uri);
			HttpResponse<String> response = request.asString();
			service = new Gson().fromJson(response.getBody(), Service.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return service;
	}

}
