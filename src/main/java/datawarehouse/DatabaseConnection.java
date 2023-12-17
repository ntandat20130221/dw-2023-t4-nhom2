package datawarehouse;

import datawarehouse.models.Config;
import datawarehouse.models.ProcessControl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private final Connection conn;

    public DatabaseConnection(String url) throws SQLException {
        conn = DriverManager.getConnection(url);
    }

    public Connection getConn() {
        return conn;
    }

    public Config getConfig(int sourceId) {
        try {
            var stm = conn.prepareStatement("SELECT * FROM config WHERE id = ?");
            stm.setInt(1, sourceId);
            var rs = stm.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                String sourceUrl = rs.getString(2);
                String sourceSuffix = rs.getString(3);
                String fileFormat = rs.getString(4);
                String filePattern = rs.getString(5);
                String fileDestination = rs.getString(6);
                String fileDelimiter = rs.getString(7);
                String dateFormat = rs.getString(8);
                String userAgent = rs.getString(9);
                String description = rs.getString(10);
                return new Config(id, sourceUrl, sourceSuffix, fileFormat, filePattern, fileDestination,
                        fileDelimiter, dateFormat, userAgent, description);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public ProcessControl getProcess(String process) {
        try {
            var stm = conn.prepareStatement("SELECT * FROM process_control WHERE process_name = ?");
            stm.setString(1, process);
            var rs = stm.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                String processName = rs.getString(2);
                String status = rs.getString(3);
                long startTime = rs.getDate(4).getTime();
                return new ProcessControl(id, processName, status, startTime);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public void insertProcess(String processName, String status) {
        try {
            var stm = conn.prepareStatement("INSERT INTO process_control(process_name, status) VALUES (?, ?)");
            stm.setString(1, processName);
            stm.setString(2, status);
            stm.execute();
        } catch (Exception ignored) {
        }
    }

    public void updateProcessStatus(String processName, String status) {
        try {
            var stm = conn.prepareStatement("UPDATE process_control SET status = ?, start_time = datetime('now', 'localtime') " +
                    "WHERE process_name = ?");
            stm.setString(1, status);
            stm.setString(2, processName);
            stm.execute();
        } catch (Exception ignored) {
        }
    }

    public void close() {
        try {
            conn.close();
        } catch (SQLException ignored) {
        }
    }
}
