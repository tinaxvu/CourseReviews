package edu.virginia.sde.reviews;
import java.sql.Timestamp;

public class Review {
    /*
     * This class will represent the reviews
     * Each review will have a
     * User
     * Course
     * String reviewComment
     * Rating
     * Timestamp (added by Andrew)
     */

    //
    private int id;
    private User user;
    private Course course;
    private String reviewComment;
    private double rating;
    private Timestamp timestamp;

    public Review(User user, Course course, String reviewComment, double rating, Timestamp timestamp) {
        this.user = user;
        this.course = course;
        this.reviewComment = reviewComment;
        this.rating = rating;
        this.timestamp = timestamp;
        this.id = -999;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getComment() {
        return reviewComment;
    }

    public void setComment(String comment) {
        this.reviewComment = comment;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public void setId(int id) {this.id = id;}

    @Override
    public String toString() {
        return "Review{" +
                "user=" + user +
                ", course=" + course +
                ", comment='" + reviewComment + '\'' +
                ", rating=" + rating +
                ", timestamp=" + timestamp +
                '}';
    }
}
