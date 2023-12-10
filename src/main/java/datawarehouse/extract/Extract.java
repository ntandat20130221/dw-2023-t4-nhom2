package datawarehouse.extract;

import datawarehouse.DatabaseConnection;
import datawarehouse.PropertyManager;
import datawarehouse.Utils;
import datawarehouse.models.Config;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static datawarehouse.PropertyManager.*;

public class Extract {
    private static PropertyManager prop;
    private static DatabaseConnection conn;

    public static void main(String[] args) {
        // 1. Load properties file.
        var isLoadPropertiesSuccessful = loadProperties();
        if (!isLoadPropertiesSuccessful) return;

        // 2. Connect to control database.
        var isConnectSuccessful = connectControlDatabase();
        if (!isConnectSuccessful) return;

        // 3. Read a row from process_control table where process_name == 'extract'.
        var process = conn.getProcess(prop.get(PROP_PROCESS_EXTRACT));
        if (process == null) {
            // 4. If there is no row, insert a new row.
            conn.insertProcess(prop.get(PROP_PROCESS_EXTRACT), prop.get(PROP_BEGIN_EXTRACT));
        } else {
            // 5. Check if start_time == today && status == BE || CE.
            if (Utils.isToday(process.startTime) &&
                    (process.status.equals(prop.get(PROP_BEGIN_EXTRACT)) || process.status.equals(prop.get(PROP_COMPLETE_EXTRACT)))) {
                return;
            } else {
                // 6. Update process_control table.
                conn.updateProcessStatus(prop.get(PROP_PROCESS_EXTRACT), prop.get(PROP_BEGIN_EXTRACT));
            }
        }
        // 7. Read config data from config table.
        var config = conn.getConfig();
        if (config == null) return;

        // 8. Using config to crawl data from source.
        String data = crawlData(config, args);
        if (data == null) {
            // 9. If crawling is failed, update process status to FAIL_EXTRACT.
            conn.updateProcessStatus(prop.get(PROP_PROCESS_EXTRACT), prop.get(PROP_FAIL_EXTRACT));
            return;
        }
        // 10. Write data to .csv file.
        File newFile = writeDataToCsv(config, data);
        if (newFile == null) {
            conn.updateProcessStatus(prop.get(PROP_PROCESS_EXTRACT), prop.get(PROP_FAIL_EXTRACT));
            return;
        }

        // 11. Delete other .csv files.
        deleteOtherCsvFiles(config, newFile);

        // 12. Update process status to COMPLETE_EXTRACT.
        conn.updateProcessStatus(prop.get(PROP_PROCESS_EXTRACT), prop.get(PROP_COMPLETE_EXTRACT));

        // 13. Close control database.
        conn.close();
    }

    private static boolean loadProperties() {
        try {
            prop = new PropertyManager();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static boolean connectControlDatabase() {
        try {
            conn = new DatabaseConnection(prop.get(PROP_CONTROL_DB_URL));
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private static String crawlData(Config config, String[] args) {
        var crawler = new Crawler();
        return crawler.crawl(config, args);
    }

    private static File writeDataToCsv(Config config, String data) {
        try {
            String formattedDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern(config.filePattern));
            String filePath = config.fileDestination + formattedDateTime + config.fileFormat;
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            var pw = new PrintWriter(file, StandardCharsets.UTF_8);
            pw.write(data);
            pw.close();
            return file;
        } catch (Exception e) {
            return null;
        }
    }

    private static void deleteOtherCsvFiles(Config config, File newFile) {
        try {
            for (File file : Objects.requireNonNull(new File(config.fileDestination).listFiles())) {
                if (!file.getName().equals(newFile.getName())) {
                    Files.delete(file.toPath());
                }
            }
        } catch (IOException ignored) {
        }
    }
}
