package edu.virginia.sde.reviews;

import java.sql.Timestamp;

public class MyReviewObject{

    private String mnemonic;
    private int courseNum;
    private double rating;


    public MyReviewObject(String mnemonic, int courseNum, double rating) {
       this.mnemonic = mnemonic;
       this.courseNum = courseNum;
       this.rating = rating;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    public int getCourseNum() {
        return courseNum;
    }

    public void setCourseNum(int courseNum) {
        this.courseNum = courseNum;
    }

    @Override
    public String toString() {
        return "MyReviewObject [mnemonic=" + mnemonic + ", courseNum=" + courseNum + ", rating=" + rating + "]";
    }
}
