package com.examples.school.repository;


import com.examples.school.model.Student;
import java.util.List;

public interface StudentRepository {
    List<Student> findAll();
    Student findById(String id);
    void save(Student student);
    void delete(String id);
}
}
