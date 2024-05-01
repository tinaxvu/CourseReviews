package edu.virginia.sde.reviews;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
public class CourseReviewsSceneController {
    @FXML
    private Button addReviewButton;

    @FXML
    private Label successLabel;

    @FXML
    private Label errorLabel;

    @FXML
    private TextArea reviewTextArea;

    @FXML
    private TextField ratingTextField;

    @FXML
    private Button backButton;

    @FXML
    private LoginSceneController loginSceneController;

    private String username;

    private Course course;

    private User user;

    private DatabaseDriver databaseDriver;

    private Timestamp timestamp;

    public void initialize() throws SQLException {
        databaseDriver = new DatabaseDriver("course_reviews.sqlite");
        databaseDriver.connect();
    }

    public void setUsername() {
        this.username = loginSceneController.getUsername();
    }

    public User getUser() throws SQLException {
        return databaseDriver.getUserByUsername(username);
    }

    public void setUser(User user) {
        this.user = user;
    }

    @FXML
    public void handleAddReview() throws SQLException {
        String reviewComment = reviewTextArea.getText();
        try {
            if (isValidRating(ratingTextField.getText()) && !reviewAddedAlready(course)) {
                Review review = new Review(user, course, reviewComment, Double.parseDouble(ratingTextField.getText()), timestamp);
                successLabel.setText("Review added!");
                successLabel.setVisible(true);
                databaseDriver.addReview(review);

            } else if (!isValidRating(ratingTextField.getText())){
                System.out.println("Number Format Exception: invalid input.");
                errorLabel.setText("Invalid rating");
                errorLabel.setVisible(true);
                successLabel.setVisible(false);

            } else if (reviewAddedAlready(course)) {
                errorLabel.setText("You already made a review");
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

    public boolean isValidRating(String rating) {
        try {
            if (rating != null) {
                Double.parseDouble(rating);
                return true;
            }
            return false;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    /**
     * gets the User object from username
     * gets a list of reviews made by user
     * if course matches, user has already added review
     * @param course
     * @return
     * @throws SQLException
     */
    public boolean reviewAddedAlready(Course course) throws SQLException {
        user = databaseDriver.getUserByUsername(username);
        List<Review> reviews = databaseDriver.getReviewsByUser(user);

        // iterate through user's reviews
        for (Review review : reviews) {
            if (review.getCourse().equals(course)) {
                return true;
            }
        }
        return false;
    }
    public void handleBackButton() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("course-search.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
