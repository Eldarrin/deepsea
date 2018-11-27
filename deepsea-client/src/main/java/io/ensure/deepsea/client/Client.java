package io.ensure.deepsea.client;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject(generateConverter = true)
public class Client {
	
	private String clientId;
	private String clientName;
	
	public Client(Client client) {
		super();
		this.clientId = client.clientId;
		this.clientName = client.clientName;
	}
	
	public String getClientId() {
		return clientId;
	}
	
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	
	public String getClientName() {
		return clientName;
	}
	
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	
	public Client(JsonObject json) {
		ClientConverter.fromJson(json, this);
		if (json.containsKey("_id") && !json.containsKey("clientId")) {
			this.clientId = json.getString("_id");
		}
	}

	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		ClientConverter.toJson(this, json);
		return json;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Client client = (Client) o;

		return clientName.equals(client.clientName) && clientId.equals(client.clientId);
	}

	@Override
	public int hashCode() {
		int result = clientId.hashCode();
		result = 31 * result + clientName.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return this.toJson().encodePrettily();
	}

}
