package kz.edu.fim.repository;

import kz.edu.fim.model.FileRecord;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class FileRepository {

    private final DatabaseManager databaseManager;

    public FileRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void save(FileRecord fileRecord) throws SQLException {
        throw new UnsupportedOperationException(
                "Файл мәліметін сақтау әзірге іске асырылмаған"
        );
    }

    public Optional<FileRecord> findByPath(String path)
            throws SQLException {
        throw new UnsupportedOperationException(
                "Файлды іздеу әзірге іске асырылмаған"
        );
    }

    public List<FileRecord> findAll() throws SQLException {
        throw new UnsupportedOperationException(
                "Файлдар тізімін алу әзірге іске асырылмаған"
        );
    }
}