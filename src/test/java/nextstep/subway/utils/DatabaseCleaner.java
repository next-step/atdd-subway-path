package nextstep.subway.utils;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DatabaseCleaner {

    @PersistenceContext
    private EntityManager entityManager;

    private List<String> tableNames;

    @EventListener(ApplicationReadyEvent.class)
    public void getTableNames() {
        tableNames = entityManager.getMetamodel()
            .getEntities()
            .stream()
            .filter(e -> e.getJavaType().getAnnotation(Entity.class) != null &&  e.getJavaType().getAnnotation(Table.class) != null)
            .map(e -> e.getJavaType().getAnnotation(Table.class).name())
            .collect(Collectors.toList());
    }

    @Transactional
    public void execute() {
        entityManager.flush();
        entityManager.clear();

        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

        tableNames.forEach(tableName -> {
            entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
            entityManager.createNativeQuery(String.format("ALTER TABLE %s ALTER COLUMN ID RESTART WITH 1", tableName)).executeUpdate();
        });

        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }
}
