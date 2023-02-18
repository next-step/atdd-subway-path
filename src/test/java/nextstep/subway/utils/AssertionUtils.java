package nextstep.subway.utils;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.exception.ErrorResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

public class AssertionUtils {

    // status code
    public static void 응답코드_200을_반환한다(final ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(OK.value());
    }

    public static void 응답코드_400을_반환한다(final ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
    }


    // list
    public static <T> void 목록은_다음을_순서대로_포함한다(final List<T> list, final T... obj) {
        assertThat(list).containsExactly(obj);
    }

    public static <T> void 목록은_다음을_포함하지_않는다(final List<T> list, final T... obj) {
        assertThat(list).doesNotContain(obj);
    }

    // error
    public static void 에러메시지는_다음과_같다(ExtractableResponse<Response> response, String message) {
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(errorResponse.getMessage()).isEqualTo(message);
    }
}
