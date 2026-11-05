package kz.edu.fim.service;

import kz.edu.fim.model.FileEvent;
import kz.edu.fim.model.FileRecord;

import java.util.Optional;

public class IntegrityService {

    public Optional<FileEvent> compare(
            FileRecord baseline,
            FileRecord current) {

        // Файл күйлерін салыстыру кейін іске асырылады.
        throw new UnsupportedOperationException(
                "Тұтастықты тексеру әзірге іске асырылмаған"
        );
    }
}