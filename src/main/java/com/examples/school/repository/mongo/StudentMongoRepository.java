package com.examples.school.repository.mongo;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import com.examples.school.model.Student;
import com.examples.school.repository.StudentRepository;

public class StudentMongoRepository implements StudentRepository {

    public static final String SCHOOL_DB_NAME = "school";
    public static final String STUDENT_COLLECTION_NAME = "student";

    private MongoCollection<Document> studentCollection;

    public StudentMongoRepository(MongoClient client) {
        studentCollection = client
            .getDatabase(SCHOOL_DB_NAME)
            .getCollection(STUDENT_COLLECTION_NAME);
    }

    // Methods like save, findById, delete, findAll will be implemented later
}
