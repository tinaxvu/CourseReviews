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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class CourseSearchController {
    @FXML
    private Button searchButton;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Course> courseTable;

    @FXML
    private TableColumn<Course, String> courseTitleColumn;
    @FXML
    private TableColumn<Course, String> courseMnemonicColumn;
    @FXML
    private TableColumn<Course, Integer> courseIdColumn;
    @FXML
    private TableColumn<Course, Integer> courseNumberColumn;
    @FXML
    private TableColumn<Course, Double> courseRatingColumn;

    private DatabaseDriver driver;

    public void initialize() throws SQLException {
        driver = new DatabaseDriver("course_reviews.sqlite");
        courseTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        courseMnemonicColumn.setCellValueFactory(new PropertyValueFactory<>("mnemonic"));
        courseNumberColumn.setCellValueFactory(new PropertyValueFactory<>("courseNumber"));
        courseRatingColumn.setCellValueFactory(new PropertyValueFactory<>("averageRating"));
        courseIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        driver.connect();
        loadCourses();
    }

    @FXML
    public void loadCourses(){
        try{
            List<Course> courses = driver.getCourses();
            ObservableList<Course> observableCourses = FXCollections.observableList(courses);
            courseTable.setItems(observableCourses);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
