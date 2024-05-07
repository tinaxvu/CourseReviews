package edu.virginia.sde.reviews;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.sql.SQLException;

public class LoginSceneController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Label successLabel;

    private DatabaseDriver databaseDriver;

    public void initialize() throws SQLException {
        databaseDriver = new DatabaseDriver("course_reviews.sqlite");
        databaseDriver.connect();
        usernameField.requestFocus();
    }

    @FXML
    private void handleLoginButton() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        try {
            if (isValidCredentials(username, password)) {
                successLabel.setText("Login successful");
                successLabel.setVisible(true);
                errorLabel.setVisible(false);
                CurrentUser.init(usernameField.getText());
                navigateToCourseSearchScreen();
            } else if (usernameExists(username)){
                errorLabel.setText("Incorrect password");
                errorLabel.setVisible(true);
                successLabel.setVisible(false);
            } else{
                errorLabel.setText("Username does not exist");
                errorLabel.setVisible(true);
                successLabel.setVisible(false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            errorLabel.setText("Database error");
            errorLabel.setVisible(true);
            successLabel.setVisible(false);
        }
    }

    @FXML
    private void handleCreateUserButton() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        try {
            if(usernameExists(username)){
                errorLabel.setText("Username already exists");
                errorLabel.setVisible(true);
                successLabel.setVisible(false);
            }
            else if (isPasswordValid(password)) {
                User newUser = new User(username, password);
                databaseDriver.addUser(newUser);
                databaseDriver.commit();
                successLabel.setText("User created successfully");
                successLabel.setVisible(true);
                errorLabel.setVisible(false);
            } else {
                errorLabel.setText("Invalid password, must have 8+ characters");
                errorLabel.setVisible(true);
                successLabel.setVisible(false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            errorLabel.setText("Database error");
            errorLabel.setVisible(true);
            successLabel.setVisible(false);
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
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            CourseSearchController controller = fxmlLoader.getController();
            controller.initialize(databaseDriver);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCloseButton() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }

    public TextField getUsernameField() {
        return usernameField;
    }
}
