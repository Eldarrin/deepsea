package io.ensure.deepsea.models;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject(generateConverter = true)
public class Address {

    private String addressId;
    private Country country;
    private State state;
    private String street;
    private String street2;
    private String city;
    private String zip;

    public Address(Address address) {
        super();
        this.addressId = address.addressId;
        this.country = address.country;
        this.state = address.state;
        this.street = address.street;
        this.street2 = address.street2;
        this.city = address.city;
        this.zip = address.zip;
    }

    public Address(JsonObject json) {
        AddressConverter.fromJson(json, this);
        if (json.containsKey("_id") && !json.containsKey("addressId")) {
            this.addressId = json.getString("_id");
        }
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        AddressConverter.toJson(this, json);
        return json;
    }

    public String toString() { return this.toJson().encodePrettily(); }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public Country getCountryId() {
        return country;
    }

    public void setCountryId(Country country) {
        this.country = country;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreet2() {
        return street2;
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }



}
