package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.applicaion.exception.DuplicationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nextstep.subway.utils.HttpRequestTestUtil.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관리 기능")
class StationAcceptanceTest extends AcceptanceTest {

    private String defaultUrl = "/stations";

    /**
     * When 지하철역 생성을 요청 하면
     * Then 지하철역 생성이 성공한다.
     */
    @DisplayName("지하철역 생성")
    @Test
    void createParams() {
        // given
        Map<String, String> params = createParams("강남역");

        // when
        ExtractableResponse<Response> response = postRequest(defaultUrl, params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    /**
     * Given 지하철역 생성을 요청 하고
     * Given 새로운 지하철역 생성을 요청 하고
     * When 지하철역 목록 조회를 요청 하면
     * Then 두 지하철역이 포함된 지하철역 목록을 응답받는다
     */
    @DisplayName("지하철역 목록 조회")
    @Test
    void getStations() {
        /// given
        String 강남역 = "강남역";
        Map<String, String> params1 = createParams(강남역);
        postRequest(defaultUrl, params1);

        String 역삼역 = "역삼역";
        Map<String, String> params2 = createParams(역삼역);
        postRequest(defaultUrl, params2);

        // when
        ExtractableResponse<Response> response = getRequest(defaultUrl);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<String> stationNames = response.jsonPath().getList("name");
        assertThat(stationNames).contains(강남역, 역삼역);
    }

    /**
     * Given 지하철역 생성을 요청 하고
     * When 생성한 지하철역 삭제를 요청 하면
     * Then 생성한 지하철역 삭제가 성공한다.
     */
    @DisplayName("지하철역 삭제")
    @Test
    void deleteStation() {
        // given
        Map<String, String> params = createParams("강남역");
        ExtractableResponse<Response> createResponse = postRequest(defaultUrl, params);

        // when
        ExtractableResponse<Response> response = deleteRequest(createResponse.header(HttpHeaders.LOCATION));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    /**
     * Given 지하철 역을 생성한다.
     * When 동일한 이름의 지하철 역 생성을 요청한다.
     * Then 지하철 역 생성이 실패한다.
     */
    @DisplayName("중복된 지하철 역은 생성이 실패한다")
    @Test
    void duplicateStation() {
        //given
        String 지하철역 = "지하철역";
        Map<String, String> params = createParams(지하철역);
        postRequest(defaultUrl, params);

        //when
        ExtractableResponse<Response> response = postRequest(defaultUrl, params);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(response.jsonPath().getString("message")).isEqualTo(DuplicationException.MESSAGE);
    }

    private Map<String, String> createParams(String 지하철역) {
        Map<String, String> params = new HashMap<>();
        params.put("name", 지하철역);
        return params;
    }
}
