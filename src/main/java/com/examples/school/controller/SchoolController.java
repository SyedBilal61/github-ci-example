package com.examples.school.controller;

import com.examples.school.model.Student;
import com.examples.school.repository.StudentRepository;
import com.examples.school.view.StudentView;

public class SchoolController {
    private StudentView studentView;              // the view (for showing info to user)
    private StudentRepository studentRepository;  // the repository (for accessing data)

    // Constructor — connects the controller with the view and the repository
    public SchoolController(StudentView studentView, StudentRepository studentRepository) {
        this.studentView = studentView;
        this.studentRepository = studentRepository;
    }

    // 1️⃣ Show all students
    public void allStudents() {
        studentView.showAllStudents(studentRepository.findAll());
    }

    // 2️⃣ Add a new student
    public void newStudent(Student student) {
        // Check if student already exists
        Student existingStudent = studentRepository.findById(student.getId());

        if (existingStudent != null) {
            // If found, show an error message in the view and stop
            studentView.showError(
                "Already existing student with id " + student.getId(),
                existingStudent
            );
            return;
        }

        // If not found, save the new student and tell the view to update
        studentRepository.save(student);
        studentView.studentAdded(student);
    }

    // 3️⃣ Delete a student
    public void deleteStudent(Student student) {
        // Check if student exists before deleting
        if (studentRepository.findById(student.getId()) == null) {
            studentView.showError(
                "No existing student with id " + student.getId(),
                student
            );
            return;
        }

        // If found, delete the student and tell the view to update
        studentRepository.delete(student.getId());
        studentView.studentRemoved(student);
    }
}
