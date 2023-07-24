package subway.common.annotation;

import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * api 테스트를 위한 클래스임을 명시하는 어노테이션
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Profile("test")
@Component
public @interface ApiTester {

  @AliasFor(annotation = Component.class)
  String value() default "";

}
