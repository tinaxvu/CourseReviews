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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    private Label name;

    @FXML
    private TextArea reviewTextArea;

    @FXML
    private TextField ratingTextField;

    @FXML
    private Button backButton;
    @FXML
    private TableView<Review> reviewsTable;

    @FXML
    private TableColumn<Review, String> commentColumn;

    @FXML
    private TableColumn<Review, Double> ratingColumn;

    @FXML
    private TableColumn<Review, Timestamp> timestampColumn;

    private User user;

    private DatabaseDriver databaseDriver;
    private Course selectedCourse;

    public void initialize(Course course) throws SQLException {
        databaseDriver = new DatabaseDriver("course_reviews.sqlite");
        commentColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
        timestampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        databaseDriver.connect();
        populateTable(course);
    }

    public void populateTable(Course course) throws SQLException {
        try {
            this.selectedCourse = course;
            List<Review> reviews = databaseDriver.getReviewsByCourse(selectedCourse);
            ObservableList<Review> observableReviews = FXCollections.observableList(reviews);
            reviewsTable.setItems(observableReviews);
            name.setText(selectedCourse.getMnemonic() + " " + selectedCourse.getCourseNumber() + ": " + selectedCourse.getTitle());
            name.setVisible(true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setUser(User user) {
        this.user = user;
    }

    @FXML
    public void handleAddReview() throws SQLException {
        String reviewComment = reviewTextArea.getText();
        try {
            if (!isValidRating(ratingTextField.getText())) {
                System.out.println("Number Format Exception: invalid input.");
                errorLabel.setText("Invalid rating");
                errorLabel.setVisible(true);
                successLabel.setVisible(false);

            } else if (reviewAddedAlready()) {
                errorLabel.setText("You already made a review");
                errorLabel.setVisible(true);
                successLabel.setVisible(false);

            } else if (isValidRating(ratingTextField.getText()) && !reviewAddedAlready()) {
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                Review review = new Review(user, selectedCourse, reviewComment, Double.parseDouble(ratingTextField.getText()), timestamp);
                successLabel.setText("Review added!");
                successLabel.setVisible(true);
                databaseDriver.addReview(review);
                populateTable(selectedCourse);
            }

        } catch (SQLException | IOException e) {
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

    public boolean reviewAddedAlready() throws SQLException, IOException {
        setUser(databaseDriver.getUserByUsername(CurrentUser.getInstance().getUsername()));
        List<Review> reviews = databaseDriver.getReviewsByUser(user);

        // iterate through user's reviews
        for (Review review : reviews) {
            if (review.getCourse().equals(selectedCourse)) {
                return true;
            }
        }
        return false;
    }
    public void handleBackButton() throws IOException {
        try {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("course-search.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setTitle("Course Search");
        stage.setScene(scene);
        Stage courseReview = (Stage) reviewsTable.getScene().getWindow();
        courseReview.close();
        stage.show();
    } catch (IOException e) {
        e.printStackTrace();
    }
    }
}
