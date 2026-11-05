package kz.edu.fim.notification;

import kz.edu.fim.model.FileEvent;

import java.io.IOException;
import java.nio.file.Path;

public class EventLogger {

    private final Path logFilePath;

    public EventLogger(Path logFilePath) {
        this.logFilePath = logFilePath;
    }

    public void log(FileEvent event) throws IOException {
        // Оқиғаны журналға жазу кейін іске асырылады.
        throw new UnsupportedOperationException(
                "Журналға жазу әзірге іске асырылмаған"
        );
    }
}