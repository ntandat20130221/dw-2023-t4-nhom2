package datawarehouse;

import datawarehouse.extract.Extract;

import java.io.IOException;
import java.util.Properties;

public class PropertyManager {
    private static final String PROPERTIES_FILE = "/config.properties";

    public static final String PROP_CONTROL_DB_URL = "control_db_url";
    public static final String PROP_PROCESS_EXTRACT = "process_extract";
    public static final String PROP_BEGIN_EXTRACT = "begin_extract";
    public static final String PROP_FAIL_EXTRACT = "fail_extract";
    public static final String PROP_COMPLETE_EXTRACT = "complete_extract";

    private final Properties prop;

    public PropertyManager() throws IOException {
        prop = new Properties();
        prop.load(Extract.class.getResourceAsStream(PROPERTIES_FILE));
    }

    public String get(String key) {
        return prop.getProperty(key);
    }
}
