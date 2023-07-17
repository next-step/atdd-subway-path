package nextstep.subway.support;

import org.springframework.stereotype.Service;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.shaded.com.google.common.base.CaseFormat;

import javax.annotation.PostConstruct;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@ActiveProfiles("test")
public class DatabaseCleanup {

    @PersistenceContext
    private EntityManager entityManager;

    private List<String> tableNames;

    @PostConstruct
    public void init() {
        tableNames = entityManager
                .getMetamodel()
                .getEntities()
                .stream()
                .filter(e -> e.getJavaType().getAnnotation(Entity.class) != null)
                .map(e -> CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, e.getName()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void execute() {
        entityManager
                .flush();

        entityManager
                .createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE")
                .executeUpdate();

        for (String tableName : tableNames) {
            entityManager
                    .createNativeQuery(String.format("TRUNCATE TABLE %s", tableName))
                    .executeUpdate();
        }

        entityManager
                .createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE")
                .executeUpdate();
    }

}
