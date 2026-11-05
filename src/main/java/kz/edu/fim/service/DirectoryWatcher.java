package kz.edu.fim.service;

import kz.edu.fim.model.FileEvent;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;

public class DirectoryWatcher {

    public void watch(Path directoryPath,
                      Consumer<FileEvent> onEvent)
            throws IOException, InterruptedException {

        // WatchService арқылы бақылау кейін іске асырылады.
        throw new UnsupportedOperationException(
                "Каталогты бақылау әзірге іске асырылмаған"
        );
    }
}