package io.ensure.deepsea.ai;

import grakn.client.GraknClient;
import graql.lang.Graql;
import static graql.lang.Graql.*;
import graql.lang.query.GraqlGet;
import graql.lang.query.GraqlInsert;
import grakn.core.concept.answer.ConceptMap;

import java.util.List;
import java.util.stream.Stream;


public class Example {
    public static void main(String[] args) {
        GraknClient client = new GraknClient("localhost:48555");
        GraknClient.Session session = client.session("social_network");

        // Insert a person using a WRITE transaction
        GraknClient.Transaction writeTransaction = session.transaction().write();
        GraqlInsert insertQuery = Graql.insert(var("x").isa("person").has("email", "x@email.com"));
        List<ConceptMap> insertedId = writeTransaction.execute(insertQuery);
        System.out.println("Inserted a person with ID: " + insertedId.get(0).get("x").id());
        // to persist changes, a write transaction must always be committed (closed)
        writeTransaction.commit();

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
}
