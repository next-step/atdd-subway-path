package nextstep.subway.acceptance.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.util.CommonAcceptanceTest;
import nextstep.subway.common.Constant;
import nextstep.subway.line.presentation.request.CreateLineRequest;
import nextstep.subway.line.presentation.request.UpdateLineRequest;
import nextstep.subway.line.presentation.response.CreateLineResponse;
import nextstep.subway.line.presentation.response.ShowAllLinesResponse;
import nextstep.subway.line.presentation.response.ShowLineResponse;
import nextstep.subway.line.presentation.response.UpdateLineResponse;
import nextstep.subway.station.presentation.request.CreateStationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.subway.acceptance.line.LineApiExtractableResponse.*;
import static nextstep.subway.acceptance.station.StationApiExtractableResponse.지하철_역_생성;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends CommonAcceptanceTest {

    Long 강남역_ID;
    Long 신논현역_ID;
    Long 압구정로데오역_ID;
    Long 강남구청역_ID;
    CreateLineRequest 신분당선_생성_요청;
    CreateLineRequest 수인분당선_생성_요청;
    UpdateLineRequest 신분당선_수정_요청;

    @BeforeEach
    protected void setUp() {
        강남역_ID = 지하철_역_생성(CreateStationRequest.from(Constant.강남역)).jsonPath().getLong("stationId");
        신논현역_ID = 지하철_역_생성(CreateStationRequest.from(Constant.신논현역)).jsonPath().getLong("stationId");
        압구정로데오역_ID = 지하철_역_생성(CreateStationRequest.from(Constant.압구정로데오역)).jsonPath().getLong("stationId");
        강남구청역_ID = 지하철_역_생성(CreateStationRequest.from(Constant.강남구청역)).jsonPath().getLong("stationId");
        신분당선_생성_요청 = CreateLineRequest.of(Constant.신분당선, Constant.빨간색, 강남역_ID, 신논현역_ID, Constant.기본_역_간격);
        수인분당선_생성_요청 = CreateLineRequest.of(Constant.수인분당선, Constant.노란색, 압구정로데오역_ID, 강남구청역_ID, Constant.기본_역_간격);
        신분당선_수정_요청 = UpdateLineRequest.of(Constant.파란색);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void 지하철_노선을_생성() {
        // given
        CreateLineResponse 신분당선_생성_응답 = 지하철_노선_생성(신분당선_생성_요청).as(CreateLineResponse.class);

        // when
        ShowAllLinesResponse 지하철_노선_목록_조회_응답 = 지하철_노선_목록_조회().as(ShowAllLinesResponse.class);

        // then
        지하철_노선_생성_검증(신분당선_생성_응답, 지하철_노선_목록_조회_응답);
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 생성한 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void 지하철_노선_목록을_조회() {
        // given
        CreateLineResponse 신분당선_생성_응답 = 지하철_노선_생성(신분당선_생성_요청).as(CreateLineResponse.class);
        CreateLineResponse 수인분당선_생성_응답 = 지하철_노선_생성(수인분당선_생성_요청).as(CreateLineResponse.class);

        // when
        ShowAllLinesResponse 지하철_노선_목록_조회_응답 = 지하철_노선_목록_조회().as(ShowAllLinesResponse.class);

        // then
        지하철_노선_생성_검증(신분당선_생성_응답, 지하철_노선_목록_조회_응답);
        지하철_노선_생성_검증(수인분당선_생성_응답, 지하철_노선_목록_조회_응답);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void 지하철_노선을_조회() {
        // given
        CreateLineResponse 신분당선_생성_응답 = 지하철_노선_생성(신분당선_생성_요청).as(CreateLineResponse.class);
        Long 신분당선_ID = 신분당선_생성_응답.getLineId();

        // when
        ShowLineResponse 지하철_노선_조회_응답 = 지하철_노선_조회(신분당선_ID).as(ShowLineResponse.class);

        // then
        지하철_노선_조회_검증(신분당선_생성_응답, 지하철_노선_조회_응답);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다.
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void 지하철_노선을_수정() {
        // given
        CreateLineResponse 신분당선_생성_응답 = 지하철_노선_생성(신분당선_생성_요청).as(CreateLineResponse.class);
        Long 신분당선_ID = 신분당선_생성_응답.getLineId();

        // when
        UpdateLineResponse 신분당선_수정_응답 = 지하철_노선_수정(신분당선_ID, 신분당선_수정_요청).as(UpdateLineResponse.class);

        // then
        ShowLineResponse 지하철_노선_조회_응답 = 지하철_노선_조회(신분당선_ID).as(ShowLineResponse.class);
        지하철_노선_수정_검증(신분당선_수정_응답, 지하철_노선_조회_응답);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다.
     */
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void 지하철_노선을_삭제() {
        // given
        CreateLineResponse 신분당선_생성_응답 = 지하철_노선_생성(신분당선_생성_요청).as(CreateLineResponse.class);
        Long 신분당선_ID = 신분당선_생성_응답.getLineId();

        // when
        지하철_노선_삭제(신분당선_ID);

        // then
        ExtractableResponse<Response> 지하철_노선_조회_응답 = 지하철_노선_조회(신분당선_ID);
        지하철_노선_삭제_검증(지하철_노선_조회_응답);
    }

    void 지하철_노선_생성_검증(CreateLineResponse createLineResponse, ShowAllLinesResponse showAllLinesResponse) {
        assertTrue(showAllLinesResponse.getLines().stream()
                .anyMatch(line -> line.getLineId().equals(createLineResponse.getLineId())));
        assertTrue(showAllLinesResponse.getLines().stream()
                .anyMatch(line -> line.getName().equals(createLineResponse.getName())));
        assertTrue(showAllLinesResponse.getLines().stream()
                .anyMatch(line -> line.getColor().equals(createLineResponse.getColor())));
    }

    void 지하철_노선_조회_검증(CreateLineResponse createLineResponse, ShowLineResponse showLineResponse) {
        assertTrue(showLineResponse.getLineId().equals(createLineResponse.getLineId()));
        assertTrue(showLineResponse.getName().equals(createLineResponse.getName()));
        assertTrue(showLineResponse.getColor().equals(createLineResponse.getColor()));
    }

    void 지하철_노선_수정_검증(UpdateLineResponse updateLineResponse, ShowLineResponse showLineResponse) {
        assertTrue(showLineResponse.getLineId().equals(updateLineResponse.getLineId()));
        assertTrue(showLineResponse.getName().equals(updateLineResponse.getName()));
        assertTrue(showLineResponse.getColor().equals(updateLineResponse.getColor()));
    }

    void 지하철_노선_삭제_검증(ExtractableResponse<Response> extractableResponse) {
        assertThat(extractableResponse.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

}
