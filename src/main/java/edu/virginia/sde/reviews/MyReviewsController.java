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
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class MyReviewsController {

    @FXML
    private TableView<Course> courseTable;


    @FXML
    private TableColumn<Course, String> courseMnemonicColumn;


    @FXML
    private TableColumn<Course, Integer> courseNumberColumn;

    @FXML
    private TableColumn<Course, Double> courseRatingColumn;

    @FXML
    private Button backButton;

    private DatabaseDriver databaseDriver;
    private String username;

    public void setUsername(String username) {
        this.username = username;
    }

  /*  public void initialize(Database driver) throws SQLException {
        databaseDriver = driver;
        initializeColumns();
        loadCourses();
    }
    */

    private void initializeColumns() {
        courseMnemonicColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMnemonic()));
        courseNumberColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCourseNumber()).asObject());
        courseRatingColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getAverageRating()).asObject());
    }

  /*  private void loadCourses() {
        try {
            List<Course> courses = databaseDriver.getCourses();
            ObservableList<Course> observableCourses = FXCollections.observableList(courses);
            courseTable.setItems(observableCourses);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

   */

    @FXML
    private void handleBackButton(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("course-search.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle("Course Search");
            stage.setScene(scene);
            Stage myReviewsStage = (Stage) backButton.getScene().getWindow();
            myReviewsStage.close();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
