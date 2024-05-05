package edu.virginia.sde.reviews;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;

public class Course {
    /*
     * This java class will represent the courses
     * It will contain instance variables for
     * Unique ID
     * Course Number
     * Mnemonic
     * Title
     * and the avg rating
     * */
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private int id;
    private int courseNumber;
    private String mnemonic;
    private String title;
    private String averageRating;
    public Course(int id, int courseNumber, String mnemonic, String title, double averageRating) {
        this.id = id;
        this.courseNumber = courseNumber;
        this.mnemonic = mnemonic;
        this.title = title;
        this.averageRating = df.format(averageRating);
    }
    public Course(int courseNumber, String mnemonic, String title, double averageRating) {
        this.id = -999;
        this.courseNumber = courseNumber;
        this.mnemonic = mnemonic;
        this.title = title;
        this.averageRating = df.format(averageRating);
    }
    public Course(int courseNumber, String mnemonic, String title) {
        //this.id = id;
        this.courseNumber = courseNumber;
        this.mnemonic = mnemonic;
        this.title = title;
        this.averageRating = df.format(0.0);
    }

    public Course() { }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCourseNumber() {
        return courseNumber;
    }

    public void setCourseNumber(int courseNumber) {
        this.courseNumber = courseNumber;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double avgRating) {
       averageRating = df.format(avgRating);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Course course = (Course) o;
        return id == course.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "Course{" + "id = " + id +
                ", mnemonic = " + mnemonic +
                ", courseNumber = " + courseNumber +
                ", title = " + title +
                ", averageRating = " + averageRating +
                '}';
    }
}
