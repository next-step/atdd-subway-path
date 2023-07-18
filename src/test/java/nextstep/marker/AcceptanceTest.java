package nextstep.marker;

import io.restassured.RestAssured;
import nextstep.subway.utils.DataTruncateExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(DataTruncateExtension.class)
@Import({AcceptanceTest.SetUpRestAssured.class})
public @interface AcceptanceTest {


    @TestComponent
    class SetUpRestAssured implements ApplicationListener<ServletWebServerInitializedEvent> {

        @Override
        public void onApplicationEvent(ServletWebServerInitializedEvent event) {
            RestAssured.port = event.getWebServer().getPort();
        }
    }
}
