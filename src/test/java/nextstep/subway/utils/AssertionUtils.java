package nextstep.subway.utils;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;

public class AssertionUtils {

    // status code
    public static void 응답코드_200을_반환한다(final ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(OK.value());
    }


    // list

    public static <T> void 목록은_다음을_순서대로_포함한다(final List<T> list, final T... obj) {
        assertThat(list).containsExactly(obj);
    }
}
