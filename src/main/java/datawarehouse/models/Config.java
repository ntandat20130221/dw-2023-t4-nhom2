package datawarehouse.models;

public class Config {
    public int id;
    public String sourceUrl;
    public String sourceSuffix;
    public String fileFormat;
    public String filePattern;
    public String fileDestination;
    public String fileDelimiter;
    public String dateFormat;
    public String userAgent;
    public String description;

    public Config(int id, String sourceUrl, String sourceSuffix, String fileFormat, String filePattern,
                  String fileDestination, String fileDelimiter, String dateFormat, String userAgent, String description) {
        this.id = id;
        this.sourceUrl = sourceUrl;
        this.sourceSuffix = sourceSuffix;
        this.fileFormat = fileFormat;
        this.filePattern = filePattern;
        this.fileDestination = fileDestination;
        this.fileDelimiter = fileDelimiter;
        this.dateFormat = dateFormat;
        this.userAgent = userAgent;
        this.description = description;
    }
}
