package io.ensure.deepsea.ai.tests;

import grakn.client.GraknClient;
import graql.lang.Graql;
import static graql.lang.Graql.*;
import graql.lang.query.GraqlGet;
import graql.lang.query.GraqlInsert;
import grakn.core.concept.answer.ConceptMap;
import io.ensure.deepsea.admin.enrolment.models.Enrolment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Stream;


public class Loader {

    public void loadEnrolment(Enrolment enrolment) {
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


        /*
        GraknClient client = new GraknClient("localhost:48555");
        GraknClient.Session session = client.session("enrolment");

        // Insert a person using a WRITE transaction
        GraknClient.Transaction writePersonTransaction = session.transaction().write();

        LocalDateTime dateOfBirth = LocalDate.parse("1970-01-01").atStartOfDay();

       //###

        List<ConceptMap> insertedPerson = writePersonTransaction.execute(insertQuery);
        String personId = insertedPerson.get(0).get("x").id().getValue();
        // to persist changes, a write transaction must always be committed (closed)
        writePersonTransaction.commit();

        GraknClient.Transaction writePolicyTransaction = session.transaction().write();

        LocalDateTime startDate = LocalDate.parse("2019-05-08").atStartOfDay();

//###

        List<ConceptMap> insertedPolicy = writePolicyTransaction.execute(insertPolicy);
        String policyId = insertedPolicy.get(0).get("y").id().getValue();
        // to persist changes, a write transaction must always be committed (closed)
        writePolicyTransaction.commit();

        GraknClient.Transaction writeDeviceTransaction = session.transaction().write();

//###

        List<ConceptMap> insertedDevice = writeDeviceTransaction.execute(insertDevice);
        String deviceId = insertedDevice.get(0).get("z").id().getValue();

        writeDeviceTransaction.commit();

        GraknClient.Transaction writeOwnerTransaction = session.transaction().write();


        List<ConceptMap> insertedRelId = writeOwnerTransaction.execute(insertPolicyOwner);

        writeOwnerTransaction.commit();

        GraknClient.Transaction writeInsuredDeviceTransaction = session.transaction().write();

        GraqlInsert insertDeviceInsurer = Graql.match(
                var("pol").isa("policy").has("policyId", "policy3"),
                var("dev").isa("device").has("deviceId", "device3")
        ).insert(
                var("insurer").isa("device-insured")
                .rel("insured-device", "dev")
                .rel("device-insurer", "pol")
        );

        List<ConceptMap> insertDevRel = writeInsuredDeviceTransaction.execute(insertDeviceInsurer);

        writeInsuredDeviceTransaction.commit();

        // Read the person using a READ only transaction
        GraknClient.Transaction readTransaction = session.transaction().read();
        GraqlGet getQuery = Graql.match(var("p").isa("person")).get().limit(10);
        Stream<ConceptMap> answers = readTransaction.stream(getQuery);
        answers.forEach(answer -> System.out.println(answer.get("p").id()));

        // transactions, sessions and clients must always be closed
        readTransaction.close();
        session.close();
        client.close();
    }
    */
}
