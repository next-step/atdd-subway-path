package nextstep.subway.line.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.line.acceptance.LineRequestSteps.지하철_노선_생성_요청;
import static nextstep.subway.line.acceptance.LineSectionRequestSteps.*;
import static nextstep.subway.line.acceptance.LineSectionVerificationSteps.*;
import static nextstep.subway.station.StationRequestSteps.지하철_역_등록_됨;

@DisplayName("지하철 노선에 역 등록 관련 기능")
public class LineSectionAcceptanceTest extends AcceptanceTest {

    private StationResponse 양재역;
    private StationResponse 청계산입구역;
    private StationResponse 판교역;
    private LineResponse 신분당선_양재_청계산입구_노선;

    @BeforeEach
    public void init() {
        super.setUp();

        // given
        양재역 = 지하철_역_등록_됨("양재역").as(StationResponse.class);
        청계산입구역 = 지하철_역_등록_됨("청계산입구역").as(StationResponse.class);
        판교역 = 지하철_역_등록_됨("판교역").as(StationResponse.class);

        신분당선_양재_청계산입구_노선 = 지하철_노선_생성_요청(노선_요청("신분당선", "bg-red-600", 양재역.getId(), 청계산입구역.getId(), 7))
                .as(LineResponse.class);
    }

    @Test
    @DisplayName("지하철 노선에 구간을 등록한다.")
    void addLineSection() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(신분당선_양재_청계산입구_노선.getId(), 청계산입구역.getId(), 판교역.getId(), 10);

        // then
        지하철_노선에_구간_등록_됨(response);
    }

    @Test
    @DisplayName("지하철 노선에 이미 포함된 역을 구간으로 등록한다.")
    void addLineSectionAlreadyIncluded() {
        // given
        지하철_노선에_구간_등록_요청(신분당선_양재_청계산입구_노선.getId(), 양재역.getId(), 청계산입구역.getId(), 7);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(신분당선_양재_청계산입구_노선.getId(), 양재역.getId(), 청계산입구역.getId(), 7);

        // then
        지하철_노선에_구간_등록_실패_됨(response);
    }

    @Test
    @DisplayName("지하철 노선에 등록된 구간을 제거한다.")
    void removeLineSection() {
        // given
        지하철_노선에_구간_등록_요청(신분당선_양재_청계산입구_노선.getId(), 청계산입구역.getId(), 판교역.getId(), 10).as(LineResponse.class);

        // when
        ExtractableResponse<Response> deleteResponse = 지하철_노선에_등록된_구간_제거_요청(신분당선_양재_청계산입구_노선.getId(), 판교역.getId());

        // then
        지하철_노선에_등록된_구간_제거_됨(deleteResponse);
    }

    @Test
    @DisplayName("지하철 노선에 등록된 하행 종점역이 아닌 역을 제거한다.")
    void removeLineSectionNotLastDownStation() {
        // given
        지하철_노선에_구간_등록_요청(신분당선_양재_청계산입구_노선.getId(), 청계산입구역.getId(), 판교역.getId(), 7).as(LineResponse.class);

        // when
        ExtractableResponse<Response> deleteResponse = 지하철_노선에_등록된_구간_제거_요청(신분당선_양재_청계산입구_노선.getId(), 청계산입구역.getId());

        // then
        지하철_노선에_등록된_구간_제거_실패_됨(deleteResponse);
    }

    @Test
    @DisplayName("지하철 노선에 구간이 하나일 때 지하철역을 제외한다.")
    void removeLineSectionOnlyOneSection() {
        // when
        ExtractableResponse<Response> deleteResponse = 지하철_노선에_등록된_구간_제거_요청(신분당선_양재_청계산입구_노선.getId(), 청계산입구역.getId());

        // then
        지하철_노선에_등록된_구간_제거_실패_됨(deleteResponse);
    }
}
