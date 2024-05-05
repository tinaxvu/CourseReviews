package edu.virginia.sde.reviews;

import java.sql.Timestamp;

public class MyReviewObject{

    private String mnemonic;
    private int courseNum;
    private int id;
    private int userID;
    private int courseID;
    private String reviewComment;
    private double rating;
    private Timestamp timestamp;

    public MyReviewObject(int id, String mnemonic, int courseNum, int userID, int courseID, String reviewComment, double rating, Timestamp timestamp) {
       this.mnemonic = mnemonic;
       this.courseNum = courseNum;
       this.id = id;
       this.userID = userID;
       this.courseID = courseID;
       this.reviewComment = reviewComment;
       this.rating = rating;
       this.timestamp = timestamp;
    }



}
