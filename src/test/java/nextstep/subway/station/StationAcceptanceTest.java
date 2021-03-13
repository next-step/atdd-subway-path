package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static nextstep.subway.station.StationRequestSteps.*;
import static nextstep.subway.station.StationVerificationSteps.*;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("지하철역을 생성한다.")
    void createStation() {
        // given & when
        ExtractableResponse<Response> response = 지하철_역_생성_요청("강남역");

        // then
        지하철_역_생성_됨(response);
    }

    @Test
    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    void createStationWithDuplicateName() {
        // given
        지하철_역_생성_요청("강남역");

        // when
        ExtractableResponse<Response> response = 지하철_역_생성_요청("강남역");

        // then
        지하철_역_생성_실패_됨(response);
    }

    @DisplayName("지하철역 목록을 조회한다.")
    @Test
    void getStations() {
        /// given
        ExtractableResponse<Response> createResponse1 = 지하철_역_생성_요청("강남역");
        ExtractableResponse<Response> createResponse2 = 지하철_역_생성_요청("역삼역");

        // when
        ExtractableResponse<Response> response = 지하철_역_목록_조회_요청();

        // then
        지하철_역_목록_조회_됨(response);
        지하철_역_목록_조회_결과에_생성한_역_포함_확인(response, Arrays.asList(createResponse1, createResponse2));
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> createResponse = 지하철_역_생성_요청("강남역");

        // when
        ExtractableResponse<Response> response = 지하철_역_제거_요청(생성된_지하철_역_URI_경로_확인(createResponse));

        // then
        지하철_역_제거_됨(response);
    }
}
