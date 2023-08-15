package subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static subway.acceptance.assertions.LineAssertions.노선_목록_검증;
import static subway.acceptance.assertions.LineAssertions.노선_응답_성공_검증;
import static subway.acceptance.utils.SubwayClient.노선_삭제_요청;
import static subway.acceptance.utils.SubwayClient.노선_생성_요청;
import static subway.acceptance.utils.SubwayClient.노선_수정_요청;
import static subway.acceptance.utils.SubwayClient.노선_조회_요청;
import static subway.acceptance.utils.SubwayClient.지하철역_생성_요청;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import subway.dto.LineRequest;
import subway.dto.StationRequest;

@DisplayName("노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선 찾을 수 있다
     */
    @DisplayName("[성공] 노선을 생성한다.")
    @Test
    void 노선을_생성() {
        // Given
        Long 강남역 = 지하철역_생성_요청(new StationRequest("강남역")).jsonPath().getLong("id");
        Long 광교역 = 지하철역_생성_요청(new StationRequest("광교역")).jsonPath().getLong("id");

        // When
        Long 신분당선 = 노선_생성_요청(new LineRequest("신분당선", "bg-red-600", 강남역, 광교역, 30L)).jsonPath().getLong("id");

        // Then
        ExtractableResponse<Response> 노선_조회_응답 = 노선_조회_요청(신분당선);
        노선_응답_성공_검증(노선_조회_응답, HttpStatus.OK, 30L, List.of(강남역, 광교역));
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("[성공] 지하철 노선 목록을 조회한다.")
    @Test
    void 지하철_노선_목록을_조회() {
        // Given
        Long 강남역 = 지하철역_생성_요청(new StationRequest("강남역")).jsonPath().getLong("id");
        Long 광교역 = 지하철역_생성_요청(new StationRequest("광교역")).jsonPath().getLong("id");

        Long 판교역 = 지하철역_생성_요청(new StationRequest("판교역")).jsonPath().getLong("id");
        Long 이매역 = 지하철역_생성_요청(new StationRequest("이매역")).jsonPath().getLong("id");

        노선_생성_요청(new LineRequest("신분당선", "bg-red-600", 강남역, 광교역, 30L)).jsonPath().getLong("id");
        노선_생성_요청(new LineRequest("경강선", "bg-blue-600", 판교역, 이매역, 10L)).jsonPath().getLong("id");

        // When
        ExtractableResponse<Response> 노선_조회_응답 = 노선_조회_요청();

        // Then
        노선_목록_검증(노선_조회_응답, HttpStatus.OK, List.of("신분당선", "경강선"));
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("[성공] 지하철 노선을 조회한다.")
    @Test
    void 지하철_노선을_조회() {
        // Given
        Long 강남역 = 지하철역_생성_요청(new StationRequest("강남역")).jsonPath().getLong("id");
        Long 광교역 = 지하철역_생성_요청(new StationRequest("광교역")).jsonPath().getLong("id");

        // When
        Long 신분당선 = 노선_생성_요청(new LineRequest("신분당선", "bg-red-600", 강남역, 광교역, 30L)).jsonPath().getLong("id");

        // Then
        ExtractableResponse<Response> 노선_조회_응답 = 노선_조회_요청(신분당선);
        노선_응답_성공_검증(노선_조회_응답, HttpStatus.OK, 30L, List.of(강남역, 광교역));
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("[성공] 지하철 노선을 수정한다.")
    @Test
    void 지하철_노선을_수정() {
        // Given
        Long 강남역 = 지하철역_생성_요청(new StationRequest("강남역")).jsonPath().getLong("id");
        Long 광교역 = 지하철역_생성_요청(new StationRequest("광교역")).jsonPath().getLong("id");

        Long 신분당선 = 노선_생성_요청(new LineRequest("신분당선", "bg-red-600", 강남역, 광교역, 30L)).jsonPath().getLong("id");

        // When
        ExtractableResponse<Response> 노선_수정_응답 = 노선_수정_요청(신분당선, new LineRequest("수인분당선", "bg-yellow-600", null, null, null));

        // then
        assertThat(노선_수정_응답.statusCode()).isEqualTo(HttpStatus.OK.value());

        JsonPath responseJsonPath = 노선_수정_응답.jsonPath();
        assertThat((String) responseJsonPath.get("name")).isEqualTo("수인분당선");
        assertThat((String) responseJsonPath.get("color")).isEqualTo("bg-yellow-600");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("[성공] 지하철 노선을 삭제한다.")
    @Test
    void 지하철_노선을_삭제() {
        // Given
        Long 강남역 = 지하철역_생성_요청(new StationRequest("강남역")).jsonPath().getLong("id");
        Long 광교역 = 지하철역_생성_요청(new StationRequest("광교역")).jsonPath().getLong("id");

        Long 신분당선 = 노선_생성_요청(new LineRequest("신분당선", "bg-red-600", 강남역, 광교역, 30L)).jsonPath().getLong("id");


        // When
        ExtractableResponse<Response> 노선_삭제_응답 = 노선_삭제_요청(신분당선);

        // Then
        assertThat(노선_삭제_응답.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

}
