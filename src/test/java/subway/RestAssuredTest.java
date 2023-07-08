package subway;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class RestAssuredTest {

	@DisplayName("구글 페이지 접근 테스트")
	@Test
	void accessGoogle() {
		final String googleUrl = "https://google.com";

		ExtractableResponse<Response> response = RestAssured.given()
			.log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().get(googleUrl)
			.then().extract();

		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}
}
