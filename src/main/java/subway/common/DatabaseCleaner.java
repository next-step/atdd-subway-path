package subway.common;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import javax.transaction.Transactional;
import org.springframework.stereotype.Component;

@Component
public class DatabaseCleaner {
    private final EntityManager entityManager;
    private final List<String> tableNames;

    public DatabaseCleaner(final EntityManager entityManager) {
        this.entityManager = entityManager;
        this.tableNames = entityManager.getMetamodel()
            .getEntities()
            .stream()
            .map(EntityType::getName)
            .map(String::toLowerCase)
            .collect(Collectors.toList());
    }

    @Transactional
    public void execute() {
        entityManager.flush();
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

        for (String tableName : tableNames) {
            entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
        }

        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }
}
