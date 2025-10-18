package com.examples.school.view;

import com.examples.school.model.Student;
import java.util.List;

public interface StudentView {
    void showAllStudents(List<Student> students);   // show list in table or text area
    void showError(String message, Student student); // show error pop-up or message
    void studentAdded(Student student);             // update view after adding
    void studentRemoved(Student student);           // update view after removing
}
