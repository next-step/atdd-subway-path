package nextstep.subway.line.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.core.AcceptanceTest;
import nextstep.core.RestAssuredHelper;
import nextstep.subway.common.LineApiHelper;
import nextstep.subway.common.StationApiHelper;
import nextstep.subway.line.service.dto.LineResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("노선 관련 기능")
@AcceptanceTest
public class LineAcceptanceTest {
    private Long 지하철역_Id;
    private Long 새로운지하철역_Id;
    private Long 또다른지하철역_Id;
    private final String 신분당선 = "신분당선";
    private final String 신분당선_color = "bg-red-600";
    private final int 신분당선_distance = 10;
    private final String 분당선 = "분당선";
    private final String 분당선_color = "bg-green-600";
    private final int 분당선_distance = 15;

    @BeforeEach
    void setUp() {
        지하철역_Id = RestAssuredHelper.getIdFrom(StationApiHelper.createStation("지하철역"));
        새로운지하철역_Id = RestAssuredHelper.getIdFrom(StationApiHelper.createStation("새로운지하철역"));
        또다른지하철역_Id = RestAssuredHelper.getIdFrom(StationApiHelper.createStation("또다른지하철역"));
    }

    /**
     * When 노선을 생성하면
     * Then 노선이 생성된다
     * Then 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("노선을 생성한다.")
    @Test
    void 노선_생성_테스트() {
        // when
        final ExtractableResponse<Response> response = 노선_생성_요청(신분당선, 신분당선_color, 지하철역_Id, 새로운지하철역_Id, 신분당선_distance);

        // then
        노선이_정상_생성된다(response);

        // then
        노선_목록_조회_시_생성한_노선을_찾을_수_있다();
    }

    /**
     * Given 2개의 노선을 생성하고
     * When 노선 목록을 조회하면
     * Then 2개의 노선을 응답 받는다
     */
    @DisplayName("노선 목록을 조회한다.")
    @Test
    void 노선_목록_조회_테스트() {
        // given
        final ExtractableResponse<Response> 신분당선_response = 노선_생성_요청(신분당선, 신분당선_color, 지하철역_Id, 새로운지하철역_Id, 신분당선_distance);
        final ExtractableResponse<Response> 분당선_response = 노선_생성_요청(분당선, 분당선_color, 지하철역_Id, 또다른지하철역_Id, 분당선_distance);

        // when
        final ExtractableResponse<Response> response = 노선_목록_조회_요청();

        // then
        조회된_목록에_생성한_노선_정보가_있다(response, 신분당선_response, 분당선_response);
    }

    /**
     * Given 노선을 생성하고
     * When 생성한 노선을 조회하면
     * Then 생성한 노선을 응답 받는다
     */
    @DisplayName("특정 노선을 조회한다.")
    @Test
    void 노선_조회_테스트() {
        // given
        final ExtractableResponse<Response> 신분당선_response = 노선_생성_요청(신분당선, 신분당선_color, 지하철역_Id, 새로운지하철역_Id, 신분당선_distance);
        final Long 신분당선_id = RestAssuredHelper.getIdFrom(신분당선_response);

        // when
        final ExtractableResponse<Response> response = 노선_조회_요청(신분당선_id);

        // then
        섕성된_노선이_조회_된다(response);
    }

    /**
     * Given 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("특정 노선을 수정한다.")
    @Test
    void 노선_수정_테스트() {
        // given
        final ExtractableResponse<Response> 신분당선_response = 노선_생성_요청(신분당선, 신분당선_color, 지하철역_Id, 새로운지하철역_Id, 신분당선_distance);
        final Long 신분당선_id = RestAssuredHelper.getIdFrom(신분당선_response);

        // when
        final String 다른분당선 = "다른분당선";
        final ExtractableResponse<Response> response = 노선_이름_변경_요청(신분당선_id, 다른분당선);

        // then
        노선_지하철_정보가_수정된다(response, 신분당선_id, 다른분당선);
    }

    /**
     * Given 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("특정 노선을 삭제한다.")
    @Test
    void 노선_삭제_테스트() {
        // given
        final ExtractableResponse<Response> 신분당선_response = 노선_생성_요청(신분당선, 신분당선_color, 지하철역_Id, 새로운지하철역_Id, 신분당선_distance);
        final ExtractableResponse<Response> 분당선_response = 노선_생성_요청(분당선, 분당선_color, 지하철역_Id, 또다른지하철역_Id, 분당선_distance);
        final Long 신분당선_id = RestAssuredHelper.getIdFrom(신분당선_response);

        // when
        final ExtractableResponse<Response> response = 노선_삭제_요청(신분당선_id);

        // then
        노선_목록에서_삭제된다(response, 분당선_response);
    }

    private void 노선_목록에서_삭제된다(final ExtractableResponse<Response> response, final ExtractableResponse<Response> 분당선_response) {
        assertSoftly(softly -> {
            softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
            final List<Long> lineIds = getIds(노선_목록_조회_요청());
            softly.assertThat(lineIds).containsExactly(RestAssuredHelper.getIdFrom(분당선_response));
        });
    }

    private static ExtractableResponse<Response> 노선_삭제_요청(final Long 신분당선_id) {
        return LineApiHelper.removeLine(신분당선_id);
    }

    private void 노선_지하철_정보가_수정된다(final ExtractableResponse<Response> response, final Long 신분당선_id, final String 다른분당선) {
        assertSoftly(softly -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            final LineResponse lineResponse = 노선_조회_요청(신분당선_id).as(LineResponse.class);
            assertLineResponse(lineResponse, 다른분당선, 신분당선_color, 지하철역_Id, 새로운지하철역_Id);
        });
    }

    private ExtractableResponse<Response> 노선_이름_변경_요청(final Long 신분당선_id, final String 다른분당선) {
        return LineApiHelper.modifyLine(신분당선_id, 다른분당선, 신분당선_color);
    }

    private void 섕성된_노선이_조회_된다(final ExtractableResponse<Response> response) {
        assertSoftly(softly -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertLineResponse(response.as(LineResponse.class), 신분당선, 신분당선_color, 지하철역_Id, 새로운지하철역_Id);
        });
    }

    private static ExtractableResponse<Response> 노선_조회_요청(final Long 신분당선_id) {
        return LineApiHelper.fetchLineById(신분당선_id);
    }

    private void 조회된_목록에_생성한_노선_정보가_있다(final ExtractableResponse<Response> response, final ExtractableResponse<Response> 신분당선_response, final ExtractableResponse<Response> 분당선_response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
                , () -> assertResponseData(response, RestAssuredHelper.getIdFrom(신분당선_response), 신분당선, 신분당선_color, 지하철역_Id, 새로운지하철역_Id)
                , () -> assertResponseData(response, RestAssuredHelper.getIdFrom(분당선_response), 분당선, 분당선_color, 지하철역_Id, 또다른지하철역_Id)
        );
    }

    private static ExtractableResponse<Response> 노선_목록_조회_요청() {
        return LineApiHelper.fetchLines();
    }

    private static ExtractableResponse<Response> 노선_생성_요청(final String 신분당선1, final String 신분당선_color1, final Long 지하철역_id, final Long 새로운지하철역_id, final int 신분당선_distance1) {
        return LineApiHelper.createLine(신분당선1, 신분당선_color1, 지하철역_id, 새로운지하철역_id, 신분당선_distance1);
    }

    private void 노선_목록_조회_시_생성한_노선을_찾을_수_있다() {
        final List<String> lineNames = getLineNames(노선_목록_조회_요청());
        assertThat(lineNames).containsAnyOf(신분당선);
    }

    private void 노선이_정상_생성된다(final ExtractableResponse<Response> response) {
        assertSoftly(softly -> {
            softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
            assertLineResponse(response.as(LineResponse.class), 신분당선, 신분당선_color, 지하철역_Id, 새로운지하철역_Id);
        });
    }

    private List<Long> getIds(final ExtractableResponse<Response> response) {
        return response.jsonPath().getList("id", Long.class);
    }

    private List<String> getLineNames(final ExtractableResponse<Response> response) {
        return response.jsonPath().getList("name", String.class);
    }

    private void assertResponseData(final ExtractableResponse<Response> response, final Long id, final String name, final String color, final Long upStationId, final Long downStationId) {
        final LineResponse lineResponse = RestAssuredHelper.findObjectFrom(response, id, LineResponse.class);
        assertLineResponse(lineResponse, name, color, upStationId, downStationId);
    }

    private void assertLineResponse(final LineResponse lineResponse, final String name, final String color, final Long upStationId, final Long downStationId) {
        assertSoftly(softly -> {
            softly.assertThat(lineResponse.getName()).isEqualTo(name);
            softly.assertThat(lineResponse.getColor()).isEqualTo(color);
            softly.assertThat(lineResponse.getStations())
                    .extracting("id").containsExactly(upStationId, downStationId);
        });
    }

}
