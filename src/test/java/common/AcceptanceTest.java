package common;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestExecutionListeners.MergeMode;

// 참조 : https://mangkyu.tistory.com/264
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@Retention(RetentionPolicy.RUNTIME)
@TestExecutionListeners(value = AcceptanceTestExecutionListener.class, mergeMode = MergeMode.MERGE_WITH_DEFAULTS)
public @interface AcceptanceTest {

}
