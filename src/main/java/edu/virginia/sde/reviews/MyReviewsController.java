package edu.virginia.sde.reviews;


import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class MyReviewsController {

    @FXML
    private TableView<MyReviewObject> reviewTable;

    @FXML
    private TableColumn<Course, String> courseMnemonicColumn;

    @FXML
    private TableColumn<Review, String> reviewCommentColumn;

    @FXML
    private TableColumn<Review, Integer> reviewIDColumn;

    @FXML
    private TableColumn<Review, Integer> reviewUserIDColumn;

    @FXML
    private TableColumn<Review, Integer> courseIDColumn;

    @FXML
    private TableColumn<Review, Timestamp> reviewTimestampTableColumn;

    @FXML
    private TableColumn<Course, Integer> courseNumberColumn;

    @FXML
    private TableColumn<Course, Double> courseRatingColumn;

    @FXML
    private Button backButton;

    private DatabaseDriver databaseDriver;

    public void initialize(DatabaseDriver driver) throws SQLException {
        databaseDriver = driver;
        initializeColumns();
        loadReviews();
    }

    private void initializeColumns() {
        courseMnemonicColumn.setCellValueFactory(new PropertyValueFactory<>("Mnemonic"));
        reviewCommentColumn.setCellValueFactory(new PropertyValueFactory<>("Comment"));
        reviewIDColumn.setCellValueFactory(new PropertyValueFactory<>("ReviewID"));
        reviewUserIDColumn.setCellValueFactory(new PropertyValueFactory<>("UserID"));
        courseIDColumn.setCellValueFactory(new PropertyValueFactory<>("CourseID"));
        courseRatingColumn.setCellValueFactory(new PropertyValueFactory<>("Rating"));
        reviewTimestampTableColumn.setCellValueFactory(new PropertyValueFactory<>("Timestamp"));
        courseNumberColumn.setCellValueFactory(new PropertyValueFactory<>("Number"));
    }

   /*private void loadCourses() {
        try {
            List<Course> courses = databaseDriver.getCourses();
            ObservableList<Course> observableCourses = FXCollections.observableList(courses);
            courseTable.setItems(observableCourses);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }*/
    @FXML
    private void loadReviews() throws SQLException {
        User user = databaseDriver.getUserByUsername(CurrentUser.getInstance().getUsername());
        List<Review> reviews = databaseDriver.getReviewsByUser(user);
        List<MyReviewObject> myReviews = new ArrayList<>();
        for(Review review : reviews) {
            MyReviewObject MRO = new MyReviewObject(review.getId(), review.getCourse().getMnemonic(), review.getCourse().getCourseNumber(), review.getUser().getId(), review.getCourse().getId(), review.getComment(), review.getRating(), review.getTimestamp());
            myReviews.add(MRO);
        }
        ObservableList<MyReviewObject> observableList = FXCollections.observableList(myReviews);
        reviewTable.setItems(observableList);

    }

    @FXML
    private void handleBackButton(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("course-search.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            CourseSearchController controller = fxmlLoader.getController();
            controller.initialize(databaseDriver);
            stage.setTitle("Course Search");
            stage.setScene(scene);
            Stage myReviewsStage = (Stage) backButton.getScene().getWindow();
            myReviewsStage.close();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
