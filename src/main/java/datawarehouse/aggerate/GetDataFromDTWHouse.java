package datawarehouse.aggerate;

import datawarehouse.DatabaseConnection;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;


public class GetDataFromDTWHouse {
    private static DatabaseConnection db;


    static boolean connectDatabase() {
        try {
            db = new DatabaseConnection("jdbc:sqlite:D:/DW/datawarehouse.db");
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
    public static ArrayList<XoSoResult> getXoSoResults() {
        ArrayList<XoSoResult> xoSoResults = new ArrayList<>();
        try {
            String query = "SELECT P.ProvinceName, XR.ResultDate, PR.PrizeName, XR.WinningNumbers, XR.PrizeAmount, XR.WinnerCount " +
                    "FROM XoSoResults XR " +
                    "JOIN Provinces P ON XR.ProvinceID = P.ProvinceID " +
                    "JOIN Prizes PR ON XR.PrizeID = PR.PrizeID";
            try (Statement statement = db.getConn().createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
                while (resultSet.next()) {
                    String provinceName = resultSet.getString("provinceName");
                    Date resultDate = resultSet.getDate("resultDate");
                    String prizeName = resultSet.getString("prizeName");
                    String winningNumbers = resultSet.getString("winningNumbers");
                    BigDecimal prizeAmount = resultSet.getBigDecimal("prizeAmount");
                    int winnerCount = resultSet.getInt("winnerCount");

                    XoSoResult xoSoResult = new XoSoResult(provinceName, resultDate, prizeName, winningNumbers, prizeAmount, winnerCount);

                    // Thêm đối tượng vào danh sách
                    xoSoResults.add(xoSoResult);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return xoSoResults;
    }

}
