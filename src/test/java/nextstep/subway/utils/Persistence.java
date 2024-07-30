package nextstep.subway.utils;

import nextstep.subway.domain.BaseEntity;

import java.lang.reflect.Field;

public class Persistence {
    Long lastId = 0L;

    public <T extends BaseEntity> T persist(Long id, T entity) {
        try {
            Field field = BaseEntity.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(entity, id);
            this.lastId = id;
            return entity;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("테스트 중 id 생성을 실패했습니다", e);
        }
    }

    public <T extends BaseEntity> T persist(T entity) {
        try {
            Field field = BaseEntity.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(entity, this.lastId++);
            return entity;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("테스트 중 id 생성을 실패했습니다", e);
        }
    }
}
