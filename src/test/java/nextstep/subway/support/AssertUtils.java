package nextstep.subway.support;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.global.error.code.ErrorCode;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class AssertUtils {

    private static final String ERROR_MESSAGES_KEY = "errorMessages";

    /**
     * <pre>
     * 응답코드에 대한 검증
     * </pre>
     *
     * @param response
     * @param status
     */
    public static void assertThatStatusCode(ExtractableResponse<Response> response, HttpStatus status) {
        assertThat(response.statusCode()).isEqualTo(status.value());
    }

    /**
     * <pre>
     * 에러메시지에 대한 검증
     * </pre>
     *
     * @param response
     * @param errorCode
     */
    public static void assertThatErrorMessage(ExtractableResponse<Response> response, ErrorCode errorCode) {
        assertThat(response.jsonPath().getList(ERROR_MESSAGES_KEY, String.class))
                .containsAnyOf(errorCode.getMessage());
    }

}
