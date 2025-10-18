package com.examples.school.controller;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import com.examples.school.model.Student;
import com.examples.school.repository.StudentRepository;
import com.examples.school.view.StudentView;

public class SchoolControllerTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentView studentView;

    @InjectMocks
    private SchoolController schoolController;

    private AutoCloseable closeable;

    @Before
    public void setup() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @After
    public void releaseMocks() throws Exception {
        closeable.close();
    }

    // Helper method to create students
    private Student newStudent(String id, String name) {
        return new Student(id, name);
    }

    // Test 1: show all students
    @Test
    public void testAllStudents() {
        List<Student> students = asList(newStudent("1", "test"));
        when(studentRepository.findAll()).thenReturn(students);

        schoolController.allStudents();

        verify(studentView).showAllStudents(students);
    }

    // Test 2: add student when it does NOT exist
    @Test
    public void testNewStudentWhenStudentDoesNotAlreadyExist() {
        Student student = newStudent("1", "test");
        when(studentRepository.findById("1")).thenReturn(null);

        schoolController.newStudent(student);

        InOrder inOrder = inOrder(studentRepository, studentView);
        inOrder.verify(studentRepository).save(student);
        inOrder.verify(studentView).studentAdded(student);
    }

    // Test 3: add student when it ALREADY exists
    @Test
    public void testNewStudentWhenStudentAlreadyExists() {
        Student studentToAdd = newStudent("1", "test");
        Student existingStudent = newStudent("1", "name");
        when(studentRepository.findById("1")).thenReturn(existingStudent);

        schoolController.newStudent(studentToAdd);

        verify(studentView)
                .showError("Alreadyexistingstudentwithid1", existingStudent);
        verifyNoMoreInteractions(ignoreStubs(studentRepository));
    }

    // Test 4: delete student when it exists
    @Test
    public void testDeleteStudentWhenStudentExists() {
        Student studentToDelete = newStudent("1", "test");
        when(studentRepository.findById("1")).thenReturn(studentToDelete);

        schoolController.deleteStudent(studentToDelete);

        InOrder inOrder = inOrder(studentRepository, studentView);
        inOrder.verify(studentRepository).delete("1");
        inOrder.verify(studentView).studentRemoved(studentToDelete);
    }

    // Test 5: delete student when it does NOT exist
    @Test
    public void testDeleteStudentWhenStudentDoesNotExist() {
        Student student = newStudent("1", "test");
        when(studentRepository.findById("1")).thenReturn(null);

        schoolController.deleteStudent(student);

        verify(studentView)
                .showError("Noexistingstudentwithid1", student);
        verifyNoMoreInteractions(ignoreStubs(studentRepository));
    }
}
