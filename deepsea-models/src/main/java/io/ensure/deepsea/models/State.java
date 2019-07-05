package io.ensure.deepsea.models;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject(generateConverter = true)
public class State {

    private String stateId;
    private Country country;
    private String name;
    private String code;

    private State(State state) {
        super();
        this.stateId = state.stateId;
        this.country = state.country;
        this.name = state.name;
        this.code = state.code;
    }

    public State(JsonObject json) {
        StateConverter.fromJson(json, this);
        if (json.containsKey("_id") && !json.containsKey("stateId")) {
            this.stateId = json.getString("_id");
        }
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        StateConverter.toJson(this, json);
        return json;
    }

    public String toString() { return this.toJson().encodePrettily(); }

    public String getStateId() {
        return stateId;
    }

    public void setStateId(String stateId) {
        this.stateId = stateId;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
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
}
