package nextstep.subway.acceptance;

import static nextstep.subway.acceptance.LineSteps.지하철_노선_조회_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_제거_요청;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    protected static void 노선의_하행_종점으로_신규_구간을_추가_검증(Long lineId, List<Long> stationIds) {
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(lineId);

        Assertions.assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("stations.id", Long.class))
                        .containsExactly(stationIds.toArray(Long[]::new))
        );
    }

    protected static void 지하철_노선에_구간을_제거_검증(Long lineId, List<Long> stationIds) {
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(lineId);

        Assertions.assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("stations.id", Long.class))
                        .containsExactly(stationIds.toArray(Long[]::new))
        );
    }

    protected static void 지하철_노선에_중간역을_제거_검증(final Long lineId, final List<Long> stationIds) {
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(lineId);

        Assertions.assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("stations.id", Long.class))
                        .containsExactly(stationIds.toArray(Long[]::new))
        );
    }

    protected static void 노선_조회시_상행역_부터_하행역_순으로_조회_검증(Long lineId, List<Long> stationIds) {
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(lineId);

        Assertions.assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("stations.id", Long.class))
                        .containsExactly(stationIds.toArray(Long[]::new))
        );
    }

    protected static void 노선에_등록되지_않은_역을_제거시_예외_발생(final Long line, final Long station) {
        // when
        var response = 지하철_노선에_지하철_구간_제거_요청(line, station);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    protected static void 구간_제거시_노선에_구간이_하나면_예외_발생(final Long line, final Long station) {
        // when
        var response = 지하철_노선에_지하철_구간_제거_요청(line, station);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    protected static void 신규구간_추가시_상행역과_하행역_모두_노선에_존재하지_않는_역이면_예외_발생(
            final Long line,
            final Long upStation,
            final Long downStation
    ) {
        // when
        var response = 지하철_노선에_지하철_구간_생성_요청(line, createSectionCreateParams(upStation, downStation, 4));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    protected static void 신규구간_추가시_상행역과_하행역_모두_노선에_등록되어_있다면_예외_발생(
            final Long line,
            final Long upStation,
            final Long downStation
    ) {
        // when
        var response = 지하철_노선에_지하철_구간_생성_요청(line, createSectionCreateParams(upStation, downStation, 4));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    protected static void 기존구간_사이에_신규구간_추가시_신규구간이_기존구간_사이_길이보다_크거나_같으면_예외_발생(
            final Long line,
            final Long upStation,
            final Long downStation,
            final int distance
    ) {
        // when
        var response = 지하철_노선에_지하철_구간_생성_요청(line, createSectionCreateParams(upStation, downStation, distance));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private static Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}
