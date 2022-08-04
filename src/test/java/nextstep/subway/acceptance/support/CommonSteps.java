package nextstep.subway.acceptance.support;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonSteps {
    public static void 에러발생_확인(final int code, final ExtractableResponse<Response> response) {
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(response.jsonPath().getLong("code")).isEqualTo(code)
                 );
    }
}
