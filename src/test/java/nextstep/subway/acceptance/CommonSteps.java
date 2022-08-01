package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.exception.ExceptionMessage;
import org.assertj.core.api.AbstractIntegerAssert;
import org.assertj.core.api.AbstractStringAssert;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class CommonSteps {

	public static AbstractIntegerAssert<?> 반환_상태_확인(ExtractableResponse<Response> response, HttpStatus status) {
		return assertThat(response.statusCode()).isEqualTo(status.value());
	}

	public static AbstractStringAssert<?> 잘못된_요청_메시지_확인(String message, ExceptionMessage exceptionMessage) {
		return assertThat(message).isEqualTo(exceptionMessage.msg());
	}
}
