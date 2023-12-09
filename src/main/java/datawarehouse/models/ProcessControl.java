package datawarehouse.models;

public class ProcessControl {
    public int id;
    public String process_name;
    public String status;
    public long startTime;

    public ProcessControl(int id, String process_name, String status, long startTime) {
        this.id = id;
        this.process_name = process_name;
        this.status = status;
        this.startTime = startTime;
    }
}
