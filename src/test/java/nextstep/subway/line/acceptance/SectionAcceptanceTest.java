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
import java.util.Map;

import static nextstep.subway.line.acceptance.LineSteps.*;
import static nextstep.subway.line.acceptance.SectionSteps.*;
import static nextstep.subway.station.StationSteps.지하철역_등록되어_있음;
import static org.assertj.core.api.Assertions.assertThat;

public class SectionAcceptanceTest extends AcceptanceTest {

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

        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", "신분당선");
        lineCreateParams.put("color", "bg-red-600");
        lineCreateParams.put("upStationId", String.valueOf(강남역.getId()));
        lineCreateParams.put("downStationId", String.valueOf(양재역.getId()));
        lineCreateParams.put("distance", String.valueOf(10));

        신분당선 = 지하철_노선_등록되어_있음(lineCreateParams).as(LineResponse.class);
    }

    //Happy Path
    @DisplayName("지하철 노선에 구간을 등록한다.")
    @Test
    void addLineSection() {
        //given
        Map<String,String> sectionRequest = makeSectionCreateParam();

        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록(신분당선.getId(),sectionRequest);

        // then
        지하철_노선_구간_응답_확인(response.statusCode(), HttpStatus.OK);
//        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();
    }

    @DisplayName("지하철 노선에 등록된 구간을 제거한다.")
    @Test
    void removeSection() {
        // given [신분당선]강남역-양재역-정자역
        Map<String,String> sectionRequest = makeSectionCreateParam();
        지하철_노선에_구간_등록(신분당선.getId(),sectionRequest);

        // when
        ExtractableResponse<Response> removeResponse = 지하철_노선에서_구간_제거(신분당선.getId(),정자역.getId());

        // then
        지하철_노선_구간_응답_확인(removeResponse.statusCode(),HttpStatus.NO_CONTENT);
    }

    @DisplayName("지하철 노선에 등록된 하행역(마지막)을 조회한다.")
    @Test
    void getLastStation(){
        // when
        ExtractableResponse<Response> response = 지하철_노선의_마지막역_조회(신분당선.getId());

        // then
        지하철_노선_구간_응답_확인(response.statusCode(),HttpStatus.OK);
        assertThat(response.jsonPath().getLong("id")).isEqualTo(양재역.getId());
    }

    private Map<String,String> makeSectionCreateParam(){
        Map<String, String> sectionCreateParams = new HashMap<>();
        sectionCreateParams.put("upStationId", java.lang.String.valueOf(양재역.getId()));
        sectionCreateParams.put("downStationId", java.lang.String.valueOf(정자역.getId()));
        sectionCreateParams.put("distance", java.lang.String.valueOf(17));

        return sectionCreateParams;
    }

    private void 지하철_노선_구간_응답_확인(int statusCode, HttpStatus status) {
        assertThat(statusCode).isEqualTo(status.value());
    }
}
