package nextstep.subway.utils;

import org.apache.commons.lang3.reflect.FieldUtils;

import javax.persistence.Entity;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EntityFixtures {

    public static <T, ID> T createEntityFixtureWithId(ID id, Class<T> clazz) {
        try {
            boolean isNotEntity = Arrays.stream(clazz.getDeclaredAnnotations())
                    .noneMatch(annotation -> Entity.class.isAssignableFrom(annotation.annotationType()));

            if (isNotEntity) {
                throw new IllegalArgumentException("must be an entity class");
            }

            Constructor<T> constructor = clazz.getConstructor();
            T entity = constructor.newInstance();

            Field idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, id);

            return entity;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public static <T> T createEntityFixtureWithFieldSet(Map<String, Object> fieldSet, Class<T> clazz) {
        try {
            boolean isNotEntity = Arrays.stream(clazz.getDeclaredAnnotations())
                    .noneMatch(annotation -> Entity.class.isAssignableFrom(annotation.annotationType()));

            if (isNotEntity) {
                throw new IllegalArgumentException("must be an entity class");
            }

            Constructor<T> constructor = clazz.getConstructor();
            T entity = constructor.newInstance();

            Map<String, Field> fieldMap = Arrays.stream(FieldUtils.getAllFields(clazz))
                    .collect(Collectors.toMap(Field::getName, Function.identity()));

            for (Map.Entry<String, Object> e : fieldSet.entrySet()) {
                String fieldName = e.getKey();
                Object fieldValue = e.getValue();

                Field field = fieldMap.get(fieldName);
                field.setAccessible(true);
                field.set(entity, fieldValue);
            }

            return entity;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }
}
