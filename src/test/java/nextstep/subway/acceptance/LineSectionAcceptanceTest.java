package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.enums.exceptions.ErrorCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.LineSteps.지하철_노선에_지하철_구간_생성_요청;
import static nextstep.subway.acceptance.SectionSteps.특정_구간_조회;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static nextstep.subway.assertionsFixtures.SectionAssertions.*;
import static nextstep.subway.assertionsFixtures.StatusAssertions.상태_검증;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관리 기능")
class LineSectionAcceptanceTest extends AcceptanceTest {
    private Long 신분당선;

    private Long 강남역;
    private Long 양재역;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");

        Map<String, String> lineCreateParams = createLineCreateParams(강남역, 양재역);
        신분당선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
    }

    /**
     * When 지하철 노선에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("지하철 노선에 구간을 등록")
    @Test
    void addLineSection() {
        // when
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, "6"));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        상태_검증(HttpStatus.OK, response);
        구간에_포함된_역_검증(response, 강남역, 양재역, 정자역);
    }

    /**
     * given : 지하철 노선에 구간을 등록
     * when : 기존 A - C의 구간을 가진 노선에 A - B 구간을 추가하면
     * then : A - B - C ( A - B ), ( B - C ) 총 두개의 구간이 생긴다.
     */
    @DisplayName("역 사이에 새로운 역 등록하기")
    @Test
    void addStationBetweenSection() {
        //given : 강남역 - 양재역

        //when
        long 강남역_양재역_사이역 = 지하철역_생성_요청("강남역_양재역_사이역").jsonPath().getLong("id");

        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 강남역_양재역_사이역, "4"));
        ExtractableResponse<Response> response = 특정_구간_조회(신분당선);

        //then
        노선_포함된_거리_검증(response, 4, 6);
        상행선_포함_검증(response, "강남역", "강남역_양재역_사이역");
        하행선_포함_검증(response, "강남역_양재역_사이역", "양재역");
    }

    /**
     * given : 지하철 노선에 구간을 등록
     * when : 새로운 역(신논현역)을 상행 종점으로 구간을 등록한다.
     * then : 강남역 - 양재역 일 때 신논현역 - 강남역 구간을 등록하면 신논현역 - 강남역 - 양재역이 된다.
     */
    @DisplayName("새로운 역을 상행 종점으로 등록하기")
    @Test
    void addNewStationLineOnTheTop() {
        // given : 강남역 - 양재역

        // when
        long 신논현역 = 지하철역_생성_요청("신논현역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신논현역, 강남역, "5"));
        ExtractableResponse<Response> response = 특정_구간_조회(신분당선);

        노선_포함된_거리_검증(response, 5, 10);
        상행선_포함_검증(response, "신논현역", "강남역");
        하행선_포함_검증(response, "강남역", "양재역");

    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존에 등록되어있는 역 사이 길이보다 크거나 같으면 Exception")
    @Test
    void notEnoughDistanceFailTest() {
        //given : 강남역 - 양재역

        //when
        long 강남역_양재역_사이역 = 지하철역_생성_요청("강남역_양재역_사이역").jsonPath().getLong("id");

        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 강남역_양재역_사이역, "100"));

        상태_검증(ErrorCode.NOT_ENOUGH_DISTANCE, HttpStatus.BAD_REQUEST, response);
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 Exception")
    @Test
    void isSameUpDownStationFailTest() {
        //given : 강남역 - 양재역

        //when
        long 신논현역 = 지하철역_생성_요청("신논현역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신논현역, 강남역, "4"));

        long 논현역 = 지하철역_생성_요청("논현역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(논현역, 신논현역, "3"));
        특정_구간_조회(신분당선);

        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(논현역, 양재역, "5"));
//
//        //then
        상태_검증(ErrorCode.IS_SAME_UP_DOWN_STATION, HttpStatus.BAD_REQUEST, response);
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 Exception")
    @Test
    void notContainUpDownStationFailTest() {
        //given : 강남역 - 양재역

        //when
        long 신논현역 = 지하철역_생성_요청("신논현역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신논현역, 강남역, "4"));

        long 논현역 = 지하철역_생성_요청("논현역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(논현역, 신논현역, "3"));
        특정_구간_조회(신분당선);

        long 신림역 = 지하철역_생성_요청("신림역").jsonPath().getLong("id");
        long 당곡역 = 지하철역_생성_요청("당곡역").jsonPath().getLong("id");
        ExtractableResponse<Response> response = 지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(신림역, 당곡역, "10"));

        //then
        상태_검증(ErrorCode.IS_NOT_CONTAIN_STATION, HttpStatus.BAD_REQUEST, response);
    }

    /**
     * given : 지하철 노선에 구간을 등록
     * when : 새로운 역(서울숲)을 하행 종점으로 구간을 등록한다.
     * then : 강남역 - 양재역 일 때 서울숲을 등록하면 강남역 - 양재역 - 서울숲이 된다.
     */
    @DisplayName("새로운 역을 하행 종점으로 등록하기")
    @Test
    void addNewStationLineOnTheBottom() {
        //given : 강남역 - 양재역

        //when
        long 서울숲 = 지하철역_생성_요청("서울숲").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 서울숲, "11"));
        ExtractableResponse<Response> response = 특정_구간_조회(신분당선);

        //then
        Assertions.assertAll(() -> {
            노선_포함된_거리_검증(response, 10, 11);
            상행선_포함_검증(response, "강남역", "양재역");
            하행선_포함_검증(response, "양재역", "서울숲");
        });
    }


    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청 하고
     * When 지하철 노선의 마지막 구간 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("지하철 노선에 구간을 제거")
    @Test
    void removeLineSection() {
        // given
        Long 정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(양재역, 정자역, "6"));

        // when
        지하철_노선에_지하철_구간_제거_요청(신분당선, 정자역);

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);
        상태_검증(HttpStatus.OK, response);
        구간에_포함된_역_검증(response, 강남역, 양재역);
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

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, String distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance);
        return params;
    }
}
