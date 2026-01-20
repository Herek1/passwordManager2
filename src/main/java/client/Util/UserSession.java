package client.Util;

import client.Users.User;

public class UserSession {
    private static User currentUser;

    // Set the current logged-in user
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    // Get the current logged-in user
    public static User getCurrentUser() {
        return currentUser;
    }

    // Check if there is a logged-in user
    public static boolean isUserLoggedIn() {
        return currentUser != null;
    }

    // Clear the session
    public static void clearSession() {
        currentUser = null;
    }
}