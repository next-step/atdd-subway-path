package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.model.LineEntitiesHelper.*;
import static nextstep.subway.acceptance.model.StationEntitiesHelper.*;
import static org.apache.http.HttpHeaders.LOCATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.*;

@DisplayName("지하철 노선 관리 기능")
class LineAcceptanceTest extends AcceptanceTest {

    private Long 역삼역_ID;
    private Long 강남역_ID;
    private Long 정자역_ID;
    private Long 판교역_ID;
    private Map<String, Object> 신분당선 = new HashMap<>();
    private Map<String, Object> 이호선 = new HashMap<>();

    @BeforeEach
    void setFixtures() {
        역삼역_ID = 지하철역_생성_요청후_아이디_조회(역삼역);
        강남역_ID = 지하철역_생성_요청후_아이디_조회(강남역);
        정자역_ID = 지하철역_생성_요청후_아이디_조회(정자역);
        판교역_ID = 지하철역_생성_요청후_아이디_조회(판교역);
        신분당선 = newLine("신분당선", "bg-red-600", 정자역_ID, 판교역_ID, 5);
        이호선 = newLine("이호선", "bg-green-600", 강남역_ID, 역삼역_ID, 2);
    }

    /**
     * When 지하철 노선 생성을 요청 하면
     * Then 지하철 노선 생성이 성공한다.
     */
    @DisplayName("지하철 노선 생성")
    @Test
    void createLine() {
        ExtractableResponse<Response> response = 노선_생성_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(CREATED.value());
        assertThat(response.header(LOCATION)).isNotBlank();
    }

    /**
     * Given 지하철 노선 생성을 요청 하고
     * Given 새로운 지하철 노선 생성을 요청 하고
     * When 지하철 노선 목록 조회를 요청 하면
     * Then 두 노선이 포함된 지하철 노선 목록을 응답받는다
     */
    @DisplayName("지하철 노선 목록 조회")
    @Test
    void getLines() {
        노선_생성_요청(이호선);
        노선_생성_요청(신분당선);
        ExtractableResponse<Response> response = 노선_목록_조회_요청();
        assertThat(response.statusCode()).isEqualTo(OK.value());
        assertThat(response.jsonPath().getList("name")).contains(이호선.get("name"), 신분당선.get("name"));
    }

    /**
     * Given 지하철 노선 생성을 요청 하고
     * When 생성한 지하철 노선 조회를 요청 하면
     * Then 생성한 지하철 노선을 응답받는다
     */
    @DisplayName("지하철 노선 조회")
    @Test
    void getLine() {
        ExtractableResponse<Response> createResponse = 노선_생성_요청(이호선);
        ExtractableResponse<Response> response = 노선_단건_조회_요청(createResponse.header(LOCATION));
        assertThat(response.statusCode()).isEqualTo(OK.value());
        assertThat(response.jsonPath().getString("name")).isEqualTo(이호선.get("name"));
    }

    /**
     * Given 지하철 노선 생성을 요청 하고
     * When 지하철 노선의 정보 수정을 요청 하면
     * Then 지하철 노선의 정보 수정은 성공한다.
     */
    @DisplayName("지하철 노선 수정")
    @Test
    void updateLine() {
        ExtractableResponse<Response> createResponse = 노선_생성_요청(이호선);
        ExtractableResponse<Response> updateResponse = 노선_수정_요청(신분당선, createResponse.header(LOCATION));
        ExtractableResponse<Response> findResponse = 노선_단건_조회_요청(createResponse.header(LOCATION));
        assertThat(updateResponse.statusCode()).isEqualTo(OK.value());
        assertThat(findResponse.statusCode()).isEqualTo(OK.value());
        assertThat(findResponse.jsonPath().getString("name")).isEqualTo(신분당선.get("name"));
        assertThat(findResponse.jsonPath().getString("color")).isEqualTo(신분당선.get("color"));
    }

    /**
     * Given 지하철 노선 생성을 요청 하고
     * When 생성한 지하철 노선 삭제를 요청 하면
     * Then 생성한 지하철 노선 삭제가 성공한다.
     */
    @DisplayName("지하철 노선 삭제")
    @Test
    void deleteLine() {
        ExtractableResponse<Response> createResponse = 노선_생성_요청(이호선);
        ExtractableResponse<Response> response = 노선_삭제_요청(createResponse.header(LOCATION));
        assertThat(response.statusCode()).isEqualTo(NO_CONTENT.value());
    }

    /**
     * Given 지하철 노선 생성을 요청 하고
     * When 같은 이름으로 지하철 노선 생성을 요청 하면
     * Then 지하철 노선 생성이 실패한다.
     */
    @DisplayName("중복이름으로 지하철 노선 생성시 실패")
    @Test
    void createDuplicateLine() {
        노선_생성_요청(이호선);
        ExtractableResponse<Response> failResponse = 노선_생성_요청(이호선);
        assertThat(failResponse.statusCode()).isEqualTo(BAD_REQUEST.value());
    }
}
