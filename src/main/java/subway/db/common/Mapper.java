package subway.db.common;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 명령을 치환하기 위한 매퍼를 나타내는 어노테이션<br>
 * return type이 클래스명과 다른 경우에는 사용할 수 없음을 약속합니다.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Mapper {

  @AliasFor(annotation = Component.class)
  String value() default "";

}
