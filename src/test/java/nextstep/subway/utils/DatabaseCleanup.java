package nextstep.subway.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.JoinTable;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.EntityType;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Profile("test")
@Service
public class DatabaseCleanup implements InitializingBean {
    @PersistenceContext
    private EntityManager entityManager;

    private List<String> entityTableNames;
    private List<String> joinTableNames;

    @Override
    public void afterPropertiesSet() {
        entityTableNames = getEntityNames();
        joinTableNames = getJoinTableNames();
    }

    private List<String> getEntityNames() {
        return entityManager.getMetamodel().getEntities().stream()
                .filter(entity -> entity.getJavaType().getAnnotation(Entity.class) != null)
                .map(EntityType::getName)
                .collect(Collectors.toUnmodifiableList());
    }

    private List<String> getJoinTableNames() {
        return entityManager.getMetamodel().getManagedTypes().stream()
                .flatMap(entity -> Arrays.stream(entity.getJavaType().getDeclaredFields())
                        .filter(field -> field.getAnnotation(JoinTable.class) != null)
                )
                .map(field -> field.getAnnotation(JoinTable.class))
                .map(JoinTable::name)
                .collect(Collectors.toUnmodifiableList());
    }

    @Transactional
    public void execute() {
        // before
        entityManager.flush();
        setReferentialIntegrity(false);

        // do
        truncateEntityTables();
        truncateJoinTables();

        // after
        setReferentialIntegrity(true);
    }

    private void setReferentialIntegrity(final boolean value) {
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY " + value).executeUpdate();
    }

    private void truncateEntityTables() {
        for (final var tableName : entityTableNames) {
            entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE " + tableName + " ALTER COLUMN ID RESTART WITH 1")
                    .executeUpdate();
        }
    }

    private void truncateJoinTables() {
        for (final var tableName : joinTableNames) {
            entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
        }
    }
}
