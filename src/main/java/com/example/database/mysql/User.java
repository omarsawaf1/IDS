package com.example.database.mysql;

import com.example.concurrent.DatabaseConnectionBools;

import javax.sql.DataSource;
import java.sql.*;

public class User {

    private final DataSource dataSource;
    private static int userId;
    public User() {
        this.dataSource = DatabaseConnectionBools.getDataSource();
    }
    public static int getUserId() {
        return userId;
    }
    public int signup(String username, String password) {
        String sql = "INSERT INTO user (password, username) VALUES (?, ?)";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, password);
            ps.setString(2, username);
            int rowsAffected = ps.executeUpdate();
            //retuning the user id
            if (rowsAffected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        userId = rs.getInt(1);
                        return userId;
                    }
                }
            }
            return -1;

        } catch (SQLException e) {
            System.err.println("Signup failed: " + e.getMessage());
            return -1;
        }
    }

    public int login(String username, String password) {
        String sql = "SELECT userid FROM user WHERE username = ? AND password = ?";

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    userId = rs.getInt("userid");
                    return userId;
                }
            }
            return -1;

        } catch (SQLException e) {
            System.err.println("Login failed: " + e.getMessage());
            return -1;
        }
    }

    public boolean delete(int rulesid) {
        String sql = "DELETE FROM user WHERE rulesid = ?";

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, rulesid);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Delete failed: " + e.getMessage());
            return false;
        }
    }
}



