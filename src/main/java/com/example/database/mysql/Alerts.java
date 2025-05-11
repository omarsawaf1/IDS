package com.example.database.mysql;
import com.example.concurrent.DatabaseConnectionBools;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class Alerts {
    private final DataSource dataSource;

    
    public Alerts() {
        this.dataSource = DatabaseConnectionBools.getDataSource();
    }

    public boolean insert(String protocol, String srcIP, String dstPort, String dstMac,
                          String srcMac, String srcPort, String dstIP, int userid, int rulesid) {
        String sql = "INSERT INTO alerts (protocol, srcMac, srcIP, srcPort, dstMac, dstIP, dstPort, userid, rulesid) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        //to prevent sql injection we use prepared statement
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, protocol);
            ps.setString(2, srcIP);
            ps.setString(3, dstPort);
            ps.setString(4, dstMac);
            ps.setString(5, srcMac);
            ps.setString(6, srcPort);
            ps.setString(7, dstIP);
            ps.setInt(8, userid);
            ps.setInt(9, rulesid);

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (Exception e) {
            System.out.println("Insert failed: " + e);
            return false;
        }
    }

    public Map<String, Object> search(int packetid) {
        String sql = "SELECT * FROM alerts WHERE packetid = ?";

        try (Connection con = dataSource.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, packetid);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                ResultSetMetaData meta = rs.getMetaData();
                int columnCount = meta.getColumnCount();
                Map<String, Object> row = new HashMap<>();

                for (int i = 1; i <= columnCount; i++) {
                    row.put(meta.getColumnName(i), rs.getObject(i));
                }

                return row;
            }

        } catch (SQLException e) {
            System.out.println("Search failed: " + e);
        }

        return null; 
    }


    public boolean delete(int packetid) {
        String sql = "DELETE FROM alerts WHERE packetid = ?";

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, packetid);
            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (Exception e) {
            System.out.println("Delete failed: " + e);
            return false;
        }
    }
}
