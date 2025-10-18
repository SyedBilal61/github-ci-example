package com.examples.school.repository.mongo;

import static org.assertj.core.api.Assertions.*;

import java.net.InetSocketAddress;

import org.bson.Document;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

public class StudentMongoRepositoryTest {

    private static MongoServer server;
    private static InetSocketAddress serverAddress;

    private MongoClient client;
    private StudentMongoRepository studentRepository;
    private MongoCollection<Document> studentCollection;

    @BeforeClass
    public static void setupServer() {
        // Start in-memory MongoDB server
        server = new MongoServer(new MemoryBackend());
        serverAddress = server.bind(); // random local port
    }

    @AfterClass
    public static void shutdownServer() {
        // Stop the server
        server.shutdown();
    }

    @Before
    public void setup() {
        // Create MongoClient connected to in-memory server
        client = new MongoClient(new ServerAddress(serverAddress));

        // Inject client into repository
        studentRepository = new StudentMongoRepository(client);

        // Get the collection directly for test setup
        studentCollection = client
                .getDatabase(StudentMongoRepository.SCHOOL_DB_NAME)
                .getCollection(StudentMongoRepository.STUDENT_COLLECTION_NAME);

        // Clean database before each test
        studentCollection.drop();
    }

    @After
    public void tearDown() {
        // Close client after each test
        client.close();
    }

    @Test
    public void testFindAllWhenDatabaseIsEmpty() {
        // database is empty, should return empty list
        assertThat(studentRepository.findAll()).isEmpty();
    }
}
