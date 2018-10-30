package io.ensure.deepsea.actuarial.bordereau;

import java.text.ParseException;

import java.time.Instant;

import io.ensure.deepsea.common.helper.ISO8601DateParser;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject(generateConverter = true)
public class BordereauLine {

	private String bordereauLineId;
	private String clientId;
	private String customerName;
	private double value = 0.0d;
	private double ipt = 0.0d;
	private Instant startDate;
	private Instant eventDate;
	private BordereauEvent event;

	public BordereauLine() {

	}

	public BordereauLine(BordereauLine bordereauLine) {
		super();
		this.bordereauLineId = bordereauLine.bordereauLineId;
		this.clientId = bordereauLine.clientId;
		this.customerName = bordereauLine.customerName;
		this.value = bordereauLine.value;
		this.ipt = bordereauLine.ipt;
		this.startDate = bordereauLine.startDate;
		this.eventDate = bordereauLine.eventDate;
		this.event = bordereauLine.event;
	}

	public String getBordereauLineId() {
		return bordereauLineId;
	}

	public void setBordereauLineId(String bordereauLineId) {
		this.bordereauLineId = bordereauLineId;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public double getIpt() {
		return ipt;
	}

	public void setIpt(double ipt) {
		this.ipt = ipt;
	}

	public Instant getStartDate() {
		return startDate;
	}

	public void setStartDate(Instant startDate) {
		this.startDate = startDate;
	}

	public Instant getEventDate() {
		return eventDate;
	}

	public void setEventDate(Instant eventDate) {
		this.eventDate = eventDate;
	}

	public BordereauEvent getEvent() {
		return event;
	}

	public void setEvent(BordereauEvent event) {
		this.event = event;
	}

	public BordereauLine(JsonObject json) {
		BordereauLineConverter.fromJson(json, this);
		try {
			this.startDate = ISO8601DateParser.parse(
					json.getString("startDate")).toInstant();
			this.eventDate = ISO8601DateParser.parse(
					json.getString("eventDate")).toInstant();
		} catch (ParseException pe) {
			// zero the dates if an error
		}
	}

	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		BordereauLineConverter.toJson(this, json);
		json.put("startDate", this.startDate);
		json.put("eventDate", this.eventDate);
		return json;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		BordereauLine bordereauLine = (BordereauLine) o;

		return bordereauLineId.equals(bordereauLine.bordereauLineId) && clientId.equals(bordereauLine.clientId);
	}

	@Override
	public int hashCode() {
		int result = bordereauLineId.hashCode();
		result = 31 * result + clientId.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return this.toJson().encodePrettily();
	}

}
