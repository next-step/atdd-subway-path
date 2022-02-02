package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.step_feature.StationStepFeature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

import static nextstep.subway.acceptance.step_feature.StationStepFeature.*;

@DisplayName("지하철역 관리 기능")
class StationAcceptanceTest extends AcceptanceTest {

    /**
     * When 지하철역 생성을 요청 하면
     * Then 지하철역 생성이 성공한다.
     */
    @DisplayName("지하철역 생성")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = 지하철역_생성_요청(강남역_이름);

        // then
        역_생성_응답상태_검증(response);
    }

    /**
     * Given 지하철역 생성
     * When 같은 이름으로 지하철역 생성
     * Then 400 status code를 응답한다.
     */
    @DisplayName("중복 이름의 지하철역을 생성하면 실패한다")
    @Test
    void createStation_duplicate_fail() {
        // given
        지하철역_생성_요청(강남역_이름);

        // when
        ExtractableResponse<Response> response = 지하철역_생성_요청(강남역_이름);

        // then
        역_생성_실패_응답상태_검증(response);
    }

    /**
     * Given 지하철역 생성을 요청 하고
     * When 생성한 지하철을 조회한다.
     * Then 생성한 지하철의 정보를 응답받는다
     */
    @DisplayName("지하철역 id로 조회")
    @Test
    void showStation() {
        // given
        ExtractableResponse<Response> createResponse = 지하철역_생성_요청(강남역_이름);
        String location = createResponse.header("Location");

        // when
        ExtractableResponse<Response> response = 지하쳘역_삭제_요청(location);

        // then
        역_조회_응답상태_검증(response);
        역_조회_검증(response, Arrays.asList(강남역_이름));
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
        // given
        지하철역_생성_요청(강남역_이름);
        지하철역_생성_요청(판교역_이름);

        // when
        ExtractableResponse<Response> response = StationStepFeature.모든_지하철역_조회_요청();

        // then
        역_조회_응답상태_검증(response);
        역_조회_검증(response, Arrays.asList(강남역_이름, 판교역_이름));
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
        ExtractableResponse<Response> createResponse = 지하철역_생성_요청(강남역_이름);
        String location = createResponse.header("Location");

        // when
        ExtractableResponse<Response> response = StationStepFeature.지하철역_삭제_요청(location);

        // then
        역_응답상태_검증(response.statusCode(), HttpStatus.NO_CONTENT);
    }

}
