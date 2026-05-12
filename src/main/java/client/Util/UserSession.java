package client.Util;

import client.Users.User;

public class UserSession {
    private static User currentUser;
    private static String pendingPassword;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static void setPendingPassword(String pwd) {
        pendingPassword = pwd;
    }

    public static String clearPendingPassword() {
        String pwd = pendingPassword;
        pendingPassword = null;
        return pwd;
    }
    public static User getCurrentUser() {
        return currentUser;
    }
    public static boolean isUserLoggedIn() {
        return currentUser != null;
    }
    public static void clearSession() {
        currentUser = null;
    }
}