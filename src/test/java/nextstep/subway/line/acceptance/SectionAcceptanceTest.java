package nextstep.subway.line.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import static nextstep.subway.line.acceptance.LineSteps.*;
import static nextstep.subway.line.acceptance.SectionSteps.*;
import static nextstep.subway.station.StationSteps.지하철역_생성_요청;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    private StationResponse 석촌역;
    private StationResponse 남한산성입구역;
    private StationResponse 산성역;
    private LineRequest lineRequest;

    @BeforeEach
    void setup() {
        super.setUp();

        int distance = 4;
        석촌역 = 지하철역_생성_요청("석촌역").as(StationResponse.class);
        남한산성입구역 = 지하철역_생성_요청("남한산성입구역").as(StationResponse.class);
        산성역 = 지하철역_생성_요청("산성역").as(StationResponse.class);
        lineRequest = new LineRequest("8호선", "pink", 석촌역.getId(), 남한산성입구역.getId(), distance);
    }

    @DisplayName("지하철 노선 구간 추가 한다.")
    @Test
    void createLineSection() {
        long lineId = createPinkLine();
        ExtractableResponse<Response> response = stationInit(lineId);

        지하철_노선_구간_응답_확인(response.statusCode(), HttpStatus.OK);
    }

    @DisplayName("지하철 노선 구간 제거 한다.")
    @Test
    void deleteLineSection() {
        long lineId = createPinkLine();
        stationInit(lineId);

        ExtractableResponse<Response> remove = 지하철_구간_제거_요청(lineId, 산성역.getId());

        지하철_노선_구간_응답_확인(remove.statusCode(), HttpStatus.OK);
    }

    @DisplayName("지하철 노선 구간이 1개인 경우 삭제 불가능")
    @Test
    public void stationOnlyOneSectionRemoveFail() {
        // given
        long lineId = createPinkLine();

        // when
        ExtractableResponse<Response> response = 지하철_구간_제거_요청(lineId, 남한산성입구역.getId());

        // then
        지하철_노선_구간_응답_확인(response.statusCode(), HttpStatus.BAD_REQUEST);
    }

    @DisplayName("지하철 노선 마지막역(하행 종점역)만 아닌경우 삭제 불가능")
    @Test
    public void notFinishedStationRemoveFail() {
        // given
        long lineId = createPinkLine();
        SectionRequest sectionRequest = SectionRequest.of(남한산성입구역.getId(), 산성역.getId(), 3);

        지하철_노선_구간_등록_요청(sectionRequest, lineId);

        // when
        ExtractableResponse<Response> response1 = 지하철_구간_제거_요청(lineId, 석촌역.getId());

        지하철_노선_구간_응답_확인(response1.statusCode(), HttpStatus.BAD_REQUEST);
    }

    @DisplayName("지하철 노선 구간 역 목록을 조회한다.")
    @Test
    public void getLineSection() {
        // given
        long lineId = createPinkLine();
        SectionRequest sectionRequest = SectionRequest.of(남한산성입구역.getId(), 산성역.getId(), 3);

        지하철_노선_구간_등록_요청(sectionRequest, lineId);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(lineId);

        // then
        지하철_노선_응답_확인(response.statusCode(), HttpStatus.OK);
    }

    ExtractableResponse<Response> stationInit(long lineId) {
        SectionRequest sectionRequest = SectionRequest.of(석촌역.getId(), 산성역.getId(), 3);
        ExtractableResponse<Response> response = 지하철_노선_구간_등록_요청(sectionRequest, lineId);
        return response;
    }

    long createPinkLine() {
        return 지하철_노선_생성요청(lineRequest).as(LineResponse.class).getId();
    }
}
