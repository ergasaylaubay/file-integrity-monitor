package kz.edu.fim.repository;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {

    private final String databasePath;

    public DatabaseManager(String databasePath) {
        this.databasePath = databasePath;
    }

    public Connection getConnection() throws SQLException {
        // SQLite қосылымы келесі кезеңде іске асырылады.
        throw new UnsupportedOperationException(
                "Базаға қосылу әзірге іске асырылмаған"
        );
    }

    public void initializeDatabase() throws SQLException {
        // Қажетті кестелер келесі кезеңде жасалады.
        throw new UnsupportedOperationException(
                "Базаны дайындау әзірге іске асырылмаған"
        );
    }
}