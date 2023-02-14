package nextstep.step1.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.acceptance.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


@DisplayName("구간 관리 - 추가 인수 테스트")
public class SectionAcceptanceTest extends AcceptanceTest {

    private Long 이호선;

    private Long 교대역;
    private Long 강남역;
    private Long 역삼역;
    private Long 선릉역;
    private Long 삼성역;
    private Long 잠실역;
    private Long 잠실나루역;


    /**
     * Given 지하철역과 노선 생성을 요청 하고
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        역삼역 = 지하철역_생성_요청("역삼역").jsonPath().getLong("id");
        선릉역 = 지하철역_생성_요청("선릉역").jsonPath().getLong("id");
        삼성역 = 지하철역_생성_요청("삼성역").jsonPath().getLong("id");
        잠실역 = 지하철역_생성_요청("잠실역").jsonPath().getLong("id");
        잠실나루역 = 지하철역_생성_요청("잠실나루역").jsonPath().getLong("id");

        Map<String, String> lineCreateParams = createLineCreateParams(강남역, 역삼역);
        이호선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(이호선, createSectionCreateParams(역삼역, 삼성역,10));
    }

    /**
     * When 노선의 상행종착역을 하행역으로 하는 구간을 추가
     * Then 노선의 구간 목록 조회에서 추가한 구간을 찾을 수 있다.
     */
    @Test
    @DisplayName("역 사이에 새로운 역 추가: 새로운 상행 종점역 추가")
    void addSection_success_atUpStation(){
        // give

        // when
        지하철_노선에_지하철_구간_생성_요청(이호선, createSectionCreateParams(교대역, 강남역,10));

        // then
        List<String> stationNames = 지하철_노선_조회_요청(이호선).jsonPath().getList("stations.name");
        assertThat(stationNames).contains("교대역");
    }

    /**
     * When 노선의 하행종착역을 상행역으로 하는 구간을 추가
     * Then 노선의 구간 목록 조회에서 추가한 구간을 찾을 수 있다.
     */
    @Test
    @DisplayName("역 사이에 새로운 역 추가: 새로운 하행 종점역 추가")
    void addSection_success_atDownStation(){
        // given

        // when
        지하철_노선에_지하철_구간_생성_요청(이호선, createSectionCreateParams(삼성역, 잠실역,10));

        // then
        List<String> stationNames = 지하철_노선_조회_요청(이호선).jsonPath().getList("stations.name");
        assertThat(stationNames).contains("잠실역");
    }

    /**
     * When
     * Then 노선의 구간 목록 조회에서 추가한 구간을 찾을 수 있다.
     */
    @Test
    @DisplayName("역 사이에 새로운 역 추가: 구간 중간에 구간 추가(상행역 기준)")
    void addSection_success_atMiddle1(){
        // given

        // when
        지하철_노선에_지하철_구간_생성_요청(이호선, createSectionCreateParams(역삼역, 선릉역,3));

        // then - 노선의 구간 목록 조회에서 추가한 구간을 찾을 수 있고, 기존 구간이 변경된 것을 확인할 수 있다.
        List<String> stationNames = 지하철_노선_조회_요청(이호선).jsonPath().getList("stations.name");
        assertThat(stationNames).contains("선릉역");
    }

    /**
     * When
     * Then 노선의 구간 목록 조회에서 추가한 구간을 찾을 수 있다.
     */
    @Test
    @DisplayName("역 사이에 새로운 역 추가: 구간 중간에 구간 추가(하행역 기준)")
    void addSection_success_atMiddle2(){
        // given

        // when
        지하철_노선에_지하철_구간_생성_요청(이호선, createSectionCreateParams(선릉역, 삼성역,3));

        // then - 노선의 구간 목록 조회에서 추가한 구간을 찾을 수 있고, 기존 구간이 변경된 것을 확인할 수 있다.
        List<String> stationNames = 지하철_노선_조회_요청(이호선).jsonPath().getList("stations.name");
        assertThat(stationNames).contains("선릉역");
    }

    @Test
    @DisplayName("구간 목록 조회: 구간 정보가 상행 - 하행 순으로 정렬되어 있어야 한다.")
    void findSections_withSortedSection(){
        // given

        // when - 노선의 구간 정보를 조회하면
        List<Long> stationIds = 지하철_노선_조회_요청(이호선).jsonPath().getList("stations.id", Long.class);

        // then - 구간 정보가 상행 <-> 하행 순으로 정렬되어 반환한다
        assertThat(stationIds).containsExactly(강남역, 역삼역, 삼성역);
    }


    private Map<String, String> createLineCreateParams(Long upStationId, Long downStationId) {
        Map<String, String> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", "2호선");
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

