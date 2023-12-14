package datawarehouse;

import java.io.IOException;

public class ArgumentManagerImpl implements ArgumentManager {
    private final String[] args;
    private PropertyManager prop;

    public ArgumentManagerImpl(String[] args) {
        this.args = args;
        loadProperties();
    }

    @Override
    public String getDate() {
        if (prop == null) return null;
        for (String arg : args) {
            String[] dateArg = arg.split(PropertyManager.PROP_DMT);
            if (dateArg[0].equals(prop.get(PropertyManager.PROP_ARG_DATE))) {
                return dateArg[1];
            }
        }
        return null;
    }

    @Override
    public int getSourceId() {
        if (prop == null) return -1;
        for (String arg : args) {
            String[] sourceIdArg = arg.split(PropertyManager.PROP_DMT);
            if (sourceIdArg[0].equals(prop.get(PropertyManager.PROP_ARG_SOURCE_ID))) {
                return Utils.tryParseInt(sourceIdArg[1]);
            }
        }
        return -1;
    }

    private void loadProperties() {
        try {
            prop = new PropertyManager();
        } catch (IOException ignored) {
            prop = null;
        }
    }
}
