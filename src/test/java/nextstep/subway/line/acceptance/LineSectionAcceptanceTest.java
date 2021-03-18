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
    private StationResponse 강남역1;
    private StationResponse 양재역2;
    private StationResponse 정자역3;
    private StationResponse 광교역4;

    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역1 = 지하철역_등록되어_있음("강남역1").as(StationResponse.class);
        양재역2 = 지하철역_등록되어_있음("양재역2").as(StationResponse.class);
        정자역3 = 지하철역_등록되어_있음("정자역3").as(StationResponse.class);
        광교역4 = 지하철역_등록되어_있음("광교역4").as(StationResponse.class);

        LineRequest lineRequest = 파라미터_생성("신분당선", "red", 강남역1.getId(), 양재역2.getId(), 10);
        신분당선 = 지하철_노선_등록되어_있음(lineRequest).as(LineResponse.class);
    }

    @DisplayName("지하철 노선에 구간을 등록한다.")
    @Test
    void appendLineSection() {
        // when
        지하철_노선에_지하철역_등록_요청(신분당선, 양재역2, 정자역3, 6);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_노선에_지하철역_등록됨(response);
        지하철_노선에_지하철역_순서_정렬됨(response, Arrays.asList(강남역1, 양재역2, 정자역3));
    }

    @DisplayName("지하철역_추가시_중간에_끼워넣기")
    @Test
    void 지하철역_추가시_중간에_끼워넣기(){
        //when
        지하철_노선에_지하철역_등록_요청(신분당선, 광교역4, 양재역2, 3);
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        //then
        지하철_노선에_지하철역_등록됨(response);
        지하철_노선에_지하철역_순서_정렬됨(response, Arrays.asList(강남역1, 광교역4, 양재역2));
    }

    @DisplayName("지하철 노선에 이미 포함된 역을 구간으로 등록한다.")
    @Test
    void addLineSectionAlreadyIncluded() {// given
        지하철_노선에_지하철역_등록_요청(신분당선, 양재역2, 정자역3, 6);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선, 양재역2, 정자역3, 6);

        // then
        지하철_노선에_지하철역_등록_실패됨(response);
    }

    @DisplayName("지하철 노선에 등록된 지하철역을 제외한다.")
    @Test
    void removeLineSection() {
        // given
        지하철_노선에_지하철역_등록_요청(신분당선, 양재역2, 정자역3, 6);

        // when
        ExtractableResponse<Response> removeResponse = 지하철_노선에_지하철역_제외_요청(신분당선, 정자역3);

        // then
        지하철_노선에_지하철역_제외됨(removeResponse);
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        지하철_노선에_지하철역_순서_정렬됨(response, Arrays.asList(강남역1, 양재역2));
    }

    @DisplayName("지하철 노선에 구간이 하나일 때 지하철역을 제외한다.")
    @Test
    void removeLineSectionOnlyOneSection() {
        // when
        ExtractableResponse<Response> removeResponse = 지하철_노선에_지하철역_제외_요청(신분당선, 양재역2);

        // then
        지하철_노선에_지하철역_제외_실패됨(removeResponse);
    }
}
