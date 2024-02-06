package nextstep.subway.api.practice;

import static io.restassured.RestAssured.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.api.CommonAcceptanceTest;

public class RestAssuredTest extends CommonAcceptanceTest {

	@DisplayName("구글 페이지 접근 테스트")
	@Test
	void accessGoogle() {
		ExtractableResponse<Response> response =
			given().
				when().
				get("https://www.google.com").
				then().
				extract();

		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}
}