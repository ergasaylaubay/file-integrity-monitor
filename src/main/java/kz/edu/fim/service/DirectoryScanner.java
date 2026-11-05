package kz.edu.fim.service;

import kz.edu.fim.model.FileRecord;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class DirectoryScanner {

    private final HashService hashService;

    public DirectoryScanner(HashService hashService) {
        this.hashService = hashService;
    }

    public List<FileRecord> scan(Path directoryPath)
            throws IOException {
        // Каталогты сканерлеу келесі кезеңде іске асырылады.
        throw new UnsupportedOperationException(
                "Каталогты сканерлеу әзірге іске асырылмаған"
        );
    }
}