package datawarehouse.extract;

import datawarehouse.ArgumentManagerImpl;
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
import java.text.SimpleDateFormat;
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
        var arg = new ArgumentManagerImpl(args);
        int sourceId = arg.getSourceId() != -1 ? arg.getSourceId() : Utils.tryParseInt(prop.get(PROP_SOURCE_ID));
        var config = conn.getConfig(sourceId);
        if (config == null) return;

        // 8. Using config to crawl data from source.
        String date = arg.getDate() == null ? new SimpleDateFormat(config.dateFormat).format(System.currentTimeMillis()) : arg.getDate();
        String data = crawlData(config, date);
        if (data == null) {
            // 9. If crawling is failed, update process status to FAIL_EXTRACT.
            conn.updateProcessStatus(prop.get(PROP_PROCESS_EXTRACT), prop.get(PROP_FAIL_EXTRACT));
            return;
        }

        var file = new File(getFilePath(config));
        if (!file.exists()) {
            // 10. If the folder doesn't exist, create a new one.
            if (!file.getParentFile().mkdirs()) {
                conn.updateProcessStatus(prop.get(PROP_PROCESS_EXTRACT), prop.get(PROP_FAIL_EXTRACT));
                return;
            }
        }

        // 11. Write data to .csv file.
        File newFile = writeDataToCsv(file, data);
        if (newFile == null) {
            // 12. If writing is failed, update process status to FAIL_EXTRACT.
            conn.updateProcessStatus(prop.get(PROP_PROCESS_EXTRACT), prop.get(PROP_FAIL_EXTRACT));
            return;
        }

        // 13. Delete other .csv files.
        deleteOtherCsvFiles(config, newFile);

        // 14. Update process status to COMPLETE_EXTRACT.
        conn.updateProcessStatus(prop.get(PROP_PROCESS_EXTRACT), prop.get(PROP_COMPLETE_EXTRACT));

        // 15. Close control database.
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

    private static String crawlData(Config config, String date) {
        var crawler = new CrawlerImpl();
        return crawler.crawl(config, date);
    }

    private static String getFilePath(Config config) {
        String formattedDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern(config.filePattern));
        return config.fileDestination + formattedDateTime + config.fileFormat;
    }

    private static File writeDataToCsv(File file, String data) {
        try {
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
