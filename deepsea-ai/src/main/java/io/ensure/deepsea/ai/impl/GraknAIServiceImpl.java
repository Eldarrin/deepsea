package io.ensure.deepsea.ai.impl;

import grakn.client.GraknClient;
import graql.lang.Graql;
import graql.lang.query.GraqlInsert;
import io.ensure.deepsea.admin.enrolment.models.Enrolment;
import io.ensure.deepsea.ai.AIService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisOptions;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static graql.lang.Graql.var;

public class GraknAIServiceImpl implements AIService {

    public GraknAIServiceImpl() {

    }

    public GraknAIServiceImpl(Vertx vertx, JsonObject config, RedisOptions rOptions) {

    }

    @Override
    public AIService initializePersistence(Handler<AsyncResult<Void>> resultHandler) {
        return null;
    }

    @Override
    public AIService addEnrolment(Enrolment enrolment, Handler<AsyncResult<Enrolment>> resultHandler) {
        GraknClient client = new GraknClient("localhost:48555");
        GraknClient.Session session = client.session("enrolment");

        GraknClient.Transaction writeTransaction = session.transaction().write();

        writeTransaction.execute(buildPerson(enrolment));

        writeTransaction.commit();

        writeTransaction = session.transaction().write();

        writeTransaction.execute(buildPolicy(enrolment));

        writeTransaction.commit();

        writeTransaction = session.transaction().write();

        writeTransaction.execute(buildDevice(enrolment));

        writeTransaction.commit();

        writeTransaction = session.transaction().write();

        writeTransaction.execute(buildPersonPolicy(enrolment));

        writeTransaction.commit();

        writeTransaction = session.transaction().write();

        writeTransaction.execute(buildDevicePolicy(enrolment));

        writeTransaction.commit();

        session.close();
        client.close();
        return null;
    }

    private GraqlInsert buildPerson(Enrolment enrolment) {
        LocalDateTime dateOfBirth = LocalDateTime.ofInstant(enrolment.getDateOfBirth(), ZoneOffset.UTC);

        GraqlInsert insertQuery = Graql.insert(var("x")
                .isa("person")
                .has("personId", enrolment.getEnrolmentId().replaceAll("enrolment-", "person-"))
                .has("email", enrolment.getEmail())
                .has("title", enrolment.getTitle())
                .has("name", enrolment.getFirstName() + " " + enrolment.getLastName())
                .has("date-of-birth", dateOfBirth)
        );

        return insertQuery;
    }

    private GraqlInsert buildPolicy(Enrolment enrolment) {
        LocalDateTime startDate = LocalDateTime.ofInstant(enrolment.getStartDate(), ZoneOffset.UTC);

        GraqlInsert insertPolicy = Graql.insert(var("y")
                .isa("policy")
                .has("policyId", enrolment.getEnrolmentId().replaceAll("enrolment-", "policy-"))
                .has("start-date", startDate)
                .has("terms-agreed", enrolment.isAgreeTerms())
                .has("premium", enrolment.getGrossPremium())
                .has("tax", enrolment.getIpt())
                .has("policytype", enrolment.getProductId())
                .has("client", enrolment.getClientId())
        );

        return insertPolicy;
    }

    private GraqlInsert buildDevice(Enrolment enrolment) {
        GraqlInsert insertDevice = Graql.insert(var("z")
                .isa("device")
                .has("deviceId", enrolment.getEnrolmentId().replaceAll("enrolment-", "device-"))
                .has("make", enrolment.getDevices().get(0).getManufacturer())
                .has("model", enrolment.getDevices().get(0).getModel())
        );

        return insertDevice;
    }

    private GraqlInsert buildPersonPolicy(Enrolment enrolment) {
        GraqlInsert insertPolicyOwner = Graql.match(
                var("per").isa("person").has("personId", enrolment.getEnrolmentId().replaceAll("enrolment-", "person-")),
                var("pol").isa("policy").has("policyId", enrolment.getEnrolmentId().replaceAll("enrolment-", "policy-"))
        ).insert(
                var("owner").isa("policy-ownership")
                        .rel("policy-owner", "per")
                        .rel("owned-policy", "pol")
        );
        return  insertPolicyOwner;
    }

    private GraqlInsert buildDevicePolicy(Enrolment enrolment) {

        GraqlInsert insertDeviceInsurer = Graql.match(
                var("pol").isa("policy").has("policyId", enrolment.getEnrolmentId().replaceAll("enrolment-", "policy-")),
                var("dev").isa("device").has("deviceId", enrolment.getEnrolmentId().replaceAll("enrolment-", "device-"))
        ).insert(
                var("insurer").isa("device-insured")
                        .rel("insured-device", "dev")
                        .rel("device-insurer", "pol")
        );
        return insertDeviceInsurer;
    }
}
