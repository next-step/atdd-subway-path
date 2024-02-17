package nextstep.subway.acceptance;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineUpdateRequest;
import nextstep.subway.station.dto.StationRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends BaseTest {

    private static final String STATION_API_PATH = "/stations";
    private static final String LINE_API_PATH = "/lines";

    private Long 강남역_ID;
    private Long 역삼역_ID;
    private Long 지하철역_ID;

    @Override
    @BeforeEach
    void setUp() {
        super.setUp();

        final StationRequest 강남역_생성_요청 = new StationRequest("강남역");
        final ExtractableResponse<Response> 강남역_생성_응답 = callPostApi(강남역_생성_요청, STATION_API_PATH);
        강남역_ID = getIdFromApiResponse(강남역_생성_응답);

        final StationRequest 역삼역_생성_요청 = new StationRequest("역삼역");
        final ExtractableResponse<Response> 역삼역_생성_응답 = callPostApi(역삼역_생성_요청, STATION_API_PATH);
        역삼역_ID = getIdFromApiResponse(역삼역_생성_응답);

        final StationRequest 지하역_생성_요청 = new StationRequest("지하철역");
        final ExtractableResponse<Response> 지하철역_생성_응답 = callPostApi(지하역_생성_요청, STATION_API_PATH);
        지하철역_ID = getIdFromApiResponse(지하철역_생성_응답);
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void 지하철_노선_생성() {
        // when
        final LineRequest 신분당선_생성_요청 = new LineRequest("신분당선", "bg-red-600", 강남역_ID, 역삼역_ID, 10);
        callPostApi(신분당선_생성_요청, LINE_API_PATH);

        // then
        final ExtractableResponse<Response> 노선_조회_응답 = callGetApi(LINE_API_PATH);
        final JsonPath jsonPath = 노선_조회_응답.jsonPath();

        final List<String> lineNames = jsonPath.getList("name", String.class);
        assertThat(lineNames).containsAnyOf("신분당선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void 지하철_노선_목록_조회() {
        // given
        final LineRequest 강남역_생성_요청 = new LineRequest("신분당선", "bg-red-600", 강남역_ID, 역삼역_ID, 10);
        callPostApi(강남역_생성_요청, LINE_API_PATH);

        final LineRequest 지하철노선_생성_요청 = new LineRequest("지하철노선", "bg-green-600", 강남역_ID, 지하철역_ID, 15);
        callPostApi(지하철노선_생성_요청, LINE_API_PATH);

        // when
        final ExtractableResponse<Response> 노선_조회_응답 = callGetApi(LINE_API_PATH);
        final JsonPath jsonPath = 노선_조회_응답.jsonPath();

        // then
        final List<String> lineNames = jsonPath.getList("name", String.class);
        assertThat(lineNames).containsExactly("신분당선", "지하철노선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void 지하철_노선_조회() {
        // given
        final LineRequest 신분당선_생성_요청 = new LineRequest("신분당선", "bg-red-600", 강남역_ID, 역삼역_ID, 10);
        final ExtractableResponse<Response> 신분당선_생성_응답 = callPostApi(신분당선_생성_요청, LINE_API_PATH);
        final Long lineId = getIdFromApiResponse(신분당선_생성_응답);

        // when
        final ExtractableResponse<Response> 노선_조회_응답 = callGetApi(LINE_API_PATH + "/{id}", lineId);
        final JsonPath jsonPath = 노선_조회_응답.jsonPath();

        // then
        final String lineName = jsonPath.get("name");
        assertThat(lineName).isEqualTo("신분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void 지하철_노선_수정() {
        // given
        final LineRequest 신분당선_생성_요청 = new LineRequest("신분당선", "bg-red-600", 강남역_ID, 역삼역_ID, 10);
        final ExtractableResponse<Response> 신분당선_생성_응답 = callPostApi(신분당선_생성_요청, LINE_API_PATH);
        final Long lineId = getIdFromApiResponse(신분당선_생성_응답);

        final LineUpdateRequest 신분당선_수정_요청 = new LineUpdateRequest("2호선", "bg-yellow-600");

        // when
        callPutApi(신분당선_수정_요청, LINE_API_PATH + "/{id}", lineId);

        // then
        final ExtractableResponse<Response> 노선_조회_응답 = callGetApi(LINE_API_PATH + "/{id}", lineId);
        final JsonPath afterUpdatedLine = 노선_조회_응답.jsonPath();

        final String updatedName = afterUpdatedLine.get("name");
        assertThat(updatedName).isEqualTo("2호선");

        final String updatedColor = afterUpdatedLine.get("color");
        assertThat(updatedColor).isEqualTo("bg-yellow-600");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void 지하철_노선_삭제() {
        // given
        final LineRequest 신분당선_생성_요청 = new LineRequest("신분당선", "bg-red-600", 강남역_ID, 역삼역_ID, 10);
        final ExtractableResponse<Response> 신분당선_생성_응답 = callPostApi(신분당선_생성_요청, LINE_API_PATH);
        final Long lineId = getIdFromApiResponse(신분당선_생성_응답);

        // when
        callDeleteApi(LINE_API_PATH + "/{id}", lineId);

        // then
        final ExtractableResponse<Response> 노선_조회_응답 = callGetApi(LINE_API_PATH);
        final JsonPath jsonPath = 노선_조회_응답.jsonPath();
        final List<String> lineNames = jsonPath.getList("name", String.class);

        assertThat(lineNames).doesNotContain("신분당선");
    }

}
