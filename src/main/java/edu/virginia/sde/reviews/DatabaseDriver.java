package edu.virginia.sde.reviews;

import java.sql.*;
import java.util.List;

public class DatabaseDriver {

    //code up until create table comes from HW 5
    private Connection connection;

    private final String sqliteFilename;
    public DatabaseDriver (String sqlListDatabaseFilename) {
        this.sqliteFilename = sqlListDatabaseFilename;
    }

    /**
     * Connect to a SQLite Database. This turns out Foreign Key enforcement, and disables auto-commits
     * @throws SQLException
     */
    public void connect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            throw new IllegalStateException("The connection is already opened");
        }
        connection = DriverManager.getConnection("jdbc:sqlite:" + sqliteFilename);
        //the next line enables foreign key enforcement - do not delete/comment out
        connection.createStatement().execute("PRAGMA foreign_keys = ON");
        //the next line disables auto-commit - do not delete/comment out
        connection.setAutoCommit(false);
    }

    /**
     * Commit all changes since the connection was opened OR since the last commit/rollback
     */
    public void commit() throws SQLException {
        connection.commit();
    }

    /**
     * Rollback to the last commit, or when the connection was opened
     */
    public void rollback() throws SQLException {
        connection.rollback();
    }

    /**
     * Ends the connection to the database
     */
    public void disconnect() throws SQLException {
        connection.close();
    }

    public void createTables() throws SQLException {
        Statement statement = connection.createStatement();
        String query = "CREATE TABLE IF NOT ALREADY EXISTS USERS(ID INTEGER PRIMARY KEY, Username TEXT, PASSWORD TEXT)";
        statement.executeQuery(query);
        query = "CREATE TABLE IF NOT ALREADY EXISTS COURSES(ID INTEGER PRIMARY KEY, CourseNumber INTEGER, Mnemonic TEXT, Title TEXT, Rating REAL)";
        statement.executeQuery(query);
        query = "CREATE TABLE IF NOT ALREADY EXISTS REVIEWS(ID INTEGER PRIMARY KEY autoincrement, UserID INTEGER, CourseID INTEGER, Rating REAL, Comment TEXT, Stamp TIMESTAMP, Foreign Key(UserID) REFERENCES USERS(ID), Foreign Key (CoursesID) REFERENCES Courses(ID))";
        statement.executeQuery(query);
    }
    public void clearTables() throws SQLException {
        Statement sqlStatement = connection.createStatement();
        sqlStatement.addBatch("DELETE FROM USERS");
        sqlStatement.addBatch("DELETE FROM COURSES");
        sqlStatement.addBatch("DELETE FROM REVIEWS");
        sqlStatement.executeBatch();
    }

    public void addCourse(Course course) throws SQLException {
        Statement statement = connection.createStatement();
        String query = String.format("SELECT * FROM COURSES WHERE CourseNumber = %d and Mnemonic = %s and Title = %s", course.getCourseNumber(), course.getMnemonic(), course.getTitle());
        ResultSet result = statement.executeQuery(query);
        if (result.wasNull()) {
            query = String.format("""
                    INSERT INTO COURSES(ID, CourseNumber, Mnemonic, Title, Rating) values (%d, %d, %s %s %f)
                    """, course.getId(), course.getCourseNumber(), course.getMnemonic(), course.getTitle(), course.getAverageRating());
            statement.executeQuery(query);
        }
    }

    public void addUser(User user) throws SQLException {
        Statement statement = connection.createStatement();
        String query = String.format("Select * from USERS where Username = %s", user.getUsername());
        ResultSet result = statement.executeQuery(query);
        if (result.wasNull()) {
            query = String.format("""
                    INSERT INTO USERS(Username, Password) values (%s, %s)
                    """, user.getPassword(), user.getPassword());
            statement.executeQuery(query);
        }
    }
    public void addReview(Review review) throws SQLException {
        Statement statement = connection.createStatement();
        String query = String.format("SELECT * FROM REVIEWS WHERE UserID = %d and CourseID = %d", review.getUser().getId(), review.getCourse().getId());
        ResultSet result = statement.executeQuery(query);
        if(result.wasNull()){
            query = String.format("""
                    INSERT INTO REVIEWS(UserID, CourseID, Rating, Comment, Stamp) values(%d, %d, %f, %s, %o)
                    """, review.getUser().getId(), review.getCourse().getId(), review.getRating(), review.getComment(), review.getTimestamp().getTime());
            statement.executeQuery(query);
        }
    }

    /***
     * sort the list of reviews by the course id to get all the reviews about one course
     * @param course
     */
    public List<Review> getReviewsByCourse(Course course){
        return null;
    }

    /***
     * get the reviews based on the user who wrote them
     * @param user
     * @return
     */
    public List<Review> getReviewsByUser(User user){
        return null;
    }

    /***
     * gets a very specific review from the database
     */
    public Review getReviewByID(Review review){
        return null;
    }

    /***
     * Delete a review from the list
     * @param review
     */
    public void deleteReview(Review review){

    }
    /***
     * Delete a course from the list. Must also remove all reviews from reviews
     */
    public void deleteCourse(Course course){

    }

    /***
     * Delete a User from the database
     */
    public void deleteUser(User user){

    }

    /***
     * get all the courses from the database
     * @return
     */
    public List<Course> getCourses(){
        return null;
    }


}
