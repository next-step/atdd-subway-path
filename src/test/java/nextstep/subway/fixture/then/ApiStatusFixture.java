package nextstep.subway.fixture.then;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.AbstractIntegerAssert;
import org.springframework.http.HttpStatus;

public abstract class ApiStatusFixture {

    public static AbstractIntegerAssert<?> API_생성_응답코드_검사(ExtractableResponse<Response> response) {
        return assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static AbstractIntegerAssert<?> API_요청성공_응답코드_검사(ExtractableResponse<Response> response) {
        return assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static AbstractIntegerAssert<?> API_삭제_응답코드_검사(ExtractableResponse<Response> response) {
        return assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    public static AbstractIntegerAssert<?> API_잘못된요청_응답코드_검사(ExtractableResponse<Response> response) {
        return assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
