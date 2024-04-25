package edu.virginia.sde.reviews;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseDriverTest {
    DatabaseDriver databaseDriver = new DatabaseDriver("course_reviews.sqlite");

    User user1 = new User(12345, "username1","password123");
    Course Chem = new Course(123,101, "CHEM","Chemistry 101");
    Timestamp timestamp = new Timestamp(1159);
    Review review = new Review(user1,Chem, "This class is fantastic!!", 4.5,timestamp);


    @BeforeEach
    void setup() {
        try {
            databaseDriver.connect();
        } catch (SQLException sqlException) {
            System.out.println(sqlException.getMessage());
        }
    }

    @Test
    void createTablesTest() {
        try {
            databaseDriver.createTables();
            databaseDriver.commit();
            databaseDriver.disconnect();
        } catch (SQLException sqlException) {
            System.out.println(sqlException.getMessage());
        }
    }


}