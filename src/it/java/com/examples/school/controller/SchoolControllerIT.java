package com.examples.school.controller;

import com.examples.school.model.Student;
import com.examples.school.repository.StudentMongoRepository;
import com.examples.school.repository.StudentRepository;
import com.examples.school.view.StudentView;
import com.mongodb.MongoClient;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import static org.mockito.Mockito.verify;

public class SchoolControllerIT {

    @Mock
    private StudentView studentView;

    private StudentRepository studentRepository;
    private SchoolController schoolController;
    private AutoCloseable closeable;

    @Before
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        studentRepository = new StudentMongoRepository(new MongoClient("localhost"));

        // Clear the database before each test
        for (Student student : studentRepository.findAll()) {
            studentRepository.delete(student.getId());
        }

        schoolController = new SchoolController(studentView, studentRepository);
    }

    @After
    public void tearDown() throws Exception {
        closeable.close();
    }

    // --- 1️⃣ Test for allStudents() ---
    @Test
    public void testAllStudents() {
        Student student = new Student("1", "test");
        studentRepository.save(student); // insert directly in DB

        schoolController.allStudents(); // call the controller method

        verify(studentView).showAllStudents(Arrays.asList(student));
    }

    // --- 2️⃣ Test for newStudent() ---
    @Test
    public void testNewStudent() {
        Student student = new Student("1", "test");
        schoolController.newStudent(student);

        verify(studentView).studentAdded(student);
    }

    // --- 3️⃣ Test for deleteStudent() ---
    @Test
    public void testDeleteStudent() {
        Student studentToDelete = new Student("1", "test");
        studentRepository.save(studentToDelete); // insert first

        schoolController.deleteStudent(studentToDelete);

        verify(studentView).studentRemoved(studentToDelete);
    }
}
