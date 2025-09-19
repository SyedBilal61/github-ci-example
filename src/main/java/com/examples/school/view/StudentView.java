package com.examples.school.view;

import com.examples.school.model.Student;
import java.util.List;

public interface StudentView {
    void showAllStudents(List<Student> students);
    void showError(String message, Student student);
    void studentAdded(Student student);
    void studentRemoved(Student student);
}
