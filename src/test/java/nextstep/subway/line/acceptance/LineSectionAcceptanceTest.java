package nextstep.subway.line.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static nextstep.subway.line.acceptance.LineSteps.*;
import static nextstep.subway.station.StationSteps.지하철역_등록되어_있음;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선에 역 등록 관련 기능")
public class LineSectionAcceptanceTest extends AcceptanceTest {
    private LineResponse 신분당선;
    private StationResponse 강남역;
    private StationResponse 양재역;
    private StationResponse 정자역;
    private StationResponse 광교역;

    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_등록되어_있음("강남역").as(StationResponse.class);
        양재역 = 지하철역_등록되어_있음("양재역").as(StationResponse.class);
        정자역 = 지하철역_등록되어_있음("정자역").as(StationResponse.class);
        광교역 = 지하철역_등록되어_있음("광교역").as(StationResponse.class);

        LineRequest lineRequest = new LineRequest(
            "신분당선",
            "bg-red-600",
            강남역.getId(),
            양재역.getId(),
            10
        );
        신분당선 = 지하철_노선_등록되어_있음(lineRequest).as(LineResponse.class);
    }

    @DisplayName("지하철 노선에 구간을 등록한다: 새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void addLineSectionTail() {
        // when
        지하철_노선에_지하철역_등록_요청(신분당선, 양재역, 정자역, 6);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_노선에_지하철역_등록됨(response);
        지하철_노선에_지하철역_순서_정렬됨(response, Arrays.asList(강남역, 양재역, 정자역));
    }

    @DisplayName("지하철 노선에 구간을 등록한다: 새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addLineSectionHead() {
        // when
        지하철_노선에_지하철역_등록_요청(신분당선, 정자역, 강남역, 3);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_노선에_지하철역_등록됨(response);
        지하철_노선에_지하철역_순서_정렬됨(response, Arrays.asList(정자역, 강남역, 양재역));
    }

    @DisplayName("지하철 노선에 구간을 등록한다: 역 사이에 새로운 역을 등록할 경우 (1)")
    @Test
    void addLineSectionBetweenFront() {
        // when
        지하철_노선에_지하철역_등록_요청(신분당선, 강남역, 정자역, 3);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_노선에_지하철역_등록됨(response);
        지하철_노선에_지하철역_순서_정렬됨(response, Arrays.asList(강남역, 정자역, 양재역));
    }

    @DisplayName("지하철 노선에 구간을 등록한다: 역 사이에 새로운 역을 등록할 경우 (2)")
    @Test
    void addLineSectionBetweenBack() {
        // when
        지하철_노선에_지하철역_등록_요청(신분당선, 정자역, 양재역, 3);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_노선에_지하철역_등록됨(response);
        지하철_노선에_지하철역_순서_정렬됨(response, Arrays.asList(강남역, 정자역, 양재역));
    }

    @DisplayName("지하철 노선에 이미 포함된 역을 구간으로 등록할 수 없음")
    @Test
    void addLineSectionExceptionThatExisted() {
        // given
        지하철_노선에_지하철역_등록_요청(신분당선, 양재역, 정자역, 6);

        // when
        ExtractableResponse<Response> response1 = 지하철_노선에_지하철역_등록_요청(신분당선, 양재역, 정자역, 6);
        ExtractableResponse<Response> response2 = 지하철_노선에_지하철역_등록_요청(신분당선, 강남역, 양재역, 10);
        ExtractableResponse<Response> response3 = 지하철_노선에_지하철역_등록_요청(신분당선, 강남역, 정자역, 16);

        // then
        지하철_노선에_지하철역_등록_실패됨(response1);
        지하철_노선에_지하철역_등록_실패됨(response2);
        지하철_노선에_지하철역_등록_실패됨(response3);
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void addLineSectionExceptionThatIsMoreThanDistance() {
        // given
        지하철_노선에_지하철역_등록_요청(신분당선, 양재역, 광교역, 10);

        // when
        ExtractableResponse<Response> response1 = 지하철_노선에_지하철역_등록_요청(신분당선, 양재역, 정자역, 15);
        ExtractableResponse<Response> response2 = 지하철_노선에_지하철역_등록_요청(신분당선, 정자역, 광교역, 15);

        // then
        지하철_노선에_지하철역_등록_실패됨(response1);
        지하철_노선에_지하철역_등록_실패됨(response2);
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void addLineSectionExceptionThatDoseNotExist() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선, 정자역, 광교역, 6);

        // then
        지하철_노선에_지하철역_등록_실패됨(response);
    }

    @DisplayName("지하철 노선에 등록된 지하철역을 제거: 기점 제거")
    @Test
    void removeLineSectionThatFirst() {
        // given
        지하철_노선에_지하철역_등록_요청(신분당선, 양재역, 정자역, 6);

        // when
        ExtractableResponse<Response> removeResponse = 지하철_노선에_지하철역_제외_요청(신분당선, 강남역);

        // then
        지하철_노선에_지하철역_제외됨(removeResponse);
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_노선에_지하철역_순서_정렬됨(response, Arrays.asList(양재역, 정자역));
    }

    @DisplayName("지하철 노선에 등록된 지하철역을 제거: 종점 제거")
    @Test
    void removeLineSectionThatLast() {
        // given
        지하철_노선에_지하철역_등록_요청(신분당선, 양재역, 정자역, 6);

        // when
        ExtractableResponse<Response> removeResponse = 지하철_노선에_지하철역_제외_요청(신분당선, 정자역);

        // then
        지하철_노선에_지하철역_제외됨(removeResponse);
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_노선에_지하철역_순서_정렬됨(response, Arrays.asList(강남역, 양재역));
    }

    @DisplayName("지하철 노선에 등록된 지하철역을 제거: 사이역 제거")
    @Test
    void removeLineSectionThatMiddle() {
        // given
        지하철_노선에_지하철역_등록_요청(신분당선, 양재역, 정자역, 6);

        // when
        ExtractableResponse<Response> removeResponse = 지하철_노선에_지하철역_제외_요청(신분당선, 양재역);

        // then
        지하철_노선에_지하철역_제외됨(removeResponse);
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_노선에_지하철역_순서_정렬됨(response, Arrays.asList(강남역, 정자역));
    }

    @DisplayName("지하철 노선 삭제 실패: 지하철 노선에 구간이 하나일 경우")
    @Test
    void removeLineSectionOnlyOneSection() {
        // when
        ExtractableResponse<Response> removeResponse = 지하철_노선에_지하철역_제외_요청(신분당선, 양재역);

        // then
        지하철_노선에_지하철역_제외_실패됨(removeResponse);
    }

    @DisplayName("지하철 노선 삭제 실패: 존재하지 않는 노선을 삭제할 경우")
    @Test
    void removeLineSectionDoseNotExist() {
        // given
        지하철_노선에_지하철역_등록_요청(신분당선, 양재역, 정자역, 6);

        // when
        ExtractableResponse<Response> removeResponse = 지하철_노선에_지하철역_제외_요청(신분당선, 광교역);

        // then
        지하철_노선에_지하철역_제외_실패됨(removeResponse);
    }

    public static void 지하철_노선에_지하철역_등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철_노선에_지하철역_등록_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static void 지하철_노선에_지하철역_순서_정렬됨(ExtractableResponse<Response> response, List<StationResponse> expectedStations) {
        LineResponse line = response.as(LineResponse.class);
        List<Long> stationIds = line.getStations().stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        List<Long> expectedStationIds = expectedStations.stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        assertThat(stationIds).containsExactlyElementsOf(expectedStationIds);
    }

    public static void 지하철_노선에_지하철역_제외됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철_노선에_지하철역_제외_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
