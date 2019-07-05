package io.ensure.deepsea.models;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject(generateConverter = true)
public class Currency {

    private String currencyId;
    private String name;
    private String symbol;
    private double rounding;
    private Integer decimalPlaces;
    private String unitLabel;
    private String subUnitLabel;

    public Currency(Currency currency) {
        super();
        this.currencyId = currency.currencyId;
        this.name = currency.name;
        this.symbol = currency.symbol;
        this.rounding = currency.rounding;
        this.decimalPlaces = currency.decimalPlaces;
        this.unitLabel = currency.unitLabel;
        this.subUnitLabel = currency.subUnitLabel;
    }

    public Currency(JsonObject json) {
        CurrencyConverter.fromJson(json, this);
        if (json.containsKey("_id") && !json.containsKey("currencyId")) {
            this.currencyId = json.getString("_id");
        }
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        CurrencyConverter.toJson(this, json);
        return json;
    }

    public String toString() { return this.toJson().encodePrettily(); }

    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getRounding() {
        return rounding;
    }

    public void setRounding(double rounding) {
        this.rounding = rounding;
    }

    public Integer getDecimalPlaces() {
        return decimalPlaces;
    }

    public void setDecimalPlaces(Integer decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
    }

    public String getUnitLabel() {
        return unitLabel;
    }

    public void setUnitLabel(String unitLabel) {
        this.unitLabel = unitLabel;
    }

    public String getSubUnitLabel() {
        return subUnitLabel;
    }

    public void setSubUnitLabel(String subUnitLabel) {
        this.subUnitLabel = subUnitLabel;
    }
}
