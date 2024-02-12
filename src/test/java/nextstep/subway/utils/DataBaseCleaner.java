package nextstep.subway.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

@Profile("test")
@Component
public class DataBaseCleaner implements InitializingBean {

    @PersistenceContext
    private EntityManager entityManager;

    private List<String> tableNames;

    @Override
    public void afterPropertiesSet() throws Exception {
        tableNames = entityManager.getMetamodel().getEntities().stream()
                .filter(entityType -> entityType.getJavaType().getAnnotation(Entity.class) != null)
                .map(entityType -> this.convertCamelToUnderScore(entityType.getName()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void execute() {
        entityManager.flush();
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
        for (String tableName : tableNames) {
            entityManager.createNativeQuery("TRUNCATE TABLE " + tableName + " RESTART IDENTITY").executeUpdate();
        }
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }

    private String convertCamelToUnderScore(String entityName) {
        String result = entityName.replaceAll("([A-Z][a-z]+)", "_$1");
        result = StringUtils.removeStart(result, "_");

        return result.toUpperCase();
    }

}
