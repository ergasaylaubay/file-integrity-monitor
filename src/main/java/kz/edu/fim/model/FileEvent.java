package kz.edu.fim.model;

import java.time.LocalDateTime;

public class FileEvent {

    private final String filePath;
    private final EventType eventType;
    private final String oldHash;
    private final String newHash;
    private final LocalDateTime detectedAt;

    public FileEvent(String filePath, EventType eventType,
                     String oldHash, String newHash) {
        this.filePath = filePath;
        this.eventType = eventType;
        this.oldHash = oldHash;
        this.newHash = newHash;
        this.detectedAt = LocalDateTime.now();
    }

    public String getFilePath() {
        return filePath;
    }

    public EventType getEventType() {
        return eventType;
    }

    public String getOldHash() {
        return oldHash;
    }

    public String getNewHash() {
        return newHash;
    }

    public LocalDateTime getDetectedAt() {
        return detectedAt;
    }
}