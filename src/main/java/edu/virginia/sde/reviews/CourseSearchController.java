package edu.virginia.sde.reviews;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class CourseSearchController {
    @FXML
    private Button searchButton;
    @FXML
    private javafx.scene.control.Label addErrorLabel;
    @FXML
    private javafx.scene.control.Label addSuccessLabel;
    @FXML
    private TextField MnemonicAddField;
    @FXML
    private TextField TitleAddField;
    @FXML
    private TextField NumberAddField;
    @FXML
    private TextField MnemonicSearchField;
    @FXML
    private TextField TitleSearchField;
    @FXML
    private TextField NumberSearchField;


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

    public void handleLogOutButton() {
        try
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login-scene.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setTitle("Login");
        stage.setScene(scene);
        Stage SearchStage = (Stage) courseTable.getScene().getWindow();
        SearchStage.close();
        stage.show();
    } catch (IOException e) {
        e.printStackTrace();
    }
    }

    public void handleAddButton() {
        String newMnemonic = MnemonicAddField.getText().toUpperCase();
        String newTitle = TitleAddField.getText();
        String newNumber = NumberAddField.getText();
        try
        {
            if(!isMnemonicValid(newMnemonic)){
                addErrorLabel.setText("Please enter a valid mnemonic");
                addErrorLabel.setVisible(true);
                addSuccessLabel.setVisible(false);
            }
            else if(!isNumberValid(newNumber)){
                addErrorLabel.setText("Please enter a valid number");
                addErrorLabel.setVisible(true);
                addSuccessLabel.setVisible(false);
            }
            else if(!isTitleValid(newTitle)) {
                addErrorLabel.setText("Please enter a valid number");
                addErrorLabel.setVisible(true);
                addSuccessLabel.setVisible(false);
            }else{
                int number = Integer.parseInt(newNumber);
                Course course = new Course(number, newMnemonic, newTitle);
                driver.addCourse(course);
                driver.commit();
                loadCourses();
                addSuccessLabel.setText("Successfully added course!");
                addErrorLabel.setVisible(false);
                addSuccessLabel.setVisible(true);
            }
    } catch (SQLException e) {
            e.printStackTrace();
            addErrorLabel.setText("Database error");
            addErrorLabel.setVisible(true);
            addSuccessLabel.setVisible(false);
        }
    }

    public boolean isMnemonicValid (String mnemonic){
        return mnemonic.length() > 1 && mnemonic.length() < 5;
    }
    public boolean isNumberValid (String number){
        return number.length() == 4;
    }
    public boolean isTitleValid (String title){
        return !title.isEmpty() && title.length() <= 50;
    }
}
