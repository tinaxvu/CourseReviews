package edu.virginia.sde.reviews;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseDriverTest {
    DatabaseDriver databaseDriver = new DatabaseDriver("course_reviews.sqlite");

    User user1 = new User("username1","password123");
    Course Chem = new Course(101, "CHEM","Chemistry 101");
    Timestamp timestamp = new Timestamp(1159);



    @BeforeEach
    void setup() {
        try {
            databaseDriver.connect();
        } catch (SQLException sqlException) {
            System.out.println(sqlException.getMessage());
        }
    }

    @Test
    void createTablesTest() throws SQLException {
            databaseDriver.createTables();
            databaseDriver.commit();
            databaseDriver.disconnect();
    }

    @Test
    void addCourseTest() throws SQLException {
            databaseDriver.addCourse(Chem);
            databaseDriver.commit();
            databaseDriver.disconnect();
    }
    @Test
    void addUserTest() throws SQLException {
            databaseDriver.addUser(user1);
            databaseDriver.commit();
            databaseDriver.disconnect();

    }
    @Test
    void clearTablesTest() throws SQLException {
        databaseDriver.clearTables();
        databaseDriver.commit();
        databaseDriver.disconnect();
    }
    /*@Test
    void addReviewTest() throws SQLException {
        user1.setId(3);
        Chem.setId(3);
        Review review = new Review(user1,Chem, "This class is fantastic!!", 4.5,timestamp);
        databaseDriver.addReview(review);
        databaseDriver.commit();
        databaseDriver.disconnect();
    }*/

}