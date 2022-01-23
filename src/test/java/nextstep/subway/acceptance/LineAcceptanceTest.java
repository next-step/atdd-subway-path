package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.applicaion.exception.DuplicationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.utils.HttpRequestTestUtil.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관리 기능")
class LineAcceptanceTest extends AcceptanceTest {
    /**
     * When 지하철 노선 생성을 요청 하면
     * Then 지하철 노선 생성이 성공한다.
     */

    private String defaultUrl = "/lines";

    @DisplayName("지하철 노선 생성")
    @Test
    void createLine() {
        //when
        String 기존노선 = "기존 노선";
        String 기존색상 = "기존 색상";
        Map<String, String> param = createParam(기존노선, 기존색상);
        ExtractableResponse<Response> response = postRequest(defaultUrl, param);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }


    /**
     * Given 지하철 노선을 생성한다.
     * Given 새로운 지하철 노선을 생성한다.
     * When 지하철 노선 조회를 요청한다.
     * Then 지하철 노선을 반환한다.
     */
    @DisplayName("지하철 노선 목록 조회")
    @Test
    void getLines() {
        //given
        String 기존노선 = "기존 노선";
        String 기존색상 = "기존 색상";
        Map<String, String> param = createParam(기존노선, 기존색상);
        postRequest(defaultUrl, param);

        String 새로운노선 = "새로운 노선";
        String 새로운색상 = "새로운 색상";
        Map<String, String> param2 = createParam(새로운노선, 새로운색상);
        postRequest(defaultUrl, param2);

        //when
        ExtractableResponse<Response> response = getRequest(defaultUrl);

        //then
        assertThat(response.jsonPath().getList("name")).contains(새로운노선, 기존노선);
        assertThat(response.jsonPath().getList("color")).contains(새로운색상, 새로운색상);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성된 지하철 노선을 요청한다.
     * Then 생성된 지하철 노선을 반환한다.
     */
    @DisplayName("지하철 노선 조회")
    @Test
    void getLine() {
        //given
        String 기존노선 = "기존 노선";
        String 기존색상 = "기존 색상";
        Map<String, String> param = createParam(기존노선, 기존색상);
        ExtractableResponse<Response> createResponse = postRequest(defaultUrl, param);

        //when
        ExtractableResponse<Response> response = getRequest(createResponse.header(HttpHeaders.LOCATION));

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getString("name")).isEqualTo(기존노선);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 지하철 노선 수정을 요청한다,
     * Then 지하철 노선이 수정이 완료된다.
     */
    @DisplayName("지하철 노선 수정")
    @Test
    void updateLine() {
        //given
        String 기존노선 = "기존 노선";
        String 기존색상 = "기존 색상";
        Map<String, String> param = createParam(기존노선, 기존색상);
        ExtractableResponse<Response> createResponse = postRequest(defaultUrl, param);

        //when
        String 수정노선 = "수정 노선";
        String 수정색상 = "수정 색상";
        Map<String, String> updateParam = createParam(수정노선, 수정색상);
        ExtractableResponse<Response> updateResponse = putRequest(createResponse.header(HttpHeaders.LOCATION), updateParam);

        //then
        ExtractableResponse<Response> response = getRequest(createResponse.header(HttpHeaders.LOCATION));

        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getString("name")).isEqualTo(수정노선);


    }

    /**
     * Given 지하철 노션을 생성을 요청한다.
     * When 생성된 지하철 노션을 삭제를 요청한다.
     * Then 지하철 노션이 삭제된다.
     */
    @DisplayName("지하철 노선 삭제")
    @Test
    void deleteLine() {
        //given
        String 기존노선 = "기존 노선";
        String 기존색상 = "기존 색상";
        Map<String, String> param = createParam(기존노선, 기존색상);
        ExtractableResponse<Response> createResponse = postRequest(defaultUrl, param);

        //when
        ExtractableResponse<Response> response = deleteRequest(createResponse.header(HttpHeaders.LOCATION));

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    /**
     * Given 지하철 노선을 생성한다.
     * When 중복된 이름의 지하철 노선 생성을 요청한다.
     * Then 지하철 노선 생성이 실패한다.
     */

    @DisplayName("중복된 노선 생성은 실패한다")
    @Test
    void duplicationLine() {
        //given
        String 기존노선 = "기존 노선";
        String 기존색상 = "기존 색상";
        Map<String, String> param = createParam(기존노선, 기존색상);
        postRequest(defaultUrl, param);

        //when
        ExtractableResponse<Response> response = postRequest(defaultUrl, param);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(response.jsonPath().getString("message")).isEqualTo(DuplicationException.MESSAGE);
    }

    private Map<String, String> createParam(String 기존노선, String 기존색상) {
        Map<String, String> param = new HashMap<>();
        param.put("name", 기존노선);
        param.put("color", 기존색상);
        return param;
    }
}
