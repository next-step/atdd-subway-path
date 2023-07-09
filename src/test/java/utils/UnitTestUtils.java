package utils;

import org.junit.platform.commons.util.AnnotationUtils;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.CollectionUtils;

import javax.persistence.Id;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.LongStream;

public class UnitTestUtils {
    private UnitTestUtils() {
    }

    public static <T> void createEntityTestId(T entity, Long id) {
        final Field idField = findIdFieldFromEntity(entity);
        final String idFieldName = idField.getName();

        ReflectionTestUtils.setField(entity, idFieldName, id);
    }

    public static <T> void createEntityTestIds(List<T> entities, Long startId) {
        if (CollectionUtils.isEmpty(entities)) {
            return;
        }

        final Field idField = findIdFieldFromEntity(entities.get(0));
        final String idFieldName = idField.getName();

        LongStream.range(startId, startId + entities.size())
                .forEach(id -> ReflectionTestUtils.setField(entities.get((int) id - startId.intValue()), idFieldName, id));
    }

    private static <T> Field findIdFieldFromEntity(T entity) {
        return Arrays.stream(entity.getClass().getDeclaredFields())
                .filter(field -> AnnotationUtils.findAnnotation(field, Id.class).isPresent())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("id annotation must be exists in entity"));
    }
}
