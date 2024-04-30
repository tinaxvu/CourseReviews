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
import java.util.List;
public class CourseReviewsSceneController {
    private DatabaseDriver databaseDriver;

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

    public void initialize() throws SQLException {
        databaseDriver = new DatabaseDriver("course_reviews.sqlite");
        databaseDriver.connect();
    }

    @FXML
    private void addReview() throws SQLException {
        String reviewComment = reviewTextArea.getText();

        try {
            if (isValidRating(ratingTextField.getText())) {

                Review review = new Review();
                successLabel.setText("Review added!");
                successLabel.setVisible(true);
                databaseDriver.addReview(review);
            } else {
                System.out.println("Number Format Exception: invalid input.");
                errorLabel.setText("Invalid rating");
                errorLabel.setVisible(true);
                successLabel.setVisible(false);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            errorLabel.setText("You already made a review!");
            errorLabel.setVisible(true);
            successLabel.setVisible(false);
        }
    }

    private boolean isValidRating(String rating) {
        try {
            Integer.parseInt(rating);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private void handleBackButton() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("course-search.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
