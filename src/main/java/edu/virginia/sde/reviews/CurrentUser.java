package edu.virginia.sde.reviews;

public class CurrentUser {
    private static CurrentUser currentUser = null;
    private final String username;

    private CurrentUser(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public static CurrentUser getInstance() {
        if(currentUser == null) {
            throw new AssertionError("You have to call init first");
        }

        return currentUser;
    }

    public synchronized static CurrentUser init(String username) {
        currentUser = new CurrentUser(username);
        return currentUser;
    }
}
