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
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

import static nextstep.subway.line.acceptance.steps.LineSteps.지하철_노선_등록되어_있음;
import static nextstep.subway.line.acceptance.steps.SectionSteps.*;
import static nextstep.subway.station.StationSteps.지하철역_등록되어_있음;

@DisplayName("구간 추가 기능변경에 따른 인수테스트")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionAcceptanceTest extends AcceptanceTest {

    StationResponse 강남역, 양재역, 판교역, 정자역, 광교역;
    LineResponse 신분당선;

    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        // 역 순서 : 강남역 - [양재역] - 판교역 - [정자역] - 광교역
        강남역 = 지하철역_등록되어_있음("강남역").as(StationResponse.class);
        양재역 = 지하철역_등록되어_있음("양재역").as(StationResponse.class);
        판교역 = 지하철역_등록되어_있음("판교역").as(StationResponse.class);
        정자역 = 지하철역_등록되어_있음("정자역").as(StationResponse.class);
        광교역 = 지하철역_등록되어_있음("광교역").as(StationResponse.class);

        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", 양재역.getId(), 정자역.getId(), 10);
        신분당선 = 지하철_노선_등록되어_있음(lineRequest).as(LineResponse.class);
    }

    @DisplayName("지하철 노선 정보 조회")
    @Test
    void getLineById(){
        // when
        ExtractableResponse<Response> response = 지하철_노선_정보_조회_요청됨(신분당선);

        // then
        지하철_노선_정보_조회됨(response);
        지하철_노선에_역들이_순서대로_정렬됨(response, Arrays.asList(양재역.getId(), 정자역.getId()));
    }

    @DisplayName("지하철 노선 구간등록")
    @Test
    void addSection(){
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선, new SectionRequest(양재역.getId(), 판교역.getId(), 5));

        // then
        지하철_노선에_지하철역_등록_성공됨(response);
        등록된_지하철_노선에_역들이_순서대로_정렬됨(신분당선, Arrays.asList(양재역.getId(), 판교역.getId(), 정자역.getId()));
    }


    @DisplayName("지하철 노선 구간제거")
    @Test
    void removeSection(){
        // given
        지하철_노선에_지하철역_등록됨(신분당선, new SectionRequest(양재역.getId(), 판교역.getId(), 5));

        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_제거_요청(신분당선, 양재역);

        // then
        지하철_노선에_지하철역_제거_성공됨(response);
        지하철_노선에_지하철역_없음(신분당선, 양재역);
    }

}