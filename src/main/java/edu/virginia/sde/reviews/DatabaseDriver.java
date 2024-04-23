package edu.virginia.sde.reviews;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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
        query = "CREATE TABLE IF NOT ALREADY EXISTS REVIEWS(ID INTEGER PRIMARY KEY, UserID INTEGER, CourseID INTEGER, Rating REAL, Foreign Key(UserID) REFERENCES USERS(ID), Foreign Key (CoursesID) REFERENCES Courses(ID))";
        statement.executeQuery(query);
    }
}
