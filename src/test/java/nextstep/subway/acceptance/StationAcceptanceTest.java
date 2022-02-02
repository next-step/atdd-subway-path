package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.model.StationEntitiesHelper.강남역;
import static nextstep.subway.model.StationEntitiesHelper.역삼역;
import static nextstep.subway.model.StationEntitiesHelper.지하철역_목록_조회_요청;
import static nextstep.subway.model.StationEntitiesHelper.지하철역_삭제_요청;
import static nextstep.subway.model.StationEntitiesHelper.지하철역_생성_요청;
import static org.apache.http.HttpHeaders.LOCATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@DisplayName("지하철역 관리 기능")
class StationAcceptanceTest extends AcceptanceTest {
    /**
     * When 지하철역 생성을 요청 하면
     * Then 지하철역 생성이 성공한다.
     */
    @DisplayName("지하철역 생성")
    @Test
    void createStation() {
        ExtractableResponse<Response> response = 지하철역_생성_요청(강남역);
        assertThat(response.statusCode()).isEqualTo(CREATED.value());
        assertThat(response.header(LOCATION)).isNotBlank();
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
        지하철역_생성_요청(강남역);
        지하철역_생성_요청(역삼역);
        ExtractableResponse<Response> response = 지하철역_목록_조회_요청();
        assertThat(response.statusCode()).isEqualTo(OK.value());
        assertThat(response.jsonPath().getList("name")).contains(강남역, 역삼역);
    }

    /**
     * Given 지하철역 생성을 요청 하고
     * When 생성한 지하철역 삭제를 요청 하면
     * Then 생성한 지하철역 삭제가 성공한다.
     */
    @DisplayName("지하철역 삭제")
    @Test
    void deleteStation() {
        ExtractableResponse<Response> createResponse = 지하철역_생성_요청(강남역);
        ExtractableResponse<Response> response = 지하철역_삭제_요청(createResponse.header(LOCATION));
        assertThat(response.statusCode()).isEqualTo(NO_CONTENT.value());
    }

    /**
     * Given 지하철역 생성을 요청 하고
     * When 같은 이름으로 지하철역 생성을 요청 하면
     * Then 지하철역 생성이 실패한다.
     */
    @DisplayName("중복이름으로 지하철역 생성")
    @Test
    void createDuplicateStation()  {
        지하철역_생성_요청(강남역);
        ExtractableResponse<Response> failResponse = 지하철역_생성_요청(강남역);
        assertThat(failResponse.statusCode()).isEqualTo(BAD_REQUEST.value());
    }
}
