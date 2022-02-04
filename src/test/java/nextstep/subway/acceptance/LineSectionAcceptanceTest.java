package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;

@DisplayName("지하철 구간 관리 기능")
class LineSectionAcceptanceTest extends AcceptanceTest {
    public static final int DEFAULT_DISTANCE = 10;
    private Long 신분당선;

    private static Long 양재역;
    private static Long 판교역;
    private static Long 정자역;

    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
        판교역 = 지하철역_생성_요청("판교역").jsonPath().getLong("id");
        정자역 = 지하철역_생성_요청("정자역").jsonPath().getLong("id");

        Map<String, String> lineCreateParams = createLineCreateParams(양재역, 정자역);
        신분당선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
    }

    @DisplayName("상행 종점역 추가")
    @Test
    void addLineFirstUpSection() {
        //given
        Long 강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");

        //when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(강남역, 양재역));

        //then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);

        구간_생성_요청_후_역_목록_확인(response, 강남역, 양재역, 정자역);
    }

    @DisplayName("하행 종점역 추가")
    @Test
    void addLineLastDownSection() {
        //given
        Long 미금역 = 지하철역_생성_요청("미금역").jsonPath().getLong("id");

        //when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(정자역, 미금역));

        //then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);

        구간_생성_요청_후_역_목록_확인(response, 양재역, 정자역, 미금역);
    }

    /**
     * When 지하철 노선에 새로운 구간 추가를 요청 하면
     * Then 노선에 새로운 구간이 추가된다
     */
    @DisplayName("역과 역사이 신규 역을 추가")
    @ParameterizedTest(name = "역과 역사이 신규역 추가 [{index}] [{arguments}]")
    @MethodSource
    void addLineSection(Long 상행역, Long 하행역) {
        // when
        지하철_노선에_지하철_구간_생성_요청(신분당선, createSectionCreateParams(상행역, 하행역, 5));

        // then
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선);

        구간_생성_요청_후_역_목록_확인(response, 양재역, 판교역, 정자역);
    }

    private static Stream<Arguments> addLineSection() {
        return Stream.of(
                Arguments.of(양재역, 판교역),
                Arguments.of(판교역, 정자역)
        );
    }

    @DisplayName("역과 역사이 신규 역을 추가 실패 - 기존 구간의 거리보다 같거나 큰 경우")
    @ParameterizedTest(name = "구간 길이 이상 실패 [{index}] [{arguments}]")
    @ValueSource(ints = {10, 11})
    void addLineSectionDistanceException(int distance) {
        //given
        Map<String, String> sectionCreateParams = createSectionCreateParams(양재역, 판교역, distance);

        //when
        ExtractableResponse<Response> 지하철_노선에_지하철_구간_생성_요청_응답 = 지하철_노선에_지하철_구간_생성_요청(신분당선, sectionCreateParams);

        //then
        구간_생성_요청_실패(지하철_노선에_지하철_구간_생성_요청_응답);
    }

    @DisplayName("역과 역사이 신규 역을 추가 실패 - 추가하려는 역이 이미 존재하는 경우")
    @Test
    void addLineSectionExistsStationsException() {
        //given
        Map<String, String> sectionCreateParams = createSectionCreateParams(양재역, 정자역);

        //when
        ExtractableResponse<Response> 지하철_노선에_지하철_구간_생성_요청_응답 = 지하철_노선에_지하철_구간_생성_요청(신분당선, sectionCreateParams);

        //then
        구간_생성_요청_실패(지하철_노선에_지하철_구간_생성_요청_응답);
    }

    @DisplayName("역과 역사이 신규 역을 추가 실패 - 추가하려는 역이 등록되어 있지 존재하는 경우")
    @Test
    void addLineSectionNotExistsStationsException() {
        //given
        Long 시청역 = 지하철역_생성_요청("시청역").jsonPath().getLong("id");
        Long 서울역 = 지하철역_생성_요청("서울역").jsonPath().getLong("id");
        Map<String, String> sectionCreateParams = createSectionCreateParams(시청역, 서울역);

        //when
        ExtractableResponse<Response> 지하철_노선에_지하철_구간_생성_요청_응답 = 지하철_노선에_지하철_구간_생성_요청(신분당선, sectionCreateParams);

        //then
        구간_생성_요청_실패(지하철_노선에_지하철_구간_생성_요청_응답);
    }

    @DisplayName("노선에 구간이 1개인 경우 역 삭제 실패")
    @Test
    void removeSectionMinimumSizeException() {
        //when
        ExtractableResponse<Response> 구간_삭제_요청_응답 = 지하철_노선에_지하철_구간_제거_요청(신분당선, 양재역);
        
        //then
        구간_삭제_요청_실패(구간_삭제_요청_응답);
    }
    
    @DisplayName("노선에 존재하지 않는 역 삭제 실패")
    @Test
    void removeSectionNotExistsException() {
        //when
        ExtractableResponse<Response> 구간_삭제_요청_응답 = 지하철_노선에_지하철_구간_제거_요청(신분당선, 판교역);

        //then
        구간_삭제_요청_실패(구간_삭제_요청_응답);
    }

    @DisplayName("지하철 노선의 구간 삭제")
    @ParameterizedTest
    @MethodSource
    void removeSection(Long stationId, Long... expected) {
        //given
        Map<String, String> sectionCreateParams = createSectionCreateParams(양재역, 판교역, 5);
        지하철_노선에_지하철_구간_생성_요청(신분당선, sectionCreateParams);
        
        //when
        지하철_노선에_지하철_구간_제거_요청(신분당선, stationId);

        //then
        구간_삭제_요청_후_역_목록_확인(지하철_노선_조회_요청(신분당선), expected);
    }
    
    private static Stream<Arguments> removeSection() {
        return Stream.of(
                Arguments.of(양재역, new Long[]{판교역, 정자역}),
                Arguments.of(판교역, new Long[]{양재역, 정자역}),
                Arguments.of(정자역, new Long[]{양재역, 판교역})
        );
    }



    private Map<String, String> createLineCreateParams(Long upStationId, Long downStationId) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", "신분당선");
        lineCreateParams.put("color", "bg-red-600");
        lineCreateParams.put("upStationId", upStationId + "");
        lineCreateParams.put("downStationId", downStationId + "");
        lineCreateParams.put("distance", DEFAULT_DISTANCE + "");
        return lineCreateParams;
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId) {
        return createSectionCreateParams(upStationId, downStationId, DEFAULT_DISTANCE);
    }

    private Map<String, String> createSectionCreateParams(Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}
