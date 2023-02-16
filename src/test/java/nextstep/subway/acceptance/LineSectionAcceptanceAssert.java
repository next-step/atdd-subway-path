package nextstep.subway.acceptance;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_조회_요청;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;

public class LineSectionAcceptanceAssert {

    protected static void 지하철_노선_구간_등록_검증(final Long line, final Long upStation, final Long downStation) {
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(line);

        Assertions.assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("stations.id", Long.class))
                        .containsExactly(line, upStation, downStation)
        );
    }

    protected static void 지하철_노선에_지하철_구간_제거_요청_검증(final Long line, final Long upStation, final Long downStation) {
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(line);

        Assertions.assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("stations.id", Long.class))
                        .containsExactly(upStation, downStation)
        );
    }
}
