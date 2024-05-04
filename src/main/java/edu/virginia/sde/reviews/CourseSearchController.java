package edu.virginia.sde.reviews;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.awt.Button;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class CourseSearchController {

    @FXML
    private Button searchButton;
    @FXML
    private javafx.scene.control.Label addErrorLabel;
    @FXML
    private javafx.scene.control.Label SearchSuccessLabel;
    @FXML
    private javafx.scene.control.Label SearchErrorLabel;
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

    public void initialize(DatabaseDriver driver) throws SQLException {
        this.driver = driver;
        courseTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        courseMnemonicColumn.setCellValueFactory(new PropertyValueFactory<>("mnemonic"));
        courseNumberColumn.setCellValueFactory(new PropertyValueFactory<>("courseNumber"));
        courseRatingColumn.setCellValueFactory(new PropertyValueFactory<>("averageRating"));
        courseIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
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
    @FXML
    public void loadCourses(List<Course> courses){
        ObservableList<Course> observableCourses = FXCollections.observableList(courses);
        courseTable.setItems(observableCourses);
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
        driver.disconnect();
    } catch (IOException | SQLException e) {
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
                addErrorLabel.setText("Please enter a valid title");
                addErrorLabel.setVisible(true);
                addSuccessLabel.setVisible(false);
            }else if(driver.doesCourseExist(newMnemonic,newTitle,Integer.parseInt(newNumber))){
                addErrorLabel.setText("Course already exists");
                addErrorLabel.setVisible(true);
                addSuccessLabel.setVisible(false);
            }
            else{
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
    public void handleSearchButton() {
        String searchMnemonic = MnemonicSearchField.getText().toUpperCase();
        String searchTitle = TitleSearchField.getText();
        String searchNumber = NumberSearchField.getText();
        int searchNumberInt;

        try
        {
            if (searchNumber.isEmpty() && searchTitle.isEmpty() && searchMnemonic.isEmpty()){
                loadCourses();
            }
            if(!isMnemonicValid(searchMnemonic) && !searchMnemonic.isEmpty()){
                SearchErrorLabel.setText("Please enter a valid mnemonic");
                SearchErrorLabel.setVisible(true);
                SearchSuccessLabel.setVisible(false);
            }
            else if(!isNumberValid(searchNumber) && !searchNumber.isEmpty()){
                SearchErrorLabel.setText("Please enter a valid number");
                SearchErrorLabel.setVisible(true);
                SearchSuccessLabel.setVisible(false);
            }
            else if(!isTitleValid(searchTitle) && !searchTitle.isEmpty()) {
                SearchErrorLabel.setText("Please enter a valid title");
                SearchErrorLabel.setVisible(true);
                SearchSuccessLabel.setVisible(false);
            }
            else if (!searchMnemonic.isEmpty() && searchNumber.isEmpty() && searchTitle.isEmpty()){
                List<Course> courses = driver.getCoursesByMnemonic(searchMnemonic);
                loadCourses(courses);
            }
            else if (searchMnemonic.isEmpty() && !searchNumber.isEmpty() && searchTitle.isEmpty()){
                searchNumberInt = Integer.parseInt(searchNumber);
                List<Course> courses = driver.getCoursesByNumber(searchNumberInt);
                loadCourses(courses);
            }
            else if (searchMnemonic.isEmpty() && searchNumber.isEmpty() && !searchTitle.isEmpty()){
                List<Course> courses = driver.getCoursesByTitleSubstring(searchTitle);
                loadCourses(courses);
            }
            else if (!searchMnemonic.isEmpty() && !searchNumber.isEmpty() && searchTitle.isEmpty()){
                searchNumberInt = Integer.parseInt(searchNumber);
                List<Course> courses = driver.getCoursesByMnemonicNumber(searchMnemonic,searchNumberInt);
                loadCourses(courses);
            }
            else if (!searchMnemonic.isEmpty() && searchNumber.isEmpty() && !searchTitle.isEmpty()){
                List<Course> courses = driver.getCoursesByMnemonicTitle(searchMnemonic,searchTitle);
                loadCourses(courses);
            }
            else if (searchMnemonic.isEmpty() && !searchNumber.isEmpty() && !searchTitle.isEmpty()){
                searchNumberInt = Integer.parseInt(searchNumber);
                List<Course> courses = driver.getCoursesByNumberTitle(searchNumberInt,searchTitle);
                loadCourses(courses);
            }
            else if (!searchMnemonic.isEmpty() && !searchNumber.isEmpty() && !searchTitle.isEmpty()){
                searchNumberInt = Integer.parseInt(searchNumber);
                List<Course> courses = driver.getCoursesByMnemonicTitleNumber(searchMnemonic,searchTitle,searchNumberInt);
                loadCourses(courses);
            }
        } catch (SQLException e ) {
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
        try {
            Integer.parseInt(number);
        } catch(NumberFormatException e){
            return false;
        }
        return number.length() == 4;

    }
    public boolean isTitleValid (String title){
        return !title.isEmpty() && title.length() <= 50;
    }

    @FXML
    public void handleClickToChangeToReviews() throws IOException, SQLException {
        ObservableList<Course> courseObservableList;
        courseObservableList=courseTable.getSelectionModel().getSelectedItems();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("course-reviews.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        CourseReviewsSceneController controller = fxmlLoader.getController();
        try {
            controller.initialize(driver, courseObservableList.get(0));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Stage stage = new Stage();
        stage.setTitle("Course Search");
        stage.setScene(scene);
        Stage courseSearch = (Stage) courseTable.getScene().getWindow();
        courseSearch.close();
        stage.show();
    }

    public void handleMyReviewsButton() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("my-reviews.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle("My Reviews");
            stage.setScene(scene);
            Stage loginStage = (Stage) courseTable.getScene().getWindow();
            loginStage.close();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
