package com.examples.school.view.swing;

import com.examples.school.controller.SchoolController;
import com.examples.school.model.Student;
import com.examples.school.repository.mongo.StudentMongoRepository;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.MongoDBContainer;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(GUITestRunner.class)
public class StudentSwingViewIT {

    @org.junit.ClassRule
    public static final MongoDBContainer mongo = new MongoDBContainer("mongo:4.4.3");

    private MongoClient mongoClient;
    private FrameFixture window;
    private StudentSwingView studentSwingView;
    private SchoolController schoolController;
    private StudentMongoRepository studentRepository;

    @Before
    public void setUp() {
        // Connect to the Testcontainer MongoDB
        mongoClient = new MongoClient(new ServerAddress(
                mongo.getContainerIpAddress(),
                mongo.getFirstMappedPort()
        ));

        // Create repository with MongoClient
        studentRepository = new StudentMongoRepository(mongoClient);

        // Clear database
        for (Student student : studentRepository.findAll()) {
            studentRepository.delete(student.getId());
        }

        // Setup GUI and controller
        window = new FrameFixture(GuiActionRunner.execute(() -> {
            studentSwingView = new StudentSwingView();
            schoolController = new SchoolController(studentSwingView, studentRepository);
            studentSwingView.setVisible(true);
            return studentSwingView;
        }));

        window.show(); // Show frame for testing
    }

    @After
    public void tearDown() {
        if (window != null) {
            window.cleanUp();
        }
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    @Test @GUITest
    public void testAddButtonSuccess() {
        window.textBox("idTextBox").enterText("1");
        window.textBox("nameTextBox").enterText("test");
        window.button(JButtonMatcher.withText("Add")).click();

        assertThat(studentRepository.findById("1")).isEqualTo(new Student("1", "test"));
        assertThat(window.list("studentList").contents())
                .containsExactly(new Student("1", "test").toString());
    }

    @Test @GUITest
    public void testDeleteButtonSuccess() {
        // Add student via controller
        GuiActionRunner.execute(() -> schoolController.newStudent(new Student("99", "existing")));

        // Select student in the list and delete
        window.list("studentList").selectItem(0);
        window.button(JButtonMatcher.withText("Delete Selected")).click();

        assertThat(studentRepository.findById("99")).isNull();
        assertThat(window.list("studentList").contents()).isEmpty();
    }
}
