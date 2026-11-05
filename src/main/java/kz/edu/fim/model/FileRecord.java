package kz.edu.fim.model;

public class FileRecord {

    private final String path;
    private final String sha256;
    private final long size;
    private final long lastModified;

    public FileRecord(String path, String sha256,
                      long size, long lastModified) {
        this.path = path;
        this.sha256 = sha256;
        this.size = size;
        this.lastModified = lastModified;
    }

    public String getPath() {
        return path;
    }

    public String getSha256() {
        return sha256;
    }

    public long getSize() {
        return size;
    }

    public long getLastModified() {
        return lastModified;
    }
}