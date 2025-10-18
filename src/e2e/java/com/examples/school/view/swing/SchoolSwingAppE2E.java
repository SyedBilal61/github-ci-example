package com.examples.school.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

import java.util.regex.Pattern;

import javax.swing.JFrame;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.bson.Document;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.MongoDBContainer;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

@RunWith(GUITestRunner.class)
public class SchoolSwingAppE2E extends AssertJSwingJUnitTestCase {

    // Start MongoDB container for testing
    @ClassRule
    public static final MongoDBContainer mongo = new MongoDBContainer("mongo:4.4.3");

    private static final String DB_NAME = "test-db";
    private static final String COLLECTION_NAME = "test-collection";

    private static final String STUDENT_FIXTURE_1_ID = "1";
    private static final String STUDENT_FIXTURE_1_NAME = "firststudent";
    private static final String STUDENT_FIXTURE_2_ID = "2";
    private static final String STUDENT_FIXTURE_2_NAME = "secondstudent";

    private MongoClient mongoClient;
    private FrameFixture window;

    @Override
    protected void onSetUp() {
        // Connect to Mongo container
        String containerIpAddress = mongo.getHost();
        Integer mappedPort = mongo.getMappedPort(27017);

        mongoClient = new MongoClient(containerIpAddress, mappedPort);

        // Clean database before each test
        MongoDatabase db = mongoClient.getDatabase(DB_NAME);
        db.drop();

        // Insert test data
        addTestStudentToDatabase(STUDENT_FIXTURE_1_ID, STUDENT_FIXTURE_1_NAME);
        addTestStudentToDatabase(STUDENT_FIXTURE_2_ID, STUDENT_FIXTURE_2_NAME);

        // Start Swing app
        application("com.examples.school.app.swing.SchoolSwingApp")
            .withArgs(
                "--mongo-host=" + containerIpAddress,
                "--mongo-port=" + mappedPort.toString(),
                "--db-name=" + DB_NAME,
                "--db-collection=" + COLLECTION_NAME
            )
            .start();

        // Find main GUI window
        window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
            @Override
            protected boolean isMatching(JFrame frame) {
                return "StudentView".equals(frame.getTitle()) && frame.isShowing();
            }
        }).using(robot());
    }

    @Override
    protected void onTearDown() {
        mongoClient.close();
    }

    // ------------------- TESTS -------------------

    @Test
    @GUITest
    public void testOnStartAllDatabaseElementsAreShown() {
        assertThat(window.list().contents())
            .anySatisfy(e -> assertThat(e)
                .contains(STUDENT_FIXTURE_1_ID, STUDENT_FIXTURE_1_NAME))
            .anySatisfy(e -> assertThat(e)
                .contains(STUDENT_FIXTURE_2_ID, STUDENT_FIXTURE_2_NAME));
    }

    @Test
    @GUITest
    public void testAddButtonSuccess() {
        // Simulate typing a new student
        window.textBox("idTextBox").enterText("10");
        window.textBox("nameTextBox").enterText("newstudent");
        window.button(JButtonMatcher.withText("Add")).click();

        // Verify student appears in list
        assertThat(window.list().contents())
            .anySatisfy(e -> assertThat(e).contains("10", "newstudent"));
    }

    @Test
    @GUITest
    public void testAddButtonError() {
        // Try adding a student with an existing ID
        window.textBox("idTextBox").enterText(STUDENT_FIXTURE_1_ID);
        window.textBox("nameTextBox").enterText("newone");
        window.button(JButtonMatcher.withText("Add")).click();

        // Verify error message is shown
        assertThat(window.label("errorMessageLabel").text())
            .contains(STUDENT_FIXTURE_1_ID, STUDENT_FIXTURE_1_NAME);
    }

    @Test
    @GUITest
    public void testDeleteButtonSuccess() {
        // Select student from list
        window.list("studentList")
            .selectItem(Pattern.compile(".*" + STUDENT_FIXTURE_1_NAME + ".*"));

        // Click delete button
        window.button(JButtonMatcher.withText("DeleteSelected")).click();

        // Verify student is removed
        assertThat(window.list().contents())
            .noneMatch(e -> e.contains(STUDENT_FIXTURE_1_NAME));
    }

    @Test
    @GUITest
    public void testDeleteButtonError() {
        // Select the student in the list
        window.list("studentList")
            .selectItem(Pattern.compile(".*" + STUDENT_FIXTURE_1_NAME + ".*"));

        // Manually remove the student from DB (simulate external deletion)
        removeTestStudentFromDatabase(STUDENT_FIXTURE_1_ID);

        // Click delete button
        window.button(JButtonMatcher.withText("DeleteSelected")).click();

        // Verify error is shown
        assertThat(window.label("errorMessageLabel").text())
            .contains(STUDENT_FIXTURE_1_ID, STUDENT_FIXTURE_1_NAME);
    }

    // ------------------- HELPERS -------------------

    private void addTestStudentToDatabase(String id, String name) {
        mongoClient.getDatabase(DB_NAME)
            .getCollection(COLLECTION_NAME)
            .insertOne(new Document()
                .append("id", id)
                .append("name", name));
    }

    private void removeTestStudentFromDatabase(String id) {
        mongoClient
            .getDatabase(DB_NAME)
            .getCollection(COLLECTION_NAME)
            .deleteOne(Filters.eq("id", id));
    }
}
