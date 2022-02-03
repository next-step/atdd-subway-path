package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.step.StationTestStep;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지하철역 관리 기능")
class StationAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철역 생성")
    @Test
    void createStation() {
        // given
        String 강남역_이름 = "강남역";

        // when
        ExtractableResponse<Response> response = StationTestStep.지하철역_생성하기(강남역_이름);

        // then
        StationTestStep.지하철역_생성_시_성공_검증하기(response);
    }

    @DisplayName("중복이름으로 지하철역 생성")
    @Test
    void createDuplicatedNameStation() {
        // given
        String 강남역_이름 = "강남역";
        StationTestStep.지하철역_생성하기(강남역_이름);

        // when
        ExtractableResponse<Response> response = StationTestStep.지하철역_생성하기(강남역_이름);

        // then
        StationTestStep.지하철역_생성_시_중복이름_실패_검증하기(response);
    }

    @DisplayName("지하철역 목록 조회")
    @Test
    void getStations() {
        /// given
        String 강남역 = "강남역";
        StationTestStep.지하철역_생성하기(강남역);
        String 역삼역 = "역삼역";
        StationTestStep.지하철역_생성하기(역삼역);

        // when
        ExtractableResponse<Response> response = StationTestStep.지하철역_목록_조회하기();

        // then
        StationTestStep.지하철역_목록_조회_성공_검증하기(response, 강남역, 역삼역);
    }

    @DisplayName("지하철역 삭제")
    @Test
    void deleteStation() {
        // given
        String 강남역_이름 = "강남역";
        Long stationId = StationTestStep.지하철역_생성_후_아이디_추출하기(강남역_이름);

        // when
        ExtractableResponse<Response> response = StationTestStep.지하철역_삭제하기(stationId);

        // then
        StationTestStep.지하철역_삭제_성공_검증하기(response);
    }
}
