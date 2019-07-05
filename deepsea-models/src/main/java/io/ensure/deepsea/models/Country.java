package io.ensure.deepsea.models;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject(generateConverter = true)
public class Country {

    private String countryId;
    private String name;
    private String code;
    private String addressFormat;
    private Currency currency;
    private Integer phoneCode;

    public Country(Country country) {
        super();
        this.countryId = country.countryId;
        this.name = country.name;
        this.code = country.code;
        this.addressFormat = country.addressFormat;
        this.currency = country.currency;
        this.phoneCode = country.phoneCode;
    }

    public Country(JsonObject json) {
        CountryConverter.fromJson(json, this);
        if (json.containsKey("_id") && !json.containsKey("countryId")) {
            this.countryId = json.getString("_id");
        }
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        CountryConverter.toJson(this, json);
        return json;
    }

    public String toString() { return this.toJson().encodePrettily(); }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAddressFormat() {
        return addressFormat;
    }

    public void setAddressFormat(String addressFormat) {
        this.addressFormat = addressFormat;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Integer getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(Integer phoneCode) {
        this.phoneCode = phoneCode;
    }



}
