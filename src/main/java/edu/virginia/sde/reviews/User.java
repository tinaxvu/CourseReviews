package edu.virginia.sde.reviews;

public class User {
    /*
     * this java class will represent the user object
     * It will contain the instance variables for
     * ID(unique per user)
     * Username
     * Password
     * and potentially others
     */
    private int id;
    private String username;
    private String password;
    public User (String username, String password){
        this.id = -999;
        this.username = username;
        this.password = password;
    }
    public User(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    @Override
    public String toString() {
        return this.username;
    }
}
