package nextstep.config.annotations;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Retention(RetentionPolicy.RUNTIME)
@Sql(value = "/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public @interface AcceptanceTest {
}