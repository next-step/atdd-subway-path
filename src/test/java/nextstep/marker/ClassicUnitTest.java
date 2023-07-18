package nextstep.marker;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@SpringBootTest
@Transactional
@Retention(RetentionPolicy.RUNTIME)
public @interface ClassicUnitTest {

}
