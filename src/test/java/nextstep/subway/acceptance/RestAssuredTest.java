package nextstep.subway.acceptance;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class RestAssuredTest {

    @DisplayName("구글 페이지 접근 테스트")
    @Test
    void accessGoogle() {
        // TODO: 구글 페이지 요청 구현
        ExtractableResponse<Response> response =
            given()
                .log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("https://www.google.com")
                .then().log().status().statusCode(HttpStatus.OK.value()).extract();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}