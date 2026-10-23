package kz.edu.fim;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MainTest {
    @Test
    void applicationStartsWithoutException() {
        assertDoesNotThrow(() -> Main.main(new String[0]));
    }
}

