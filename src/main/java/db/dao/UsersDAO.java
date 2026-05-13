package db.dao;

import db.error.handlers.ErrorHandler;
import db.utils.Message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UsersDAO {
    private final Connection conn;
    private final Message message = new Message();
    private final ErrorHandler errorHandler = new ErrorHandler();

    private static final String ROOT_USERNAME = "root";

    public UsersDAO(Connection conn) {
        this.conn = conn;
    }

    public List<HashMap<String, String>> createUser(String username, String role) {
        String query = """
        INSERT INTO users (username, role)
        VALUES (?, ?)
        """;
        List<HashMap<String, String>> result = new ArrayList<>();

        HashMap<String, String> staticInfo1 = new HashMap<>(message.getDefaultErrorMessageAsHashMap());

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, role);
            stmt.executeUpdate();
        } catch (SQLException e) {
            staticInfo1 = errorHandler.handleSQLException(e, staticInfo1, message);
        }
        result.add(staticInfo1);
        return result;
    }

    public List<HashMap<String, String>> updateUserPassword(String login, String newPassword) {
        List<HashMap<String, String>> infoList = new ArrayList<>();

        HashMap<String, String> staticInfo1 = new HashMap<>(message.getDefaultErrorMessageAsHashMap());
        infoList.add(staticInfo1);
        String query = "UPDATE users SET master_password = ? WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newPassword);
            stmt.setString(2, login);

            HashMap<String, String> staticInfo2 = new HashMap<>();
            if (stmt.executeUpdate() > 0) {
                staticInfo2.put("success", "true");
            } else {
                staticInfo1.replace(message.getHashIdStatus(), "error");
                staticInfo1.replace(message.getHashIdUserFriendlyError(), "User password could not be set");
            }
            infoList.add(staticInfo2);
        } catch (SQLException e) {
            staticInfo1 = errorHandler.handleSQLException(e, staticInfo1, message);
        }
        infoList.set(0, staticInfo1);
        return infoList;
    }

    public List<HashMap<String, String>> deleteUser(String username) {

        List<HashMap<String, String>> result = new ArrayList<>();

        HashMap<String, String> staticInfo1 =
                new HashMap<>(message.getDefaultErrorMessageAsHashMap());

        result.add(staticInfo1);

        String query = """
        DELETE FROM users
        WHERE username = ?
        AND username <> ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, ROOT_USERNAME);
            int affected = stmt.executeUpdate();

            if (affected == 0) {
                staticInfo1.replace(message.getHashIdStatus(), "error");
                staticInfo1.replace(message.getHashIdUserFriendlyError(), "User was not deleted");
            } else {
                staticInfo1.replace(message.getHashIdStatus(), "success");
            }

        } catch (SQLException e) {
            staticInfo1 = errorHandler.handleSQLException(e, staticInfo1, message);
            result.set(0, staticInfo1);
        }

        return result;
    }

//    public List<HashMap<String, String>> isUserValid(String login, String password) {
//        List<HashMap<String, String>> userList = new ArrayList<>();
//
//        HashMap<String, String> staticInfo1 = new HashMap<>(message.getDefaultErrorMessageAsHashMap());
//
//        String query = "SELECT login, password FROM users WHERE login = ? AND password = ?";
//        try (PreparedStatement stmt = conn.prepareStatement(query)) {
//            stmt.setString(1, login);
//            stmt.setString(2, password);
//
//            try (ResultSet rs = stmt.executeQuery()) {
//                HashMap<String, String> user = new HashMap<>();
//                if (rs.next()) {
//                    user.put("exists", "true");
//                } else {
//                    staticInfo1.replace(message.getHashIdStatus(), "error");
//                    staticInfo1.replace(message.getHashIdException(), "not nown exception");
//                    staticInfo1.replace(message.getHashIdErrorMessage(), "There is no user in this database");
//                    staticInfo1.replace(message.getHashIdUserFriendlyError(), "User does not exist");
//                }
//                userList.add(user);
//            }
//        } catch (SQLException e) {
//            staticInfo1 = errorHandler.handleSQLException(e, staticInfo1, message);
//        }
//        userList.add(staticInfo1);
//        return userList;
//    }

    public List<HashMap<String, String>> getUser(String username, String password) {
        List<HashMap<String, String>> userList = new ArrayList<>();

        HashMap<String, String> staticInfo1 = new HashMap<>(message.getDefaultErrorMessageAsHashMap());
        userList.add(staticInfo1);
        String query = "SELECT id,username, role FROM users WHERE username = ? AND master_password = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                HashMap<String, String> user = new HashMap<>();
                while (rs.next()) {
                    user.put("id", rs.getString("id"));
                    user.put("username", rs.getString("username"));
                    user.put("role", rs.getString("role"));
                    userList.add(user);
                }
                if (user.isEmpty()) {
                    staticInfo1.replace(message.getHashIdStatus(), "error");
                    staticInfo1.replace(message.getHashIdUserFriendlyError(), "There is no user for that password and login");
                    userList.set(0, staticInfo1);
                }
            }
        } catch (SQLException e) {
            staticInfo1 = errorHandler.handleSQLException(e, staticInfo1, message);
            userList.set(0, staticInfo1);
        }
        return userList;
    }

    public List<HashMap<String, String>> getUserByUsername(String username, String role) {
        List<HashMap<String, String>> result = new ArrayList<>();
        HashMap<String, String> staticInfo = new HashMap<>(message.getDefaultErrorMessageAsHashMap());
        result.add(staticInfo);

        String query = """
            SELECT id, username, role
            FROM users
            WHERE username ILIKE ?
            AND role ILIKE ?
            AND username <> ?
            ORDER BY username
        """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            if (username == null || username.isBlank()) {
                stmt.setString(1, "%");
            } else {
                stmt.setString(1, "%" + username + "%");
            }
            if (role == null || role.isBlank()) {
                stmt.setString(2, "%");
            } else {
                stmt.setString(2, "%" + role + "%");
            }
            stmt.setString(3, ROOT_USERNAME);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    HashMap<String, String> row = new HashMap<>();
                    row.put("id", rs.getString("id"));
                    row.put("username", rs.getString("username"));
                    row.put("role", rs.getString("role"));
                    result.add(row);
                }
            }

            if (result.size() == 1) {
                staticInfo.replace(message.getHashIdStatus(), "error");
                staticInfo.replace(message.getHashIdUserFriendlyError(), "No users found");
            } else {
                staticInfo.replace(message.getHashIdStatus(), "success");
            }

        }catch (SQLException e) {
            staticInfo = errorHandler.handleSQLException(e, staticInfo, message);
            result.set(0, staticInfo);
        }

        return result;
    }
}