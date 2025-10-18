package com.examples.school.repository.mongo;

import com.examples.school.model.Student;
import com.examples.school.repository.StudentRepository;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.List;
import java.util.ArrayList;

public class StudentMongoRepository implements StudentRepository {

    private final MongoCollection<Document> collection;

    public StudentMongoRepository(MongoClient client, String databaseName, String collectionName) {
        collection = client
            .getDatabase(databaseName)
            .getCollection(collectionName);
    }

    @Override
    public List<Student> findAll() {
        List<Student> students = new ArrayList<>();
        for (Document doc : collection.find()) {
            students.add(new Student(doc.getString("id"), doc.getString("name")));
        }
        return students; // returns empty list if collection is empty
    }

    @Override
    public Student findById(String id) {
        Document doc = collection.find(new Document("id", id)).first();
        return doc != null ? new Student(doc.getString("id"), doc.getString("name")) : null;
    }

    @Override
    public void save(Student student) {
        Document doc = new Document("id", student.getId())
                           .append("name", student.getName());
        collection.insertOne(doc);
    }

    @Override
    public void delete(String id) {
        collection.deleteOne(new Document("id", id));
    }
}
