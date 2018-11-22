package io.ensure.deepsea.admin.enrolment.models;

import java.text.ParseException;
import java.time.Instant;
import java.util.List;

import io.ensure.deepsea.common.helper.ISO8601DateParser;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject(generateConverter = true)
public class Enrolment {

	private String enrolmentId;
	private String clientId;
	private String productId;
	private String title;
	private String firstName;
	private String middleNames;
	private String lastName;
	private String email;
	private Instant dateOfBirth;
	private boolean termsAgreed;
	private double grossPremium = 0.0d;
	private double ipt = 0.0d;
	private Instant startDate;
	private List<Device> devices;
	
	public Enrolment() {
		
	}
	
	public Enrolment(Enrolment enrolment) {
		this.enrolmentId = enrolment.enrolmentId;
		this.clientId = enrolment.clientId;
		this.title = enrolment.title;
		this.firstName = enrolment.firstName;
		this.middleNames = enrolment.middleNames;
		this.lastName = enrolment.lastName;
		this.email = enrolment.email;
		this.dateOfBirth = enrolment.dateOfBirth;
		this.termsAgreed = enrolment.termsAgreed;
		this.grossPremium = enrolment.grossPremium;
		this.ipt = enrolment.ipt;
		this.startDate = enrolment.startDate;
		this.productId = enrolment.productId;
		this.devices = enrolment.devices;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Instant getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Instant dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public boolean isAgreeTerms() {
		return termsAgreed;
	}

	public void setAgreeTerms(boolean agreeTerms) {
		this.termsAgreed = agreeTerms;
	}

	public String getEnrolmentId() {
		return enrolmentId;
	}

	public void setEnrolmentId(String enrolmentId) {
		this.enrolmentId = enrolmentId;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleNames() {
		return middleNames;
	}

	public void setMiddleNames(String middleNames) {
		this.middleNames = middleNames;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public double getGrossPremium() {
		return grossPremium;
	}

	public void setGrossPremium(double grossPremium) {
		this.grossPremium = grossPremium;
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
	
	public Enrolment(JsonObject json) {
		EnrolmentConverter.fromJson(json, this);
		if (json.getValue("enrolmentId") instanceof Integer) {
			this.enrolmentId = "enrolment-" + json.getInteger("enrolmentId").toString();
		}
		try {
			this.startDate = ISO8601DateParser.parse(
					json.getString("startDate")).toInstant();
		} catch (ParseException pe) {
			// zero the dates if an error
			
		}
	}

	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		EnrolmentConverter.toJson(this, json);
		json.put("startDate", this.startDate);
		return json;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		
		Enrolment enrolment = (Enrolment) o;

		return enrolmentId == enrolment.enrolmentId && clientId.equals(enrolment.clientId);
	}

	@Override
	public int hashCode() {
		int result = enrolmentId.hashCode();
		result = 38 * result + clientId.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return this.toJson().encodePrettily();
	}

	public List<Device> getDevices() {
		return devices;
	}

	public void setDevices(List<Device> devices) {
		this.devices = devices;
	}

}
