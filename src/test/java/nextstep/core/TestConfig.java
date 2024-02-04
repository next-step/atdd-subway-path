package nextstep.core;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@TestConfiguration
@ComponentScan("nextstep.subway")
public class TestConfig {

    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public DatabaseCleaner databaseCleaner() {
        return new DatabaseCleaner(entityManager);
    }
}
