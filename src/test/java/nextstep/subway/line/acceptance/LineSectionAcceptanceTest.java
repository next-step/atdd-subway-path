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
    private StationResponse 고속터미널역;

    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_등록되어_있음("강남역").as(StationResponse.class);
        양재역 = 지하철역_등록되어_있음("양재역").as(StationResponse.class);
        정자역 = 지하철역_등록되어_있음("정자역").as(StationResponse.class);
        광교역 = 지하철역_등록되어_있음("광교역").as(StationResponse.class);
        고속터미널역 = 지하철역_등록되어_있음("고속터미널역").as(StationResponse.class);

        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", "신분당선");
        lineCreateParams.put("color", "bg-red-600");
        lineCreateParams.put("upStationId", 강남역.getId() + "");
        lineCreateParams.put("downStationId", 양재역.getId() + "");
        lineCreateParams.put("distance", 10 + "");
        신분당선 = 지하철_노선_등록되어_있음(lineCreateParams).as(LineResponse.class);
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
    void addLineSectionAlreadyIncluded() {
        // given
        지하철_노선에_지하철역_등록_요청(신분당선, 양재역, 정자역, 6);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선, 양재역, 정자역, 6);

        // then
        지하철_노선에_지하철역_등록_실패됨(response);
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 새로운 길이를 뺀 나머지를 새롭게 추가된 역과의 길이로 설정한다.")
    @Test
    void addLineSectionInMiddle(){
        //given
        //강남----------양재--------------------광교
        지하철_노선에_지하철역_등록_요청(신분당선, 양재역, 광교역, 20);

        //when
        //강남----------양재-------정자-------------광교
        지하철_노선에_지하철역_등록_요청(신분당선, 양재역, 정자역, 8);

        //then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_노선에_지하철역_순서_정렬됨(response, Arrays.asList(강남역, 양재역, 정자역, 광교역));
    }

    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void addNewStationAsFirst() {
        //given
        //강남-----양재

        //when
        //고속터미널-----강남-----양재
        지하철_노선에_지하철역_등록_요청(신분당선, 고속터미널역, 강남역, 5);

        //then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_노선에_지하철역_순서_정렬됨(response, Arrays.asList(고속터미널역, 강남역, 양재역));
    }

    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    @Test
    void addNewStationAsLast() {
        //given
        //강남----------양재-----정자
        지하철_노선에_지하철역_등록_요청(신분당선, 양재역, 정자역, 5);

        //when
        //강남----------양재-----정자-----광교
        지하철_노선에_지하철역_등록_요청(신분당선, 정자역, 광교역, 5);

        //then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_노선에_지하철역_순서_정렬됨(response, Arrays.asList(강남역, 양재역, 정자역, 광교역));
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음.")
    @Test
    void whenAddLineSectionInMiddleWithBiggerDistance_thenReturnError() {
        //given
        //강남----------양재-----광교
        지하철_노선에_지하철역_등록_요청(신분당선, 양재역, 광교역, 5);

        //when
        //양재-----정자 추가 시도
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선, 양재역, 정자역, 5);

        지하철_노선에_지하철역_등록_실패됨(response);
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음.")
    @Test
    void whenAddLineSectionWithStationAlreadyExists_thenFail() {
        //given
        //강남----------양재-----광교
        지하철_노선에_지하철역_등록_요청(신분당선, 양재역, 광교역, 5);

        //when
        //양재-------광교 추가 시도
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선, 양재역, 광교역, 7);
        지하철_노선에_지하철역_등록_실패됨(response);
    }


    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어 있지 않으면 추가할 수 없음")
    @Test
    void whenAddLineSectionWithNoStationIncludedAtAll_thenFail() {
        //given
        //강남----------양재

        //when
        //정자-----광교 추가 시도
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선, 정자역, 광교역, 5);
        지하철_노선에_지하철역_등록_실패됨(response);
    }

    @DisplayName("지하철 노선에 등록된 지하철역을 제외한다.")
    @Test
    void removeFinalSection() {
        // given
        // 강남----------양재------정자
        지하철_노선에_지하철역_등록_요청(신분당선, 양재역, 정자역, 6);

        // when
        // 정자역 삭제 시도
        ExtractableResponse<Response> removeResponse = 지하철_노선에_지하철역_제외_요청(신분당선, 정자역);

        // then
        지하철_노선에_지하철역_제외됨(removeResponse);
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_노선에_지하철역_순서_정렬됨(response, Arrays.asList(강남역, 양재역));
    }

    @DisplayName("지하철 노선에 등록된 상행 종점역을 제외한다.")
    @Test
    void removeFirstSection(){
        // given
        // 강남----------양재------정자
        지하철_노선에_지하철역_등록_요청(신분당선, 양재역, 정자역, 6);

        // when
        // 강남역 삭제 시도
        ExtractableResponse<Response> removeResponse = 지하철_노선에_지하철역_제외_요청(신분당선, 강남역);

        // then
        지하철_노선에_지하철역_제외됨(removeResponse);
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_노선에_지하철역_순서_정렬됨(response, Arrays.asList(양재역, 정자역));
    }

    @DisplayName("지하철 노선에 등록된 중간에 위치한 지하철역을 제외한다.")
    @Test
    void removeLineSection_WhenStationInMiddle(){
        // given
        // 강남----------양재------정자
        지하철_노선에_지하철역_등록_요청(신분당선, 양재역, 정자역, 6);

        // when
        // 양재역 삭제 시도
        ExtractableResponse<Response> removeResponse = 지하철_노선에_지하철역_제외_요청(신분당선, 양재역);

        // then
        지하철_노선에_지하철역_제외됨(removeResponse);
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_노선에_지하철역_순서_정렬됨(response, Arrays.asList(강남역, 정자역));
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
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
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
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
