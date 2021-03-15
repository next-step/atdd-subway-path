package nextstep.subway.line.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.line.acceptance.LineSteps.*;
import static nextstep.subway.station.StationSteps.지하철역_등록되어_있음;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선에 역 등록 관련 기능")
public class LineSectionAcceptanceTest extends AcceptanceTest {
    private LineResponse 신분당선;
    private LineResponse 이호선;
    private LineResponse 삼호선;

    private StationResponse 강남역;
    private StationResponse 교대역;
    private StationResponse 남부터미널역;
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
        교대역 = 지하철역_등록되어_있음("교대역").as(StationResponse.class);
        남부터미널역 = 지하철역_등록되어_있음("남부터미널역").as(StationResponse.class);

        신분당선 = LineSteps.지하철_노선_등록되어_있음("신분당선", "bg-red-600", 강남역.getId(), 양재역.getId(), 10);
        이호선 = 지하철_노선_등록되어_있음("2호선", "bg-red-600", 교대역.getId(), 강남역.getId(), 6);
        삼호선 = 지하철_노선_등록되어_있음("3호선", "bg-red-600", 교대역.getId(), 양재역.getId(), 5);

        지하철_노선에_지하철역_등록_요청(삼호선, 교대역, 남부터미널역, 3);
    }

    @DisplayName("지하철 노선에 구간을 등록한다.")
    @Test
    void addLineSection() {
        // when
        지하철_노선에_지하철역_등록_요청(신분당선, 양재역, 정자역, 6);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_노선에_지하철역_등록됨(response);
        지하철_노선에_지하철역_순서_정렬됨(response, Arrays.asList(강남역, 양재역, 정자역));
    }

    @DisplayName("지하철 노선에 이미 포함된 역을 구간으로 등록한다.")
    @Test
    void addLineSectionAlreadyIncluded() {// given
        지하철_노선에_지하철역_등록_요청(신분당선, 양재역, 정자역, 6);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선, 양재역, 정자역, 6);

        // then
        지하철_노선에_지하철역_등록_실패됨(response);
    }

    @DisplayName("지하철 노선에 등록된 지하철역을 제외한다.")
    @Test
    void removeLineSection() {
        // given
        지하철_노선에_지하철역_등록_요청(신분당선, 양재역, 정자역, 6);

        // when
        ExtractableResponse<Response> removeResponse = 지하철_노선에_지하철역_제외_요청(신분당선, 정자역);

        // then
        지하철_노선에_지하철역_제외됨(removeResponse);
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_노선에_지하철역_순서_정렬됨(response, Arrays.asList(강남역, 양재역));
    }

    @DisplayName("지하철 노선에 구간이 하나일 때 지하철역을 제외한다.")
    @Test
    void removeLineSectionOnlyOneSection() {
        // when
        ExtractableResponse<Response> removeResponse = 지하철_노선에_지하철역_제외_요청(신분당선, 양재역);

        // then
        지하철_노선에_지하철역_제외_실패됨(removeResponse);
    }

    public static void 지하철_노선에_지하철역_등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철_노선에_지하철역_등록_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 지하철_노선에_지하철역_순서_정렬됨(ExtractableResponse<Response> response, List<StationResponse> expectedStations) {
        LineResponse line = response.as(LineResponse.class);
        List<Long> stationIds = line.getStations().stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());

        List<Long> expectedStationIds = expectedStations.stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());

        assertThat(stationIds).containsExactlyElementsOf(expectedStationIds);
    }

    public static void 지하철_노선에_지하철역_제외됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 지하철_노선에_지하철역_제외_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("역 사이에 새로운 구간을 추가한다.")
    @Test
    void addSectionBetweenStations() {
        //when
        ExtractableResponse<Response> response1 = 지하철_노선에_지하철역_등록_요청(신분당선, 강남역, 정자역, 5);
        ExtractableResponse<Response> response2 = 지하철_노선에_지하철역_등록_요청(신분당선, 광교역, 양재역, 2);

        ExtractableResponse<Response> readResponse = 지하철_노선_조회_요청(신분당선);

        //then
        지하철_노선에_지하철역_등록됨(response1);
        지하철_노선에_지하철역_등록됨(response2);
        지하철_노선에_지하철역_순서_정렬됨(readResponse, Arrays.asList(강남역, 정자역, 광교역, 양재역));
    }

    @DisplayName("새로운 역을 상행역으로하여 구간을 추가한다. ")
    @Test
    void addSectionPreviousUpStation() {
        //when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선, 정자역, 강남역, 5);
        ExtractableResponse<Response> readResponse = 지하철_노선_조회_요청(신분당선);

        //then
        지하철_노선에_지하철역_등록됨(response);
        지하철_노선에_지하철역_순서_정렬됨(readResponse, Arrays.asList(정자역, 강남역, 양재역));
    }

    @DisplayName("역 사이에 기존 구간보다 길이가 긴 구간을 추가한다.")
    @Test
    void addSectionBetweenStationsWithInvalidDistance() {
        //when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선, 강남역, 정자역, 15);

        //then
        지하철_노선에_지하철역_등록_실패됨(response);
    }

    @DisplayName("이미 등록된 상행역과 하행역 구간을 등록한다.")
    @Test
    void addSectionDuplicateAllStation() {
        //when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선, 강남역, 양재역, 10);

        //then
        지하철_노선에_지하철역_등록_실패됨(response);
    }

    @DisplayName("상행역과 하행역에 모두 포함되지 않는 구간을 추가한다.")
    @Test
    void addSectionNotIncludeStations() {
        //when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선, 정자역, 광교역, 10);

        //then
        지하철_노선에_지하철역_등록_실패됨(response);
    }

    @DisplayName("지하철 노선 구간의 중간에 있는 역을 삭제한다.")
    @Test
    void deleteSectionBetweenStations() {
        //given
        지하철_노선에_지하철역_등록_요청(신분당선, 양재역, 정자역, 10);
        지하철_노선에_지하철역_등록_요청(신분당선, 정자역, 광교역, 10);


        //when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_제외_요청(신분당선, 양재역);

        //then
        ExtractableResponse<Response> lineResponse = 지하철_노선_조회_요청(신분당선);
        지하철_노선에_지하철역_제외됨(response);
        지하철_노선에_지하철역_순서_정렬됨(lineResponse, Arrays.asList(강남역, 정자역, 광교역));
    }

    @DisplayName("지하철 노선 구간에 등록되지 않은 역을 삭제한다.")
    @Test
    void deleteSectionNotIncludeStation() {
        //given
        지하철_노선에_지하철역_등록_요청(신분당선, 양재역, 정자역, 10);

        //when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_제외_요청(신분당선, 광교역);

        //then
        지하철_노선에_지하철역_제외_실패됨(response);
    }


}
