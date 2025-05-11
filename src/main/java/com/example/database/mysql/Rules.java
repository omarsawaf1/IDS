package com.example.database.mysql;

import com.example.concurrent.DatabaseConnectionBools;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class Rules {
    private final DataSource dataSource;

    public Rules() {
        this.dataSource = DatabaseConnectionBools.getDataSource();
    }

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public boolean insert(String protocol, String srcIP, String dstPort, String dstMac, String srcMac, String srcPort, String dstIP, int userid) {
        String sql = "INSERT INTO rules (protocol, srcIP, dstPort, dstMac, srcMac, srcPort, dstIP, userid) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, protocol);
            ps.setString(2, srcIP);
            ps.setString(3, dstPort);
            ps.setString(4, dstMac);
            ps.setString(5, srcMac);
            ps.setString(6, srcPort);
            ps.setString(7, dstIP);
            ps.setInt(8, userid);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    //add one that select all rules based on userid
    public List<Map<String, Object>> searchUserRulesList(int userId) {
        String sql = "SELECT * FROM rules WHERE userid = ?";
        // this list will hold alot hashmaps
        //[{"des":"fls","port":200},{},{},{}]
        List<Map<String, Object>> results = new ArrayList<>();

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData meta = rs.getMetaData();
                int columnCount = meta.getColumnCount();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.put(meta.getColumnName(i), rs.getObject(i));
                    }
                    results.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }
    public Map<String, Object> search(int rulesid) {
        String sql = "SELECT * FROM rules WHERE rulesid = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, rulesid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ResultSetMetaData meta = rs.getMetaData();
                    int columnCount = meta.getColumnCount();
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.put(meta.getColumnName(i), rs.getObject(i));
                    }
                    return row;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; 
    }

    public boolean delete(int rulesid) {
        String sql = "DELETE FROM rules WHERE rulesid = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, rulesid);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
