package com.examples.repository;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.*;
import org.testcontainers.containers.GenericContainer;

import static org.junit.Assert.*;

public class StudentMongoRepositoryTestcontainersIT {

    // GenericContainer will start a mongo container from Docker Hub
    private static GenericContainer<?> mongo =
            new GenericContainer<>("mongo:4.4")
                    .withExposedPorts(27017);

    @BeforeClass
    public static void startContainer() {
        // Start the container before tests
        mongo.start();
    }

    @AfterClass
    public static void stopContainer() {
        // Stop and remove the container after tests
        mongo.stop();
    }

    @Test
    public void testInsertAndFind() {
        // Get how to reach the MongoDB container from the host
        String host = mongo.getHost();                       // usually "localhost"
        Integer port = mongo.getMappedPort(27017);           // mapped port on the host

        // Create a MongoClient connecting to the temporary MongoDB
        MongoClient client = new MongoClient(host, port);

        // Use a test database and collection
        MongoDatabase db = client.getDatabase("testdb");
        MongoCollection<Document> coll = db.getCollection("students");

        // Clean collection (ensures repeatable runs)
        coll.drop();

        // Insert a document
        Document doc = new Document("name", "Bilal").append("age", 25);
        coll.insertOne(doc);

        // Assert it was inserted
        long count = coll.countDocuments();
        assertEquals(1L, count);

        Document first = coll.find().first();
        assertNotNull(first);
        assertEquals("Bilal", first.getString("name"));

        client.close();
    }
}
