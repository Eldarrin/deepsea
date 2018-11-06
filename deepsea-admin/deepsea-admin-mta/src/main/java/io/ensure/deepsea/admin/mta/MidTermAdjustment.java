package io.ensure.deepsea.admin.mta;

import java.text.ParseException;
import java.time.Instant;

import io.ensure.deepsea.common.helper.ISO8601DateParser;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject(generateConverter = true)
public class MidTermAdjustment {

	private String mtaId;
	private MTAEvent mtaEvent;
	private Instant eventDate;
	private String policyId;
	private String oldProductId;
	private String newproductId;
	private String oldRiskId;
	private String newRiskId;
	private double oldCoverage;
	private double newCoverage;

	public MidTermAdjustment() {
		
	}
	
	public MidTermAdjustment(MidTermAdjustment midTermAdjustment) {
		super();
		this.mtaId = midTermAdjustment.mtaId;
		this.mtaEvent = midTermAdjustment.mtaEvent;
		this.eventDate = midTermAdjustment.eventDate;
		this.policyId = midTermAdjustment.policyId;
		this.oldProductId = midTermAdjustment.oldProductId;
		this.newproductId = midTermAdjustment.newproductId;
		this.oldRiskId = midTermAdjustment.oldRiskId;
		this.newRiskId = midTermAdjustment.newRiskId;
		this.oldCoverage = midTermAdjustment.oldCoverage;
		this.newCoverage = midTermAdjustment.newCoverage;
		
	}
	
	public MidTermAdjustment(JsonObject json) {
		MidTermAdjustmentConverter.fromJson(json, this);
		try {
			this.eventDate = ISO8601DateParser.parse(
					json.getString("eventDate")).toInstant();
		} catch (ParseException pe) {
			// zero the dates if an error
		}
	}

	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		MidTermAdjustmentConverter.toJson(this, json);
		json.put("eventDate", this.eventDate);
		return json;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		MidTermAdjustment MidTermAdjustment = (MidTermAdjustment) o;

		return mtaId == MidTermAdjustment.mtaId && policyId.equals(MidTermAdjustment.policyId);
	}

	@Override
	public int hashCode() {
		int result = mtaId.hashCode();
		result = 31 * result + policyId.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return this.toJson().encodePrettily();
	}
	
	public String getMtaId() {
		return mtaId;
	}

	public void setMtaId(String mtaId) {
		this.mtaId = mtaId;
	}

	public MTAEvent getMtaEvent() {
		return mtaEvent;
	}

	public void setMtaEvent(MTAEvent mtaEvent) {
		this.mtaEvent = mtaEvent;
	}

	public Instant getEventDate() {
		return eventDate;
	}

	public void setEventDate(Instant eventDate) {
		this.eventDate = eventDate;
	}

	public String getPolicyId() {
		return policyId;
	}

	public void setPolicyId(String policyId) {
		this.policyId = policyId;
	}

	public String getOldProductId() {
		return oldProductId;
	}

	public void setOldProductId(String oldProductId) {
		this.oldProductId = oldProductId;
	}

	public String getNewproductId() {
		return newproductId;
	}

	public void setNewproductId(String newproductId) {
		this.newproductId = newproductId;
	}

	public String getOldRiskId() {
		return oldRiskId;
	}

	public void setOldRiskId(String oldRiskId) {
		this.oldRiskId = oldRiskId;
	}

	public String getNewRiskId() {
		return newRiskId;
	}

	public void setNewRiskId(String newRiskId) {
		this.newRiskId = newRiskId;
	}

	public double getOldCoverage() {
		return oldCoverage;
	}

	public void setOldCoverage(double oldCoverage) {
		this.oldCoverage = oldCoverage;
	}

	public double getNewCoverage() {
		return newCoverage;
	}

	public void setNewCoverage(double newCoverage) {
		this.newCoverage = newCoverage;
	}


}
