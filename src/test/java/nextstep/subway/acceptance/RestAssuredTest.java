package nextstep.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class RestAssuredTest {

    public static final String HTTPS_GOOGLE_COM = "https://google.com";

    @DisplayName("구글 페이지 접근 테스트")
    @Test
    void accessGoogle() {

        ExtractableResponse<Response> response = RestAssured.
                                                    given().baseUri(HTTPS_GOOGLE_COM).
                                                    when().get().
                                                    then().extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
