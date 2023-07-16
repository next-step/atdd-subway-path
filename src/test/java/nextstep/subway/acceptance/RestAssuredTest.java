package nextstep.subway.acceptance;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;

@Disabled
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class RestAssuredTest {

    private final static String GOOGLE_INDEX_PAGE_URL = "https://google.com";

    @DisplayName("구글 페이지 접근 테스트")
    @Test
    void accessGoogle() {
        givenBase()
                .when()
                .get()
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.HTML);
    }

    private RequestSpecification givenBase() {
        return given()
                .log().all()
                .baseUri(GOOGLE_INDEX_PAGE_URL);
    }
}