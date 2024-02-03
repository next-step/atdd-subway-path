package nextstep.util;

import java.lang.reflect.Field;

public class ReflectionUtils {

    private ReflectionUtils() {
    }

    public static void injectIdField(final Object object, final Object fieldValue) {
        final Class<?> clazz = object.getClass();
        try {
            final Field declaredField = clazz.getDeclaredField("id");
            declaredField.setAccessible(true);
            declaredField.set(object, fieldValue);
        } catch (final Exception e) {
            throw new RuntimeException(clazz.getName() + "의 id 필드 주입에 실패했습니다.", e);
        }
    }

}
