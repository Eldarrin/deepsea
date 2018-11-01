package io.ensure.deepsea.admin.enrolment.models;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject(generateConverter = true)
public class Device {

	private String manufacturer;
	private String model;
	
	public Device() {
		
	}
	
	public Device(Device device) {
		this.manufacturer = device.manufacturer;
		this.model = device.model;
	}
	
	public Device(JsonObject json) {
		DeviceConverter.fromJson(json, this);
	}

	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		DeviceConverter.toJson(this, json);
		return json;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

}
