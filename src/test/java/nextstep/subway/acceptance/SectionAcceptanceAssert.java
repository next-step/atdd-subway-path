package nextstep.subway.acceptance;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_조회_요청;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;

public class SectionAcceptanceAssert {

    protected static void 지하철_노선에_구간을_등록_검증(Long lineId, List<Long> stationIds) {
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(lineId);

        Assertions.assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("stations.id", Long.class))
                        .containsExactly(stationIds.toArray(Long[]::new))
        );
    }

    protected static void 기존_구간_사이에_신규_구간을_추가_검증(Long lineId, List<Long> stationIds) {
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(lineId);

        Assertions.assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("stations.id", Long.class))
                        .containsExactly(stationIds.toArray(Long[]::new))
        );
    }

    protected static void 노선의_상행_종점으로_신규_구간을_추가_검증(Long lineId, List<Long> stationIds) {
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(lineId);

        Assertions.assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("stations.id", Long.class))
                        .containsExactly(stationIds.toArray(Long[]::new))
        );
    }
}
