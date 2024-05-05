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
    private Button backToCourseSearchButton;

    @FXML
    private Button backToMyReviewsButton;

    @FXML
    private Label averageRatingLabel;

    @FXML
    private TableView<Review> reviewsTable;

    @FXML
    private TableColumn<Review, String> commentColumn;

    @FXML
    private TableColumn<Review, Double> ratingColumn;

    @FXML
    private TableColumn<Review, Timestamp> timestampColumn;

    @FXML
    private Button deleteButton;

    @FXML
    private Button editButton;

    @FXML
    private Label deleteLabel;

    private User user;

    private DatabaseDriver databaseDriver;
    private Course selectedCourse;

    public void initialize(DatabaseDriver driver, Course course) throws SQLException {
        databaseDriver = driver;
        commentColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
        timestampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        populateTable(course);
    }

    public void initialize(DatabaseDriver driver, MyReviewObject review) throws SQLException {
        databaseDriver = driver;
        commentColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
        timestampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));

        // Since we don't have a Course object associated directly with MyReviewObject,
        // we can load the reviews based on mnemonic and course number.
        Course course = databaseDriver.getCoursesByMnemonicNumber(review.getMnemonic(), review.getCourseNum()).get(0);
        if (course != null) {
            populateTable(course);

            // Handle the case where the course associated with the review is not found.
            // You can display an error message or handle it based on your application's requirements.
        }
    }

    public void populateTable(Course course) throws SQLException {
        try {
            this.selectedCourse = course;
            List<Review> reviews = databaseDriver.getReviewsByCourse(selectedCourse);
            ObservableList<Review> observableReviews = FXCollections.observableList(reviews);
            reviewsTable.setItems(observableReviews);
            name.setText(selectedCourse.getMnemonic() + " " + selectedCourse.getCourseNumber() + ": " + selectedCourse.getTitle());
            name.setVisible(true);
            averageRatingLabel.setText(String.format("%.2f", databaseDriver.getAverageRating(selectedCourse)) + "/5.00");
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
            if (reviewAddedAlready()) {
                errorLabel.setText("You already made a review");
                errorLabel.setVisible(true);
                successLabel.setVisible(false);
                deleteLabel.setVisible(false);

            } else if (!isValidRating(ratingTextField.getText())) {
                errorLabel.setText("Invalid rating");
                errorLabel.setVisible(true);
                successLabel.setVisible(false);
                deleteLabel.setVisible(false);

            } else if (isValidRating(ratingTextField.getText()) && !reviewAddedAlready()) {
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                Review review = new Review(user, selectedCourse, reviewComment, Integer.parseInt(ratingTextField.getText()), timestamp);
                successLabel.setText("Review added!");
                errorLabel.setVisible(false);
                successLabel.setVisible(true);
                databaseDriver.addReview(review);
                databaseDriver.commit();
                populateTable(selectedCourse);
                deleteLabel.setVisible(false);
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            errorLabel.setText("Database error");
            errorLabel.setVisible(true);
            successLabel.setVisible(false);
        }
    }

    @FXML
    public void handleDeleteButton() throws SQLException, IOException {
        try {
            if (reviewAddedAlready()) {
                setUser(databaseDriver.getUserByUsername(CurrentUser.getInstance().getUsername()));
                Review myReview = databaseDriver.getSpecificReview(user, selectedCourse);
                databaseDriver.deleteReview(myReview);
                databaseDriver.commit();
                populateTable(selectedCourse);
                deleteLabel.setText("Delete successful");
                deleteLabel.setVisible(true);
                errorLabel.setVisible(false);
                successLabel.setVisible(false);

            } else {
                deleteLabel.setText("You haven't made a review yet");
                deleteLabel.setVisible(true);
                errorLabel.setVisible(false);
                successLabel.setVisible(false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            deleteLabel.setText("Database error");
            deleteLabel.setVisible(true);
        }
    }

    @FXML
    public void handleEditButton() {
        String reviewComment = reviewTextArea.getText();
        try {
            if (!reviewAddedAlready()) {
                errorLabel.setText("You haven't made a review yet");
                errorLabel.setVisible(true);
                successLabel.setVisible(false);
                deleteLabel.setVisible(false);

            } else if (!isValidRating(ratingTextField.getText())) {
                errorLabel.setText("Invalid rating");
                errorLabel.setVisible(true);
                successLabel.setVisible(false);
                deleteLabel.setVisible(false);

            } else {
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                Review review = new Review(user, selectedCourse, reviewComment, Integer.parseInt(ratingTextField.getText()), timestamp);
                successLabel.setText("Review edited!");
                errorLabel.setVisible(false);
                successLabel.setVisible(true);
                databaseDriver.updateReview(review);
                databaseDriver.commit();
                populateTable(selectedCourse);
                deleteLabel.setVisible(false);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            errorLabel.setText("Database error");
            errorLabel.setVisible(true);
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

    public void handleBackToCourseSearchButton() throws IOException {
        try {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("course-search.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        CourseSearchController controller = fxmlLoader.getController();
        controller.initialize(databaseDriver);
        stage.setTitle("Course Search");
        stage.setScene(scene);
        Stage courseReview = (Stage) reviewsTable.getScene().getWindow();
        courseReview.close();
        stage.show();
    } catch (IOException e) {
        e.printStackTrace();
    } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleBackToMyReviewsButton() throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("my-reviews.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            MyReviewsController controller = fxmlLoader.getController();
            controller.initialize(databaseDriver);
            stage.setTitle("My Reviews");
            stage.setScene(scene);
            Stage courseReview = (Stage) reviewsTable.getScene().getWindow();
            courseReview.close();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
