package nextstep.subway.utils.data;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.EntityType;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : Rene Choi
 * @since : 2024/02/02
 */
@Component
public class DatabaseCleanupExecutor implements InitializingBean {

	@PersistenceContext
	private EntityManager entityManager;

	private List<String> tableNames;
	@Override
	public void afterPropertiesSet()  {
		tableNames = entityManager.getMetamodel().getEntities().stream()
			.filter(DatabaseCleanupExecutor::isEntityFound)
			.map(EntityType::getName)
			.collect(Collectors.toList());
	}

	@Transactional
	public void execute(){
		entityManager.flush();
		entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
		tableNames.forEach(tableName -> entityManager.createNativeQuery("TRUNCATE TABLE " +  tableName + " RESTART IDENTITY").executeUpdate());
		entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
	}

	private static boolean isEntityFound(EntityType<?> entity) {
		return entity.getJavaType().getAnnotation(Entity.class) != null;
	}
}
