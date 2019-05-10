package io.ensure.deepsea.ai.impl;

import grakn.client.GraknClient;
import grakn.core.concept.answer.ConceptMap;
import graql.lang.Graql;
import graql.lang.query.GraqlDefine;
import graql.lang.query.GraqlGet;
import graql.lang.query.GraqlInsert;
import io.ensure.deepsea.ai.AIService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.stream.Stream;

import static graql.lang.Graql.type;
import static graql.lang.Graql.var;

public class GraknAIServiceImpl implements AIService {

    private String graknServer;
    private String graknKeySpace;
    private static final int GRAKN_PORT = 48555;

    private Logger log = LoggerFactory.getLogger(getClass());

    public GraknAIServiceImpl() {
        graknServer = "localhost";
    }

    public GraknAIServiceImpl(Vertx vertx, JsonObject config) {
        graknServer = config.getString("host");
        graknKeySpace = config.getString("keyspace");
    }

    @Override
    public AIService initializePersistence(Handler<AsyncResult<Void>> resultHandler) {
        // check if keyspace exists
        GraqlDefine defEnrolment = Graql.define(
                type("start-date").sub("attribute").datatype("date"),
                type("title").sub("attribute").datatype("string"),
                type("email").sub("attribute").datatype("string"),
                type("terms-agreed").sub("attribute").datatype("boolean"),
                type("date-of-birth").sub("attribute").datatype("date"),
                type("name").sub("attribute").datatype("string"),
                type("premium").sub("attribute").datatype("double"),
                type("tax").sub("attribute").datatype("double"),
                type("policytype").sub("attribute").datatype("string"),
                type("client").sub("attribute").datatype("string"),
                type("personId").sub("attribute").datatype("string"),
                type("policyId").sub("attribute").datatype("string"),
                type("deviceId").sub("attribute").datatype("string"),
                type("make").sub("attribute").datatype("string"),
                type("model").sub("attribute").datatype("string"),

                type("person").sub("entity").plays("policy-owner")
                    .key("personId")
                    .has("title")
                    .has("name")
                    .has("email")
                    .has("date-of-birth"),

                type("policy").sub("entity").plays("owned-policy").plays("device-insurer")
                    .key("policyId")
                    .has("start-date")
                    .has("terms-agreed")
                    .has("premium")
                    .has("tax")
                    .has("policytype")
                    .has("client"),

                type("device").sub("entity").plays("insured-device")
                    .key("deviceId")
                    .has("make")
                    .has("model"),

                type("policy-ownership").sub("relation")
                    .relates("policy-owner")
                    .relates("owned-policy"),

                type("device-insured").sub("relation")
                    .relates("insured-device")
                    .relates("device-insurer")
                );

        GraknClient client = new GraknClient(graknServer + ":" + GRAKN_PORT);
        GraknClient.Session session = client.session(graknKeySpace);

        GraknClient.Transaction writeTransaction = session.transaction().write();

        writeTransaction.execute(defEnrolment);

        writeTransaction.commit();

        session.close();
        client.close();

        return this;
    }

    @Override
    public AIService addEnrolment(JsonObject enrolment, Handler<AsyncResult<JsonObject>> resultHandler) {

        GraknClient client = new GraknClient(graknServer + ":" + GRAKN_PORT);
        GraknClient.Session session = client.session(graknKeySpace);

        GraknClient.Transaction writeTransaction = session.transaction().write();

        writeTransaction.execute(buildPerson(enrolment));

        writeTransaction.execute(buildPolicy(enrolment));

        writeTransaction.execute(buildDevice(enrolment));

        writeTransaction.execute(buildPersonPolicy(enrolment));

        writeTransaction.execute(buildDevicePolicy(enrolment));

        writeTransaction.commit();

        session.close();
        client.close();
        return this;
    }

    private GraqlInsert buildPerson(JsonObject enrolment) {
        LocalDateTime dateOfBirth = LocalDateTime.ofInstant(enrolment.getInstant("dateOfBirth"), ZoneOffset.UTC);

        GraqlInsert insertQuery = Graql.insert(var("x")
                .isa("person")
                .has("personId", enrolment.getString("enrolmentId").replaceAll("enrolment-", "person-"))
                .has("email", enrolment.getString("email"))
                .has("title", enrolment.getString("title"))
                .has("name", enrolment.getString("firstName") + " " + enrolment.getString("lastName"))
                .has("date-of-birth", dateOfBirth)
        );

        return insertQuery;
    }

    private GraqlInsert buildPolicy(JsonObject enrolment) {
        LocalDateTime startDate = LocalDateTime.ofInstant(enrolment.getInstant("startDate"), ZoneOffset.UTC);

        GraqlInsert insertPolicy = Graql.insert(var("y")
                .isa("policy")
                .has("policyId", enrolment.getString("enrolmentId").replaceAll("enrolment-", "policy-"))
                .has("start-date", startDate)
                .has("terms-agreed", enrolment.getBoolean("agreeTerms"))
                .has("premium", enrolment.getDouble("grossPremium"))
                .has("tax", enrolment.getDouble("ipt"))
                .has("policytype", enrolment.getString("productId"))
                .has("client", enrolment.getString("clientId"))
        );

        return insertPolicy;
    }

    private GraqlInsert buildDevice(JsonObject enrolment) {
        GraqlInsert insertDevice = Graql.insert(var("z")
                .isa("device")
                .has("deviceId", enrolment.getString("enrolmentId").replaceAll("enrolment-", "device-"))
                .has("make", enrolment.getJsonArray("devices").getJsonObject(0).getString("manufacturer"))
                .has("model", enrolment.getJsonArray("devices").getJsonObject(0).getString("model"))
        );

        return insertDevice;
    }

    private GraqlInsert buildPersonPolicy(JsonObject enrolment) {
        GraqlInsert insertPolicyOwner = Graql.match(
                var("per").isa("person").has("personId", enrolment.getString("enrolmentId").replaceAll("enrolment-", "person-")),
                var("pol").isa("policy").has("policyId", enrolment.getString("enrolmentId").replaceAll("enrolment-", "policy-"))
        ).insert(
                var("owner").isa("policy-ownership")
                        .rel("policy-owner", "per")
                        .rel("owned-policy", "pol")
        );
        return  insertPolicyOwner;
    }

    private GraqlInsert buildDevicePolicy(JsonObject enrolment) {

        GraqlInsert insertDeviceInsurer = Graql.match(
                var("pol").isa("policy").has("policyId", enrolment.getString("enrolmentId").replaceAll("enrolment-", "policy-")),
                var("dev").isa("device").has("deviceId", enrolment.getString("enrolmentId").replaceAll("enrolment-", "device-"))
        ).insert(
                var("insurer").isa("device-insured")
                        .rel("insured-device", "dev")
                        .rel("device-insurer", "pol")
        );
        return insertDeviceInsurer;
    }

    @Override
    public AIService getEnrolmentInfo(Handler<AsyncResult<JsonObject>> resultHandler) {
        Future<JsonObject> future = Future.future();

        GraknClient client = new GraknClient(graknServer + ":" + GRAKN_PORT);
        GraknClient.Session session = client.session(graknKeySpace);

        JsonArray json = new JsonArray();

        GraknClient.Transaction readTransaction = session.transaction().read();

        Stream<ConceptMap> answers = readTransaction.stream(getRelQuery());

        answers.forEach(answer -> {
            JsonObject j = new JsonObject().put("holder", answer.get("holder").asAttribute().value().toString());
            if (answer.get("started").asAttribute().value() instanceof LocalDateTime){
                LocalDateTime startDate = (LocalDateTime) answer.get("started").asAttribute().value();
                j.put("start-date", startDate.toInstant(ZoneOffset.UTC));
            } else {
                log.error("$$$ NOT A DATE???");
                log.error(answer.get("started").asAttribute().type().toString());
            }
            j.put("model", answer.get("devmodel").asAttribute().value().toString());

            json.add(j);
        });

        future.setHandler(resultHandler).complete(new JsonObject().put("person_policy", json));

        readTransaction.close();

        session.close();
        client.close();
        return this;
    }

    private GraqlGet getSimpleQuery() {
        return Graql.match(var("p").isa("policy")).get().limit(10);
    }

    private GraqlGet getRels() {
        return Graql.match(
          var("a").isa("policy-ownership")
        ).get();

    }

    private GraqlGet getCompute() {
        return Graql.compute().count().asGet();
    }

    private GraqlGet getRelQuery() {
        return Graql.match(
                var("per").isa("person").has("name", var("holder")),
                var("pol").isa("policy").has("start-date", var("started")),
                var("dev").isa("device").has("model", var("devmodel")),
                var("owner").isa("policy-ownership")
                        .rel("policy-owner", "per")
                        .rel("owned-policy", "pol"),
                var("devpol").isa("device-insured")
                        .rel("insured-device", "dev")
                        .rel("device-insurer", "pol")
        ).get("holder", "started", "devmodel");
    }
}
