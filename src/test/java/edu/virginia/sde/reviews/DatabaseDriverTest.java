package edu.virginia.sde.reviews;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseDriverTest {
    DatabaseDriver databaseDriver = new DatabaseDriver("course_reviews.sqlite");

    User user1 = new User("username1","password123");
    Course Chem = new Course(101, "CHEM","Chemistry 101");
    Timestamp timestamp = new Timestamp(1159);

    User user2 = new User("username2","password123");
    Course math = new Course(101, "MATH","Mathematics 101");
    Course english = new Course(101, "ENG","English 101");

    @BeforeEach
    void setup() {
        try {
            databaseDriver.connect();
            databaseDriver.clearTables();
            databaseDriver.commit();
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
   /* @Test
    void clearTablesTest() throws SQLException {
        databaseDriver.clearTables();
        databaseDriver.commit();databaseDriver.disconnect();
     }*/


    @Test
    void addReviewTest() throws SQLException {
        databaseDriver.addUser(user1);
        databaseDriver.addCourse(Chem);
        Review review = new Review(user1,Chem, "This class is fantastic!!", 4,timestamp);
        databaseDriver.addReview(review);
        databaseDriver.commit();
        databaseDriver.disconnect();
    }

    @Test
    void getReviewByCourseTest() throws SQLException {
        databaseDriver.addUser(user1);
        databaseDriver.addCourse(Chem);
        Review review = new Review(user1,Chem, "This class is fantastic!!", 4,timestamp);
        databaseDriver.addReview(review);
        databaseDriver.commit();
        List<Review> getReviews = databaseDriver.getReviewsByCourse(Chem);
        assertEquals(review.getId(),getReviews.get(0).getId());
        databaseDriver.disconnect();
    }
    @Test
    void getReviewByCourseMultipleTest() throws SQLException {
        databaseDriver.addUser(user1);
        databaseDriver.addUser(user2);
        databaseDriver.addCourse(Chem);
        databaseDriver.addCourse(math);
        databaseDriver.addCourse(english);
        Review chemReview1 = new Review(user1,Chem, "This class is fantastic!!", 4,timestamp);
        Review chemReview2 = new Review(user2,Chem, "This class is bad!!", 2,timestamp);
        Review mathReview1 = new Review(user1,math, "This class is so fun!!", 4,timestamp);
        Review mathReview2 = new Review(user2,math, "This class is so boring!!", 1,timestamp);
        databaseDriver.addReview(chemReview1);
        databaseDriver.addReview(chemReview2);
        databaseDriver.addReview(mathReview1);
        databaseDriver.addReview(mathReview2);
        databaseDriver.commit();
        List<Review> getReviews = databaseDriver.getReviewsByCourse(Chem);
        assertEquals(chemReview1.getId(),getReviews.get(0).getId());
        assertEquals(chemReview2.getId(),getReviews.get(getReviews.size()-1).getId());
        databaseDriver.disconnect();
    }
    @Test
    void getSpecificReviewTest() throws SQLException {
        databaseDriver.addUser(user1);
        databaseDriver.addCourse(Chem);
        Review review = new Review(user1,Chem, "This class is fantastic!!", 4,timestamp);
        databaseDriver.addReview(review);
        databaseDriver.commit();
        Review myReview = databaseDriver.getSpecificReview(user1, Chem);
        assertEquals(review.getId(),myReview.getId());
        databaseDriver.disconnect();
    }
    @Test
    void getReviewByUserTest() throws SQLException {
        databaseDriver.addUser(user1);
        databaseDriver.addCourse(Chem);
        Review review = new Review(user1,Chem, "This class is fantastic!!", 4,timestamp);
        databaseDriver.addReview(review);
        databaseDriver.commit();
        List<Review> getReviews = databaseDriver.getReviewsByUser(user1);
        assertEquals(review.getId(),getReviews.get(0).getId());
        databaseDriver.disconnect();
    }
    @Test
    void getReviewByUserMultipleTest() throws SQLException {
        databaseDriver.addUser(user1);
        databaseDriver.addUser(user2);
        databaseDriver.addCourse(Chem);
        databaseDriver.addCourse(math);
        databaseDriver.addCourse(english);
        Review chemReview1 = new Review(user1,Chem, "This class is fantastic!!", 4,timestamp);
        Review chemReview2 = new Review(user2,Chem, "This class is bad!!", 2,timestamp);
        Review mathReview1 = new Review(user1,math, "This class is so fun!!", 4,timestamp);
        Review mathReview2 = new Review(user2,math, "This class is so boring!!", 1,timestamp);
        databaseDriver.addReview(chemReview1);
        databaseDriver.addReview(chemReview2);
        databaseDriver.addReview(mathReview1);
        databaseDriver.addReview(mathReview2);
        databaseDriver.commit();
        List<Review> getReviews = databaseDriver.getReviewsByUser(user1);
        assertEquals(mathReview1.getId(),getReviews.get(0).getId());
        assertEquals(mathReview1.getId(),getReviews.get(1).getId());
        assertEquals(2, getReviews.size());

        databaseDriver.disconnect();
    }
    @Test
    void deleteUserTest() throws SQLException {
        databaseDriver.addUser(user1);
        databaseDriver.addUser(user2);
        databaseDriver.deleteUser(user1);
        databaseDriver.commit();
        databaseDriver.disconnect();
    }

    @Test
    void deleteCourseTest() throws SQLException {
        databaseDriver.addCourse(Chem);
        databaseDriver.addCourse(math);
        databaseDriver.addCourse(english);
        databaseDriver.deleteCourse(math);
        databaseDriver.commit();
        databaseDriver.disconnect();
    }
    @Test
    void deleteReviewTest() throws SQLException {
        databaseDriver.addUser(user1);
        databaseDriver.addUser(user2);
        databaseDriver.addCourse(Chem);
        databaseDriver.addCourse(math);
        databaseDriver.addCourse(english);
        Review chemReview1 = new Review(user1,Chem, "This class is fantastic!!", 4,timestamp);
        Review chemReview2 = new Review(user2,Chem, "This class is bad!!", 2,timestamp);
        Review mathReview1 = new Review(user1,math, "This class is so fun!!", 4,timestamp);
        Review mathReview2 = new Review(user2,math, "This class is so boring!!", 1,timestamp);
        databaseDriver.addReview(chemReview1);
        databaseDriver.addReview(chemReview2);
        databaseDriver.addReview(mathReview1);
        databaseDriver.addReview(mathReview2);
        databaseDriver.deleteReview(mathReview1);
        databaseDriver.deleteReview(chemReview2);
        databaseDriver.commit();
        databaseDriver.disconnect();
    }
    @Test
    void getCoursesByMnemonicTest() throws SQLException {
        databaseDriver.addCourse(Chem);
        databaseDriver.addCourse(math);
        databaseDriver.addCourse(english);
        databaseDriver.commit();
        List<Course> courses= databaseDriver.getCoursesByMnemonic("ENG");
        assertEquals(english.getId(),courses.get(0).getId());
        assertEquals(1, courses.size());
        databaseDriver.disconnect();
    }
    @Test
    void getCoursesByMnemonicEmptyTest() throws SQLException {
        databaseDriver.commit();
        List<Course> courses= databaseDriver.getCoursesByMnemonic("ENG");
        assertEquals(0, courses.size());
        databaseDriver.disconnect();
    }
    @Test
    void getCoursesByNumberTest() throws SQLException {
        databaseDriver.addCourse(Chem);
        databaseDriver.addCourse(math);
        databaseDriver.addCourse(english);
        databaseDriver.commit();
        List<Course> courses= databaseDriver.getCoursesByNumber(101);
        assertEquals(Chem.getId(),courses.get(0).getId());
        assertEquals(1, courses.size());
        databaseDriver.disconnect();
    }
    @Test
    void getCoursesByTitleSubstringTest() throws SQLException {
        databaseDriver.addCourse(Chem);
        databaseDriver.addCourse(math);
        databaseDriver.addCourse(english);
        databaseDriver.commit();
        List<Course> courses= databaseDriver.getCoursesByTitleSubstring("hem");
        assertEquals(Chem.getId(),courses.get(0).getId());
        assertEquals(math.getId(),courses.get(1).getId());
        assertEquals(2, courses.size());
        databaseDriver.disconnect();
    }
    @Test
    void getCoursesByMnemonicNumberTest() throws SQLException {
        databaseDriver.addCourse(Chem);
        databaseDriver.addCourse(math);
        databaseDriver.addCourse(english);
        databaseDriver.commit();
        List<Course> courses= databaseDriver.getCoursesByMnemonicNumber("CHEM",101);
        assertEquals(Chem.getId(),courses.get(0).getId());
        assertEquals(1, courses.size());
        databaseDriver.disconnect();
    }
    @Test
    void getCoursesByMnemonicTitleTest() throws SQLException {
        databaseDriver.addCourse(Chem);
        databaseDriver.addCourse(math);
        databaseDriver.addCourse(english);
        databaseDriver.commit();
        List<Course> courses= databaseDriver.getCoursesByMnemonicTitle("CHEM","hem");
        assertEquals(Chem.getId(),courses.get(0).getId());
        assertEquals(1, courses.size());
        databaseDriver.disconnect();
    }
    @Test
    void getCoursesByNumberTitleTest() throws SQLException {
        databaseDriver.addCourse(Chem);
        databaseDriver.addCourse(math);
        databaseDriver.addCourse(english);
        databaseDriver.commit();
        List<Course> courses= databaseDriver.getCoursesByNumberTitle(101,"hem");
        assertEquals(Chem.getId(),courses.get(0).getId());
        assertEquals(2, courses.size());
        databaseDriver.disconnect();
    }
    @Test
    void getCoursesByMnemonicNumberTitleTest() throws SQLException {
        databaseDriver.addCourse(Chem);
        databaseDriver.addCourse(math);
        databaseDriver.addCourse(english);
        databaseDriver.commit();
        List<Course> courses= databaseDriver.getCoursesByMnemonicTitleNumber("CHEM", "hem", 101);
        assertEquals(Chem.getId(),courses.get(0).getId());
        assertEquals(1, courses.size());
        databaseDriver.disconnect();
    }

    @Test
    void getCoursesTest() throws SQLException {
        databaseDriver.addCourse(Chem);
        databaseDriver.addCourse(math);
        databaseDriver.addCourse(english);
        databaseDriver.commit();
        List<Course> courses= databaseDriver.getCourses();
        assertEquals(Chem.getId(),courses.get(0).getId());
        assertEquals(math.getId(),courses.get(1).getId());
        assertEquals(english.getId(),courses.get(2).getId());
        assertEquals(3, courses.size());
        databaseDriver.disconnect();
    }
    @Test
    void getUserbyUsernameTest() throws SQLException {
        databaseDriver.addUser(user1);
        databaseDriver.addUser(user2);
        databaseDriver.commit();
        User user = databaseDriver.getUserByUsername("username2");
        assertEquals(user2.getId(),user.getId());
        databaseDriver.disconnect();
    }
    @Test
    void doesCourseExistTest() throws SQLException {
        boolean emptyTest = databaseDriver.doesCourseExist("CHEM", "Chemistry 101", 101);
        assertFalse(emptyTest);
        databaseDriver.addCourse(Chem);
        databaseDriver.commit();
        boolean trueTest= databaseDriver.doesCourseExist("CHEM", "Chemistry 101", 101);
        assertTrue(trueTest);
        databaseDriver.disconnect();
    }
    @Test
    void updateReviewTest() throws SQLException {
        databaseDriver.addUser(user1);
        databaseDriver.addUser(user2);
        databaseDriver.addCourse(Chem);
        databaseDriver.addCourse(math);
        databaseDriver.addCourse(english);
        Review chemReview1 = new Review(user1,Chem, "This class is fantastic!!", 4,timestamp);
        Review chemReview2 = new Review(user2,Chem, "This class is bad!!", 2,timestamp);
        databaseDriver.addReview(chemReview2);
        databaseDriver.addReview(chemReview1);
        databaseDriver.commit();
        Review chemReviewEdit = new Review(user1,Chem, "editTest", 1,timestamp);
        databaseDriver.updateReview(chemReviewEdit);
        databaseDriver.commit();
        databaseDriver.disconnect();
    }

}