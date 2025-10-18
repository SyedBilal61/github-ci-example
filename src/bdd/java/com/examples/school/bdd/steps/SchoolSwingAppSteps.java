package com.examples.school.bdd.steps;

import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.core.GenericTypeMatcher;

import org.bson.Document;

import com.mongodb.MongoClient;
import javax.swing.JFrame;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class SchoolSwingAppSteps {

    // MongoDB constants
    private static final String DB_NAME = "test-db";
    private static final String COLLECTION_NAME = "test-collection";
    private MongoClient mongoClient;

    // GUI fixture
    private FrameFixture window;

    // ---------------- MongoDB setup ----------------
    @Before(order = 1)
    public void setUpMongo() {
        mongoClient = new MongoClient();
        mongoClient.getDatabase(DB_NAME).drop(); // start fresh
    }

    @After(order = 1)
    public void tearDownMongo() {
        mongoClient.close();
    }

    // ---------------- GUI teardown ----------------
    @After(order = 2)
    public void tearDownGUI() {
        if (window != null) {
            window.cleanUp(); // safely closes GUI
        }
    }

    @Given("The database contains the students with the following values")
    public void the_database_contains_the_students_with_the_following_values(List<List<String>> values) {
        values.forEach(v -> mongoClient
            .getDatabase(DB_NAME)
            .getCollection(COLLECTION_NAME)
            .insertOne(
                new Document()
                    .append("id", v.get(0))   // first column -> id
                    .append("name", v.get(1)) // second column -> name
            )
        );
    }



    @When("The Student View is shown")
    public void the_Student_View_is_shown() {
        // Start the Swing app
        com.examples.school.app.swing.SchoolSwingApp.main(new String[] {
            "--db-name=" + DB_NAME,
            "--db-collection=" + COLLECTION_NAME
        });

        // Find the JFrame
        window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
            @Override
            protected boolean isMatching(JFrame frame) {
                return "Student View".equals(frame.getTitle()) && frame.isShowing();
            }
        }).using(BasicRobot.robotWithCurrentAwtHierarchy());
    }

    // ---------------- Updated @Then step ----------------
    @Then("The list contains elements with the following values")
    public void the_list_contains_elements_with_the_following_values(List<List<String>> values) {
        values.forEach(v -> 
            assertThat(window.list("studentList").contents())
                .anySatisfy(e -> assertThat(e).contains(v.get(0), v.get(1)))
        );
    }

}
