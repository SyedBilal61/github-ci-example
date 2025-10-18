package com.examples.school.bdd;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/bdd/resources", // path to your .feature files
    glue = "com.examples.school.bdd.steps",
    monochrome = true
)
public class SchoolSwingAppBDD {

    @BeforeClass
    public static void setUpOnce() {
        // Ensures all Swing component access is done in EDT
        FailOnThreadViolationRepaintManager.install();
    }
}
