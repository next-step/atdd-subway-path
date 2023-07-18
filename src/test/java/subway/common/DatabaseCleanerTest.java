package subway.common;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class DatabaseCleanerTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void entities() {
        entityManager.getMetamodel()
            .getEntities()
            .stream()
            .map(EntityType::getName)
            .forEach(System.out::println);
    }

}
