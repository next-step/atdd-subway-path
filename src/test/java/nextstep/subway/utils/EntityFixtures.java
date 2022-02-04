package nextstep.subway.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class EntityFixtures {

    // 테스트에서 fixture 엔티티를 생성 할 때 ID를 설정해주어야 하지만 도메인 엔티티 클래스 생성자에 id 파라미터를 추가하기 싫을 때 사용한다.
    public static <T> T createEntityFixtureWithId(Long id, Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getConstructor(null);
            T entity = constructor.newInstance();

            Field idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, id);

            return entity;
        } catch (Exception e) {
            return null;
        }
    }
}
