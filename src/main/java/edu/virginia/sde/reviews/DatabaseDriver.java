package edu.virginia.sde.reviews;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseDriver {

    //code up until create table comes from HW 5
    private Connection connection;

    private final String sqliteFilename;

    public DatabaseDriver(String sqlListDatabaseFilename) {
        this.sqliteFilename = sqlListDatabaseFilename;
    }

    /**
     * Connect to a SQLite Database. This turns out Foreign Key enforcement, and disables auto-commits
     *
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
        String query = "CREATE TABLE IF NOT EXISTS USERS(ID INTEGER PRIMARY KEY autoincrement, Username TEXT, PASSWORD TEXT)";
        statement.executeUpdate(query);
        query = "CREATE TABLE IF NOT EXISTS COURSES(ID INTEGER PRIMARY KEY autoincrement, CourseNumber INTEGER, Mnemonic TEXT, Title TEXT, Rating REAL)";
        statement.executeUpdate(query);
        query = "CREATE TABLE IF NOT EXISTS REVIEWS(ID INTEGER PRIMARY KEY autoincrement, UserID INTEGER, CourseID INTEGER,  Comment TEXT, Rating REAL, Stamp TIMESTAMP, Foreign Key(UserID) REFERENCES USERS(ID), Foreign Key (CourseID) REFERENCES Courses(ID))";
        statement.executeUpdate(query);
        statement.close();
    }

    public void clearTables() throws SQLException {
        Statement sqlStatement = connection.createStatement();
        sqlStatement.addBatch("DELETE FROM REVIEWS");
        sqlStatement.executeBatch();
        sqlStatement.addBatch("DELETE FROM USERS");
        sqlStatement.addBatch("DELETE FROM COURSES");
        sqlStatement.executeBatch();
    }

    public void addCourse(Course course) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            Statement statement = connection.createStatement();
            String query = String.format("SELECT * FROM COURSES WHERE CourseNumber = %d and Mnemonic = '%s' and Title = '%s'", course.getCourseNumber(), course.getMnemonic(), course.getTitle());
            ResultSet result = statement.executeQuery(query);
            if (!result.next()) {// error with this
                PreparedStatement ps = connection.prepareStatement("INSERT INTO COURSES (CourseNumber, Mnemonic, Title, Rating) VALUES (?, ?, ?, ?)");
                ps.setInt(1, course.getCourseNumber());
                ps.setString(2, course.getMnemonic());
                ps.setString(3, course.getTitle());
                ps.setDouble(4, course.getAverageRating());
                ps.execute();
                PreparedStatement ps2 = connection.prepareStatement("SELECT last_insert_rowid()");
                ResultSet ID = ps2.executeQuery();
                if (ID.next()) {//error with this
                    course.setId(ID.getInt(1));
                }
            }
        }
    }

    public void addUser(User user) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            Statement statement = connection.createStatement();
            String query = String.format("Select * FROM USERS WHERE Username = '%s'", user.getUsername());
            ResultSet result = statement.executeQuery(query);
            if (!result.next()) {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO USERS (Username, Password) VALUES (?, ?)");
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getPassword());
                ps.execute();
                PreparedStatement ps2 = connection.prepareStatement("SELECT last_insert_rowid()");
                ResultSet ID = ps2.executeQuery();
                if (ID.next()) {
                    user.setId(ID.getInt(1));
                }
            } else {
                throw new SQLException("User already exists");
            }
        }
    }

    public void addReview(Review review) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            Statement statement = connection.createStatement();
            String query = String.format("SELECT * FROM REVIEWS WHERE UserID = %d and CourseID = %d", review.getUser().getId(), review.getCourse().getId());
            ResultSet result = statement.executeQuery(query);
            if (!result.next()) {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO REVIEWS(UserID, CourseID, Comment, Rating, Stamp) values(?,?,?,?,?)");
                ps.setInt(1, review.getUser().getId());
                ps.setInt(2, review.getCourse().getId());
                ps.setString(3, review.getComment());
                ps.setDouble(4, review.getRating());
                ps.setTimestamp(5, review.getTimestamp());
                ps.execute();
                PreparedStatement ps2 = connection.prepareStatement("SELECT last_insert_rowid()");
                ResultSet ID = ps2.executeQuery();
                if (ID.next()) {
                    review.setId(ID.getInt(1));
                }
            }
            calculateAverageRating(review.getCourse());
        }
    }

    /***
     * sort the list of reviews by the course id to get all the reviews about one course
     * @param course
     */
    public List<Review> getReviewsByCourse(Course course) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            PreparedStatement preparedStatement = connection.prepareStatement("Select * from Reviews where CourseID = ?");
            preparedStatement.setInt(1, course.getId());
            ResultSet result = preparedStatement.executeQuery();

            List<Review> reviews = new ArrayList<>();
            while (result.next()) {
                PreparedStatement userPS = connection.prepareStatement("SELECT * FROM USERS WHERE ID = ?");
                ResultSet resultUser = userPS.executeQuery();
                User user = null;
                while (resultUser.next()) {
                    user = new User(resultUser.getString("Username"), resultUser.getString("Password"));
                    user.setId(resultUser.getInt("UserID"));
                }
                reviews.add(new Review(result.getInt("ID"), user, course, result.getString("Comment"), result.getDouble("Rating"), result.getTimestamp("Stamp")));
            }
            return reviews;
        }
        return null;
    }

    /***
     * get the reviews based on the user who wrote them
     * @param user
     * @return
     */
    public List<Review> getReviewsByUser(User user) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            PreparedStatement ps = connection.prepareStatement("SELECT * from REVIEWS WHERE UserID = ?");
            ps.setInt(1, user.getId());
            ResultSet resultSet = ps.executeQuery();

            List<Review> reviewsList = new ArrayList<>();
            Review review = new Review();
            Course course = new Course();

            while (resultSet.next()) {
                PreparedStatement ps2 = connection.prepareStatement("SELECT * FROM COURSES WHERE ID = ?");
                ps2.setInt(1, resultSet.getInt("CourseID"));
                ResultSet courseResultSet = ps2.executeQuery();

                if (courseResultSet.next()) {
                    course.setId(courseResultSet.getInt("ID"));
                    course.setCourseNumber(courseResultSet.getInt("CourseNumber"));
                    course.setMnemonic(courseResultSet.getString("Mnemonic"));
                    course.setTitle(courseResultSet.getString("Title"));
                    course.setAverageRating(courseResultSet.getDouble("Rating"));
                }
                review.setId(resultSet.getInt("ID"));
                review.setUser(user);
                review.setCourse(course);
                review.setComment(resultSet.getString("Comment"));
                review.setRating(resultSet.getDouble("Rating"));
                review.setTimestamp(resultSet.getTimestamp("Stamp"));
                reviewsList.add(review);
            }
            return reviewsList;
        }
        return null;
    }

    /***
     * Delete a review from the list
     * @param review
     */
    public void deleteReview(Review review) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM REVIEWS WHERE ID = ?");
            ps.setInt(1, review.getId());

            ps.executeUpdate();
        }
    }

    /***
     * Delete a course from the list. Must also remove all reviews from reviews
     */
    public void deleteCourse(Course course) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM REVIEWS WHERE CourseID = ?");
            ps.setInt(1, course.getId());
            ps.executeUpdate();

            PreparedStatement ps1 = connection.prepareStatement("DELETE FROM COURSES WHERE ID = ?");
            ps1.setInt(1, course.getId());
            ps1.executeUpdate();
        }
    }

    /***
     * Delete a User from the database
     */
    public void deleteUser(User user) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM REVIEWS WHERE UserID = ?");
            ps.setInt(1, user.getId());
            ps.executeUpdate();

            PreparedStatement ps1 = connection.prepareStatement("DELETE FROM USERS WHERE ID = ?");
            ps1.setInt(1, user.getId());
            ps1.executeUpdate();
        }
    }

    /***
     * get all the courses from the database
     * @return
     */
    public List<Course> getCourses() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM COURSES");
            List<Course> courses = new ArrayList<>();
            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                int courseNumber = resultSet.getInt("CourseNumber");
                String mnemonic = resultSet.getString("Mnemonic");
                String title = resultSet.getString("Title");
                double averageRating = resultSet.getDouble("Rating");
                Course course = new Course(id, courseNumber, mnemonic, title, averageRating);
                courses.add(course);
            }
            return courses;
        }
        return null;
    }


    public List<Course> getCoursesByMnemonic(String mnemonic) throws SQLException {
        if(connection != null && !connection.isClosed()){
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM COURSES WHERE MNEMONIC = ?");
            ps.setString(1, mnemonic);
            ResultSet resultSet = ps.executeQuery();
            List<Course> courses = new ArrayList<>();
            while(resultSet.next()){
                Course course = new Course();
                course.setId(resultSet.getInt("ID"));
                course.setCourseNumber(resultSet.getInt("CourseNumber"));
                course.setTitle(resultSet.getString("Title"));
                course.setMnemonic(mnemonic);
                if(resultSet.getDouble("Rating") != 0.0){
                    course.setAverageRating(resultSet.getDouble("Rating"));
                }
                courses.add(course);
            }
            return courses;
        }
        return null;
    }
    public List<Course> getCoursesByNumber(int number) throws SQLException {
        if(connection != null && !connection.isClosed()){
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM COURSES WHERE COURSENUMBER = ?");
            ps.setInt(1, number);
            ResultSet resultSet = ps.executeQuery();
            List<Course> courses = new ArrayList<>();
            while(resultSet.next()){
                Course course = new Course();
                course.setId(resultSet.getInt("ID"));
                course.setCourseNumber(number);
                course.setTitle(resultSet.getString("Title"));
                course.setMnemonic(resultSet.getString("Mnemonic"));
                if(resultSet.getDouble("Rating") != 0.0){
                    course.setAverageRating(resultSet.getDouble("Rating"));
                }
                courses.add(course);
            }
            return courses;
        }
        return null;
    }

    public List<Course> getCoursesByTitleSubstring(String title) throws SQLException {
        if(connection != null && !connection.isClosed()){
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM COURSES WHERE TITLE LIKE ?");
            ps.setString(1, "%" + title + "%");
            ResultSet resultSet = ps.executeQuery();
            List<Course> courses = new ArrayList<>();
            while(resultSet.next()){
                Course course = new Course();
                course.setId(resultSet.getInt("ID"));
                course.setCourseNumber(resultSet.getInt("CourseNumber"));
                course.setTitle(resultSet.getString("Title"));
                course.setMnemonic(resultSet.getString("Mnemonic"));
                if(resultSet.getDouble("Rating") != 0.0){
                    course.setAverageRating(resultSet.getDouble("Rating"));
                }
                courses.add(course);
            }
            return courses;
        }
        return null;
    }
    public List<Course> getCoursesByMnemonicNumber(String mnemonic, int number) throws SQLException {
        if(connection != null && !connection.isClosed()){
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM COURSES WHERE MNEMONIC = ? AND COURSENUMBER = ?");
            ps.setString(1, mnemonic);
            ps.setInt(2, number);
            ResultSet resultSet = ps.executeQuery();
            List<Course> courses = new ArrayList<>();
            while(resultSet.next()){
                Course course = new Course();
                course.setId(resultSet.getInt("ID"));
                course.setCourseNumber(number);
                course.setTitle(resultSet.getString("Title"));
                course.setMnemonic(mnemonic);
                if(resultSet.getDouble("Rating") != 0.0){
                    course.setAverageRating(resultSet.getDouble("Rating"));
                }
                courses.add(course);
            }
            return courses;
        }
        return null;
    }
    public List<Course> getCoursesByMnemonicTitle(String mnemonic, String title) throws SQLException {
        if(connection != null && !connection.isClosed()){
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM COURSES WHERE MNEMONIC = ? AND TITLE LIKE ?");
            ps.setString(2, "%" + title + "%");
            ps.setString(1, mnemonic);
            ResultSet resultSet = ps.executeQuery();
            List<Course> courses = new ArrayList<>();
            while(resultSet.next()){
                Course course = new Course();
                course.setId(resultSet.getInt("ID"));
                course.setCourseNumber(resultSet.getInt("CourseNumber"));
                course.setTitle(resultSet.getString("Title"));
                course.setMnemonic(mnemonic);
                if(resultSet.getDouble("Rating") != 0.0){
                    course.setAverageRating(resultSet.getDouble("Rating"));
                }
                courses.add(course);
            }
            return courses;
        }
        return null;
    }
    public List<Course> getCoursesByNumberTitle(int number, String title) throws SQLException {
        if(connection != null && !connection.isClosed()){
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM COURSES WHERE COURSENUMBER = ? AND TITLE LIKE ?");
            ps.setString(2, "%" + title + "%");
            ps.setInt(1, number);
            ResultSet resultSet = ps.executeQuery();
            List<Course> courses = new ArrayList<>();
            while(resultSet.next()){
                Course course = new Course();
                course.setId(resultSet.getInt("ID"));
                course.setCourseNumber(number);
                course.setTitle(resultSet.getString("Title"));
                course.setMnemonic(resultSet.getString("Mnemonic"));
                if(resultSet.getDouble("Rating") != 0.0){
                    course.setAverageRating(resultSet.getDouble("Rating"));
                }
                courses.add(course);
            }
            return courses;
        }
        return null;
    }
    public List<Course> getCoursesByMnemonicTitleNumber(String mnemonic, String title,int number) throws SQLException {
        if(connection != null && !connection.isClosed()){
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM COURSES WHERE MNEMONIC = ? AND TITLE LIKE ? AND COURSENUMBER = ?");
            ps.setString(2, "%" + title + "%");
            ps.setString(1, mnemonic);
            ps.setInt(3, number);
            ResultSet resultSet = ps.executeQuery();
            List<Course> courses = new ArrayList<>();
            while(resultSet.next()){
                Course course = new Course();
                course.setId(resultSet.getInt("ID"));
                course.setCourseNumber(number);
                course.setTitle(resultSet.getString("Title"));
                course.setMnemonic(mnemonic);
                if(resultSet.getDouble("Rating") != 0.0){
                    course.setAverageRating(resultSet.getDouble("Rating"));
                }
                courses.add(course);
            }
            return courses;
        }
        return null;
    }

    public boolean doesCourseExist(String mnemonic, String title, int number)throws SQLException{
        if(connection != null && !connection.isClosed()) {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM COURSES WHERE MNEMONIC = ? AND TITLE = ? AND COURSENUMBER = ?");
            ps.setString(1, mnemonic);
            ps.setString(2, title);
            ps.setInt(3, number);
            ResultSet resultSet = ps.executeQuery();
            return resultSet.next();
        }
        return false;
    }

    public User getUserByUsername(String username) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM USERS WHERE Username = ?");
            ps.setString(1, username);
            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("ID"));
                user.setUsername(resultSet.getString("Username"));
                user.setPassword(resultSet.getString("Password"));
                return user;
            }
        }
        return null;
    }

    public void calculateAverageRating(Course course) throws SQLException {
        List<Review> courseReviews = getReviewsByCourse(course);
        double averageRating = 0.0;
        for(Review review : courseReviews){
            averageRating += review.getRating();
        }
        course.setAverageRating(averageRating/courseReviews.size());
    }

    public void updateReview(Review review) throws SQLException {
        if(connection != null && !connection.isClosed()){
            PreparedStatement ps = connection.prepareStatement("UPDATE Reviews set Comment = ? and Rating = ? and Stamp = ?  where id = ?");
            ps.setString(1, review.getComment());
            ps.setDouble(2, review.getRating());
            ps.setTimestamp(3, review.getTimestamp());
            ps.setInt(4, review.getId());
            ps.executeUpdate();
            calculateAverageRating(review.getCourse());
        }
    }



}
