package edu.virginia.sde.reviews;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class LoginSceneController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private DatabaseDriver databaseDriver;

    public void initialize() {
        // Initialize DatabaseDriver instance
        databaseDriver = new DatabaseDriver("your_database_file.db");
    }

    @FXML
    private void handleLoginButton() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {

            if (isValidCredentials(username, password)) {
                navigateToCourseSearchScreen();
            } else {
                errorLabel.setText("Invalid username/password");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            errorLabel.setText("Database error");
        }
    }

    @FXML
    private void handleCreateUserButton() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {

            if (!usernameExists(username) && isPasswordValid(password)) {
                User newUser = new User(username, password);
                databaseDriver.addUser(newUser);
                errorLabel.setText("User created successfully");
            } else {
                errorLabel.setText("Invalid username/password");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            errorLabel.setText("Database error");
        }
    }

    private boolean isValidCredentials(String username, String password) throws SQLException {
        User user = databaseDriver.getUserByUsername(username);
        if (user != null) {
            return user.getPassword().equals(password);
        }
        return false;
    }

    private boolean usernameExists(String username) throws SQLException {
        User user = databaseDriver.getUserByUsername(username);
        return user != null;
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 8;
    }

    private void navigateToCourseSearchScreen() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("course-search.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle("Course Search");
            stage.setScene(scene);
            stage.show();

            // Close the login window
            Stage loginStage = (Stage) usernameField.getScene().getWindow();
            loginStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCloseButton() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }
}
