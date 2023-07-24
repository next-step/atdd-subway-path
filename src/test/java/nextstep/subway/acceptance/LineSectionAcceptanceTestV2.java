package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("새로운 지하철 구간 관리 기능")
public class LineSectionAcceptanceTestV2 extends AcceptanceTest{

    private Long 신분당선;
    private Long 강남역;
    private Long 양재역;

    /**
     * Given 지하철역과 노선 생성을 요청하고
     */
    @BeforeEach
    public void setUp(){
        super.setUp();

        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");

        Map<String, String> lineCreateParams = createLineCreateParams(강남역, 양재역);
        신분당선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
    }

    /**
     *  Given   기존 구간의 역 사이에 새로운 역 생성을 요청하고
     *  When    지하철 노선에 새로운 구간 추가를 요청하면
     *  Then    노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 구간을 등록 시에 역 사이에 새로운 역을 등록")
    @Test
    void addLineSectionWhenRegisterBetweenSections(){
        //given
        final Long 중간역 = 지하철역_생성_요청("중간역").jsonPath().getLong("id");
        final Integer 구간_길이 = 6;

        //when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 중간역, 구간_길이));

        //then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactlyInAnyOrder(강남역, 중간역, 양재역);
    }

    /**
     *  Given   기존 구간의 역보다 상행역 생성을 요청하고
     *  When    지하철 노선에 새로운 구간 추가를 요청하면
     *  Then    노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 구간을 등록 시에 새로운 역을 상행 종점으로 등록")
    @Test
    void addLineSectionWhenRegisterUpStation(){
        //given
        final Long 상행역 = 지하철역_생성_요청("상행역").jsonPath().getLong("id");
        final Integer 구간_길이 = 6;

        //when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(상행역, 강남역, 구간_길이));

        //then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactlyInAnyOrder(상행역, 강남역, 양재역);
    }

    /**
     *  Given   기존 구간의 역보다 하행역 생성을 요청하고
     *  When    지하철 노선에 새로운 구간 추가를 요청하면
     *  Then    노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 구간을 등록 시에 새로운 역을 히행 종점으로 등록")
    @Test
    void addLineSectionWhenRegisterDownStation(){
        //given
        final Long 하행역 = 지하철역_생성_요청("하행역").jsonPath().getLong("id");
        final Integer 구간_길이 = 6;

        //when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 하행역, 구간_길이));

        //then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactlyInAnyOrder(강남역, 양재역, 하행역);
    }

    /**
     *  When    지하철 노선의 길이보다 긴 길이의 새로운 구간 추가를 요청하면
     *  Then    예외가 발생한다
     */
    @DisplayName("지하철 노선에 구간을 등록 시에 기존 역 사이 길이보다 크거나 같으면 등록할 수 없다")
    @Test
    void cannotAddLineSectionIfSectionDistanceIsLargerThanLineDistance(){
        //When
        final Long 중간역 = 지하철역_생성_요청("중간역").jsonPath().getLong("id");
        final String errorMessage = "지하철 노선에 구간을 등록 시에 기존 역 사이 길이보다 크거나 같으면 등록할 수 없습니다";
        final Integer 구간_길이 = 10;

        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 중간역, 구간_길이));

        //then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.jsonPath().getString("message")).isEqualTo(errorMessage);
    }

    /**
     *  When    지하철 노선에 이미 등록된 구간을 추가를 요청하면
     *  Then    예외가 발생한다
     */
    @DisplayName("지하철 노선에 구간을 등록 시에 상행역과 하행역이 이미 노선에 등록되어 있다면 추가할 수 없다")
    @Test
    void cannotAddLineSectionIfUpStationAndDownStationAlreadyExisted(){
        //when
        final String errorMessage = "지하철 노선에 구간을 등록 시에 상행역과 하행역이 이미 노선에 등록되어 있다면 추가할 수 없습니다";
        final Integer 구간_길이 = 6;

        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역, 구간_길이));

        //then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.jsonPath().getString("message")).isEqualTo(errorMessage);
    }

    /**
     *  Given   기존 구간에 존재하지 않는 새로운 역 생성을 요청하고
     *  When    지하철 노선에 등록되지 않은 역으로 이루어진 구간 추가를 요청하면
     *  The     예외가 발생한다
     */
    @DisplayName("지하철 노선에 구간을 등록 시에 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없다")
    @Test
    void cannotAddLineSectionIfSectionUpStationAndDownStationNotExistedInLine(){
        //given
        final Long 새로운_상행역 = 지하철역_생성_요청("새로운_상행역").jsonPath().getLong("id");
        final Long 새로운_하행역 = 지하철역_생성_요청("새로운_하행역").jsonPath().getLong("id");
        final String errorMessage = "지하철 노선에 구간을 등록 시에 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없습니다";
        final Integer 구간_길이 = 6;

        //when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(새로운_상행역, 새로운_하행역, 구간_길이));

        //then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.jsonPath().getString("message")).isEqualTo(errorMessage);
    }

    private Map<String, String> createLineCreateParams(Long upStationId, Long downStationId) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", "신분당선");
        lineCreateParams.put("color", "bg-red-600");
        lineCreateParams.put("upStationId", upStationId + "");
        lineCreateParams.put("downStationId", downStationId + "");
        lineCreateParams.put("distance", 10 + "");
        return lineCreateParams;
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, Integer distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}
