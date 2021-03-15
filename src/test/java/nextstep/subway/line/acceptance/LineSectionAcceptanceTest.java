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

    private LineResponse 신라인_1;

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

        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", "신라인_1");
        lineCreateParams.put("color", "bg-red-600");
        lineCreateParams.put("upStationId", 강남역.getId() + "");
        lineCreateParams.put("downStationId", 양재역.getId() + "");
        lineCreateParams.put("distance", 10 + "");
        신라인_1 = 지하철_노선_등록되어_있음(lineCreateParams).as(LineResponse.class);
    }

    @DisplayName("지하철 노선에 구간을 등록한다.")
    @Test
    void addLineSection() {
        // when
        지하철_노선에_지하철역_등록_요청(신라인_1, 양재역, 정자역, 6);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신라인_1);
        지하철_노선에_지하철역_등록됨(response);
        지하철_노선에_지하철역_순서_정렬됨(response, Arrays.asList(강남역, 양재역, 정자역));
    }

    @DisplayName("지하철 노선에 이미 포함된 역을 구간으로 등록한다.")
    @Test
    void addLineSectionAlreadyIncluded() {// given
        지하철_노선에_지하철역_등록_요청(신라인_1, 양재역, 정자역, 6);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신라인_1, 양재역, 정자역, 6);

        // then
        지하철_노선에_지하철역_등록_실패됨(response);
    }

    @DisplayName("지하철 노선에 등록된 지하철역을 제외한다.")
    @Test
    void removeLineSection() {
        // given
        지하철_노선에_지하철역_등록_요청(신라인_1, 양재역, 정자역, 6);

        // when
        ExtractableResponse<Response> removeResponse = 지하철_노선에_지하철역_제외_요청(신라인_1, 정자역);

        // then
        지하철_노선에_지하철역_제외됨(removeResponse);
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신라인_1);
        지하철_노선에_지하철역_순서_정렬됨(response, Arrays.asList(강남역, 양재역));
    }

    @DisplayName("지하철 노선에 구간이 하나일 때 지하철역을 제외한다.")
    @Test
    void removeLineSectionOnlyOneSection() {
        // when
        ExtractableResponse<Response> removeResponse = 지하철_노선에_지하철역_제외_요청(신라인_1, 양재역);

        // then
        지하철_노선에_지하철역_제외_실패됨(removeResponse);
    }

    /**
     * TDD_Step1 - 인수테스트 추가
     */
    @DisplayName("지하철 노선에 등록되어 있는 구간 사이에 새로운 구간을 등록한다. (상행기준)")
    @Test
    void addLineSectionBetweenToUpStation() {
        // given (강남역 - 10 - 양재역 - 20 - 광교역)
        지하철_노선에_지하철역_등록_요청(신라인_1, 양재역, 광교역, 20);

        // when (양재역 - 7 - 정자역)
        ExtractableResponse<Response> response1 = 지하철_노선에_지하철역_등록_요청(신라인_1, 양재역, 정자역, 7);
        ExtractableResponse<Response> response2 = 지하철_노선_조회_요청(신라인_1);

        // then
        지하철_노선에_지하철역_등록됨(response1);
        지하철_노선에_지하철역_순서_정렬됨(response2, Arrays.asList(강남역, 양재역, 정자역, 광교역));
    }

    @DisplayName("지하철 노선에 등록되어 있는 구간 사이에 새로운 구간을 등록한다. (하행기준)")
    @Test
    void addLineSectionBetweenToDownStation() {
        // given (강남역 - 10 - 양재역 - 20 - 광교역)
        지하철_노선에_지하철역_등록_요청(신라인_1, 양재역, 광교역, 20);

        // when (양재역 - 7 - 정자역)
        ExtractableResponse<Response> response1 = 지하철_노선에_지하철역_등록_요청(신라인_1, 정자역, 광교역, 7);
        ExtractableResponse<Response> response2 = 지하철_노선_조회_요청(신라인_1);

        // then
        지하철_노선에_지하철역_등록됨(response1);
        지하철_노선에_지하철역_순서_정렬됨(response2, Arrays.asList(강남역, 양재역, 정자역, 광교역));
    }

    @DisplayName("지하철 노선에 등록되어 있는 구간 상행에 새로운 구간을 등록한다.")
    @Test
    void addLineSectionToUpStation() {
        // given (강남역 - 10 - 양재역 - 20 - 광교역)
        지하철_노선에_지하철역_등록_요청(신라인_1, 양재역, 광교역, 20);

        // when (정자역 - 7 - 강남역)
        ExtractableResponse<Response> response1 = 지하철_노선에_지하철역_등록_요청(신라인_1, 정자역, 강남역, 7);
        ExtractableResponse<Response> response2 = 지하철_노선_조회_요청(신라인_1);

        // then
        지하철_노선에_지하철역_등록됨(response1);
        지하철_노선에_지하철역_순서_정렬됨(response2, Arrays.asList(정자역, 강남역, 양재역, 광교역));
    }

    @DisplayName("지하철 노선에 등록되어 있는 구간 하행에 새로운 구간을 등록한다.")
    @Test
    void addLineSectionToDownStation() {
        // given (강남역 - 10 - 양재역 - 20 - 광교역)
        지하철_노선에_지하철역_등록_요청(신라인_1, 양재역, 광교역, 20);

        // when (정자역 - 7 - 강남역)
        ExtractableResponse<Response> response1 = 지하철_노선에_지하철역_등록_요청(신라인_1, 광교역, 정자역, 7);
        ExtractableResponse<Response> response2 = 지하철_노선_조회_요청(신라인_1);

        // then
        지하철_노선에_지하철역_등록됨(response1);
        지하철_노선에_지하철역_순서_정렬됨(response2, Arrays.asList(강남역, 양재역, 광교역, 정자역));
    }

    /**
     * TDD_Step2 - 인수테스트 추가
     *
     * 위치에 상관없이 역 삭제 가능함
     * 종점이 제거되면 다음(제거된 역이 상행) or 이전(제거된 역이 하행)역이 종점이 됨
     * 중간역이 제거되면 양 옆의 역이 하나의 구간으로 재배치 되고 길이는 두 구간의 합이 됨
     *
     * (예외 케이스)
     * 노선에 등록되어 있지 않은 역을 제거
     * 구간이 하나인 경우 역 제거
     */
    @DisplayName("지하철 노선에 등록된 중간역을 삭제한다.")
    @Test
    void removeLineStation() {
        // given (강남역 - 양재역 - 정자역)
        지하철_노선에_지하철역_등록_요청(신라인_1, 양재역, 정자역, 6);

        // when
        ExtractableResponse<Response> removeResponse = 지하철_노선에_지하철역_제외_요청(신라인_1, 양재역);

        // then
        지하철_노선에_지하철역_제외됨(removeResponse);
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신라인_1);
        지하철_노선에_지하철역_순서_정렬됨(response, Arrays.asList(강남역, 정자역));
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
