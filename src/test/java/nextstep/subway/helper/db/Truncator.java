package nextstep.subway.helper.db;

import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class Truncator {
    @PersistenceContext
    private final EntityManager em;

    public Truncator(EntityManager em) {
        this.em = em;
    }

    @Transactional
    public void truncateAll() {
        for (String tableName : getAllTablesFromSchema()) {
            truncateTable(tableName);
        }
    }

    private List<String> getAllTablesFromSchema() {
        Query query = em.createNativeQuery(
                "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = SCHEMA()"
        );
        List<String> tableNames = query.getResultList();
        return tableNames.stream().map(String::toLowerCase).collect(Collectors.toList());
    }

    private void truncateTable(String name) {
        em.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

        Query truncateQuery = em.createNativeQuery("TRUNCATE TABLE " + name + " RESTART IDENTITY");
        truncateQuery.executeUpdate();

        em.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }
}