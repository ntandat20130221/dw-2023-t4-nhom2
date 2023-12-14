package datawarehouse;

import datawarehouse.extract.Extract;

import java.io.IOException;
import java.util.Properties;

public class PropertyManager {
    private static final String PROPERTIES_FILE = "/config.properties";

    public static final String PROP_CONTROL_DB_URL = "control_db_url";
    public static final String PROP_SOURCE_ID = "source_id";
    public static final String PROP_ARG_DATE = "arg_date";
    public static final String PROP_ARG_SOURCE_ID = "arg_source_id";
    public static final String PROP_PROCESS_EXTRACT = "process_extract";
    public static final String PROP_BEGIN_EXTRACT = "begin_extract";
    public static final String PROP_FAIL_EXTRACT = "fail_extract";
    public static final String PROP_COMPLETE_EXTRACT = "complete_extract";
    public static final String PROP_DMT = "=";

    private final Properties prop;

    public PropertyManager() throws IOException {
        prop = new Properties();
        prop.load(Extract.class.getResourceAsStream(PROPERTIES_FILE));
    }

    public String get(String key) {
        return prop.getProperty(key);
    }
}
