package kz.edu.fim.config;

public class MonitorConfig {

    private final String directoryPath;
    private final String databasePath;

    public MonitorConfig(String directoryPath, String databasePath) {
        this.directoryPath = directoryPath;
        this.databasePath = databasePath;
    }

    public String getDirectoryPath() {
        return directoryPath;
    }

    public String getDatabasePath() {
        return databasePath;
    }
}