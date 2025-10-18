package com.examples.school.view.swing;
import java.util.Arrays;
import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.examples.school.view.swing.StudentSwingView.Student;
import java.util.Arrays;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(GUITestRunner.class)
public class StudentSwingViewTest extends AssertJSwingJUnitTestCase {

    private FrameFixture window;
    private StudentSwingView studentSwingView;

    @Override
    protected void onSetUp() {
        // create the GUI safely on the Event Dispatch Thread
        studentSwingView = GuiActionRunner.execute(() -> new StudentSwingView());

        // wrap it in a FrameFixture so we can simulate user input
        window = new FrameFixture(robot(), studentSwingView);

        // show the frame so tests can interact with it
        window.show();
    }

    @Test
    public void test() {
        // just to check the setup works
        // when you run this, the StudentSwingView window should appear briefly and close
    }
    @Test
    @GUITest
    public void testControlsInitialStates() {
        window.label(JLabelMatcher.withText("id"));                      // the window has a label "id"
        window.textBox("idTextBox").requireEnabled();                    // the ID text box exists and is enabled
        window.label(JLabelMatcher.withText("name"));                    // the window has a label "name"
        window.textBox("nameTextBox").requireEnabled();                  // the Name text box exists and is enabled
        window.button(JButtonMatcher.withText("Add")).requireDisabled(); // the Add button exists but is disabled at the start
        window.list("studentList");                                      // the student list exists
        window.button(JButtonMatcher.withText("Delete Selected")).requireDisabled(); // the DeleteSelected button exists but is disabled at the start
        window.label("errorMessageLabel").requireText("");               // the error message label exists and is empty
    }
    @Test
    public void testWhenIdAndNameAreNonEmptyThenAddButtonShouldBeEnabled() {
        window.textBox("idTextBox").enterText("1");
        window.textBox("nameTextBox").enterText("test");
        window.button(JButtonMatcher.withText("Add")).requireEnabled();
    }

    @Test
    public void testWhenEitherIdOrNameAreBlankThenAddButtonShouldBeDisabled() {
        JTextComponentFixture idTextBox = window.textBox("idTextBox");
        JTextComponentFixture nameTextBox = window.textBox("nameTextBox");

        idTextBox.enterText("1");
        nameTextBox.enterText(" ");
        window.button(JButtonMatcher.withText("Add")).requireDisabled();

        idTextBox.setText(" ");
        nameTextBox.setText(" ");

        idTextBox.enterText(" ");
        nameTextBox.enterText("test");
        window.button(JButtonMatcher.withText("Add")).requireDisabled();
    }
   
    @Test @GUITest
    public void testShowAllStudentsShouldAddStudentDescriptionsToTheList() {
        Student student1 = new Student("1", "test1");
        Student student2 = new Student("2", "test2");

        GuiActionRunner.execute(() ->
            studentSwingView.showAllStudents(Arrays.asList(student1, student2))
        );

        String[] listContents = window.list().contents();
        assertThat(listContents)
            .containsExactly(student1.toString(), student2.toString());
    }

    @Test
    public void testShowErrorShouldShowTheMessageInTheErrorLabel() {
        Student student = new Student("1", "test1");
        GuiActionRunner.execute(() -> 
            studentSwingView.showError("errormessage", student)
        );
        window.label("errorMessageLabel")
              .requireText("errormessage:" + student);
    }
    
    
    
    
}
