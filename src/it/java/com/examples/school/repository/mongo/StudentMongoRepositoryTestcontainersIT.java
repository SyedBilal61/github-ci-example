package com.examples.school.repository.mongo;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;

import static org.assertj.core.api.Assertions.assertThat;

public class StudentMongoRepositoryTestcontainersIT {

    @SuppressWarnings("rawtypes")
    @ClassRule
    public static final MongoDBContainer mongo =
    		new MongoDBContainer("mongo:4.4.3");

    private MongoClient client;
    private StudentMongoRepository studentRepository;
    private MongoCollection<Document> studentCollection;

    private static final String SCHOOL_DB_NAME = "school";
    private static final String STUDENT_COLLECTION_NAME = "students";

    @Before
    public void setup() {
        // Connect to the Mongo container
        client = new MongoClient(
                new ServerAddress(
                        mongo.getHost(),
                        mongo.getMappedPort(27017)
                )
        );

        // Inject the client into repository
        studentRepository = new StudentMongoRepository(client);

        // Get the collection and clean database
        MongoDatabase database = client.getDatabase(SCHOOL_DB_NAME);
        database.drop();
        studentCollection = database.getCollection(STUDENT_COLLECTION_NAME);
    }

    @After
    public void tearDown() {
        client.close();
    }

    @Test
    public void testCanConnectToContainer() {
        // just to check that we can connect to the container
        assertThat(studentCollection).isNotNull();
    }
 // Helper method to add a student to the database
    private void addTestStudentToDatabase(String id, String name) {
        Document doc = new Document();
        doc.append("id", id);
        doc.append("name", name);
        studentCollection.insertOne(doc);
    }

    // Helper method to create Student object (your model)
    private Student newStudent(String id, String name) {
        return new Student(id, name);
    }

    @Test
    public void testFindAll() {
        addTestStudentToDatabase("1", "test1");
        addTestStudentToDatabase("2", "test2");

        assertThat(studentRepository.findAll())
                .containsExactly(
                        newStudent("1", "test1"),
                        newStudent("2", "test2")
                );
    }

    @Test
    public void testFindById() {
        addTestStudentToDatabase("1", "test1");
        addTestStudentToDatabase("2", "test2");

        assertThat(studentRepository.findById("2"))
                .isEqualTo(newStudent("2", "test2"));
    }

    // Similarly, you can add tests for save() and delete() methods

}
