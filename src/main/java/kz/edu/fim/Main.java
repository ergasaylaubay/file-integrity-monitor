package kz.edu.fim;

import kz.edu.fim.config.MonitorConfig;
import kz.edu.fim.service.DirectoryScanner;
import kz.edu.fim.service.HashService;
import kz.edu.fim.repository.DatabaseManager;
import kz.edu.fim.repository.FileRepository;

public class Main {

    public static void main(String[] args) {

        MonitorConfig config = new MonitorConfig(
                "./watched",
                "./data/fim.db"
        );

        HashService hashService = new HashService();
        DirectoryScanner scanner = new DirectoryScanner(hashService);

        DatabaseManager databaseManager =
                new DatabaseManager(config.getDatabasePath());

        FileRepository repository =
                new FileRepository(databaseManager);

        System.out.println("File Integrity Monitor");
        System.out.println("2-апта: жоба құрылымы дайын.");
        System.out.println("Бақыланатын қалта: "
                + config.getDirectoryPath());
        System.out.println("База жолы: "
                + config.getDatabasePath());
        System.out.println("Сканер: "
                + scanner.getClass().getSimpleName());
        System.out.println("Репозиторий: "
                + repository.getClass().getSimpleName());
    }
}