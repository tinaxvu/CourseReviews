package edu.virginia.sde.reviews;

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

    private int id;
    private int courseNumber;
    private String mnemonic;
    private String title;
    private double averageRating;
    public Course(int id, int courseNumber, String mnemonic, String title, double averageRating) {
        this.id = id;
        this.courseNumber = courseNumber;
        this.mnemonic = mnemonic;
        this.title = title;
        this.averageRating = averageRating;
    }
    public Course(int courseNumber, String mnemonic, String title, double averageRating) {
        this.id = -999;
        this.courseNumber = courseNumber;
        this.mnemonic = mnemonic;
        this.title = title;
        this.averageRating = averageRating;
    }
    public Course(int courseNumber, String mnemonic, String title) {
        //this.id = id;
        this.courseNumber = courseNumber;
        this.mnemonic = mnemonic;
        this.title = title;
        this.averageRating = 0.0;
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

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
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
