package datawarehouse.aggerate;
import datawarehouse.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ConnectAggregate {
    private static DatabaseConnection db;
    private static int lastInsertedProvinceId = -1;
    private static int lastInsertedRegionId = -1;
    private static int lastInsertedResultDateId = -1;
    private static int lastInsertedPrizeId = -1;
    private static boolean connectDatabase() {
        try {
            db = new DatabaseConnection("jdbc:sqlite:D:/DW/aggregate.db");
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
    public static void insertXoSoResult(XoSoResult xoSoResult) {
        try {
            storeDimRegion(xoSoResult);
            storeDimProvince(xoSoResult);
            storeDimResultDate(xoSoResult);
            storeDimPrize(xoSoResult);
            storeFactXoSoResults(xoSoResult);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void storeDimRegion(XoSoResult xoSoResult) throws SQLException {
        String query = "INSERT INTO DimRegion (RegionName) VALUES (?)";
        try (PreparedStatement preparedStatement = db.getConn().prepareStatement(query)) {
            preparedStatement.setString(1, xoSoResult.getProvinceName());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        lastInsertedRegionId = generatedKeys.getInt(1);
                    }
                }
            }
        }
    }

    private static void storeDimProvince(XoSoResult xoSoResult) throws SQLException {
        String query = "INSERT INTO DimProvince (ProvinceName) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = db.getConn().prepareStatement(query)) {
            preparedStatement.setString(1, xoSoResult.getProvinceName());
            preparedStatement.setInt(2, lastInsertedRegionId);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        lastInsertedProvinceId = generatedKeys.getInt(1);
                    }
                }
            }
        }
    }

    private static void storeDimResultDate(XoSoResult xoSoResult) throws SQLException {
        String query = "INSERT INTO DimResultDate (ResultDate) VALUES (?)";
        try (PreparedStatement preparedStatement = db.getConn().prepareStatement(query)) {
            preparedStatement.setDate(1, new java.sql.Date(xoSoResult.getResultDate().getTime()));
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        lastInsertedResultDateId = generatedKeys.getInt(1);
                    }
                }
            }
        }
    }

    private static void storeDimPrize(XoSoResult xoSoResult) throws SQLException {
        String query = "INSERT INTO DimPrize (PrizeName, PrizeAmount) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = db.getConn().prepareStatement(query)) {
            preparedStatement.setString(1, xoSoResult.getPrizeName());
            preparedStatement.setBigDecimal(2, xoSoResult.getPrizeAmount());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        lastInsertedPrizeId = generatedKeys.getInt(1);
                    }
                }
            }
        }
    }

    private static void storeFactXoSoResults(XoSoResult xoSoResult) throws SQLException {
        String query = "INSERT INTO FactXoSoResults (ProvinceID, ResultDateID, PrizeID, WinningNumbers, WinnerCount) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = db.getConn().prepareStatement(query)) {
            preparedStatement.setInt(1, lastInsertedProvinceId);
            preparedStatement.setInt(2, lastInsertedResultDateId);
            preparedStatement.setInt(3, lastInsertedPrizeId);
            preparedStatement.setString(4, xoSoResult.getWinningNumbers());
            preparedStatement.setInt(5, xoSoResult.getWinnerCount());
            preparedStatement.executeUpdate();
        }
    }

    public static void main(String[] args) {
        GetDataFromDTWHouse.connectDatabase();
        ArrayList<XoSoResult> xoSoResults = GetDataFromDTWHouse.getXoSoResults();
        if(xoSoResults.size()< 1 ) {
            GetDataFromDTWHouse.messNotFind();
            return;
        }
        ConnectAggregate.connectDatabase();
        for (XoSoResult xoSoResult : xoSoResults) {
            System.out.println(xoSoResult);
            ConnectAggregate.insertXoSoResult(xoSoResult);
        }
    }
}
