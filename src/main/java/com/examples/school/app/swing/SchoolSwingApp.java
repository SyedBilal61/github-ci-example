package com.examples.school.app.swing;

import java.awt.EventQueue;
import java.util.concurrent.Callable;

import com.examples.school.controller.SchoolController;
import com.examples.school.repository.mongo.StudentMongoRepository;
import com.examples.school.view.swing.StudentSwingView;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(mixinStandardHelpOptions = true)
public class SchoolSwingApp implements Callable<Void> {

    // Command-line options with default values
    @Option(names = {"--mongo-host"}, description = "MongoDB host address")
    private String mongoHost = "localhost";

    @Option(names = {"--mongo-port"}, description = "MongoDB host port")
    private int mongoPort = 27017;

    @Option(names = {"--db-name"}, description = "Database name")
    private String databaseName = "school";

    @Option(names = {"--db-collection"}, description = "Collection name")
    private String collectionName = "students";

    public static void main(String[] args) {
        // Picocli reads command-line args and calls 'call()'
        new CommandLine(new SchoolSwingApp()).execute(args);
    }

    @Override
    public Void call() throws Exception {
        EventQueue.invokeLater(() -> {
            try {
                // Connect to MongoDB using arguments
                String connectionString = "mongodb://" + mongoHost + ":" + mongoPort;
                MongoClient client = MongoClients.create(connectionString);

                // Initialize repository, view, and controller
                StudentMongoRepository studentRepository =
                        new StudentMongoRepository(client, databaseName, collectionName);

                StudentSwingView studentView = new StudentSwingView();

                SchoolController schoolController =
                        new SchoolController(studentView, studentRepository);

                // Wire view and controller
                studentView.setSchoolController(schoolController);
                studentView.setVisible(true);

                // Load all students at startup
                schoolController.allStudents();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return null;
    }
}
