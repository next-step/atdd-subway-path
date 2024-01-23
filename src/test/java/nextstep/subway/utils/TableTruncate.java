package nextstep.subway.utils;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TableTruncate{

    private static final String TRUNCATE_FORMAT = "TRUNCATE TABLE %s";
    private static final String ALTER_TABLE_FORMAT = "ALTER TABLE %s ALTER COLUMN ID RESTART WITH 1";
    private static final String CAMEL_CASE_REGEX = "([a-z])([A-Z]+)";
    private static final String SNAKE_CASE_REGEX = "$1_$2";

    @PersistenceContext
    private EntityManager em;

    private List<String> tableNames;

    @EventListener(ApplicationReadyEvent.class)
    public void getDatabaseTableNames(){
        tableNames = em.getMetamodel().getEntities().stream()
                .filter(e -> e.getJavaType().getAnnotation(Entity.class) != null)
                .map(e -> e.getName().replaceAll(CAMEL_CASE_REGEX, SNAKE_CASE_REGEX).toLowerCase())
                .collect(Collectors.toList());
    }

    @Transactional
    public void truncate() {
        em.flush();
        em.clear();
        for (String tableName : tableNames) {
            em.createNativeQuery(String.format(TRUNCATE_FORMAT, tableName)).executeUpdate();
            em.createNativeQuery(String.format(ALTER_TABLE_FORMAT, tableName)).executeUpdate();
        }
    }
}
