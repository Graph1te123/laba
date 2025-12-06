package com.example;

import com.example.tournament.TournamentApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = TournamentApplication.class)
@ActiveProfiles("test")
class AppTest {

    @Test
    void contextLoads() {
        // Смоук‑тест: проверяем, что контекст Spring Boot поднимается
    }
}
