package nextstep.step2.acceptance;

import static nextstep.subway.acceptance.LineSteps.*;

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

@DisplayName("구간 관리 - 추가 인수 테스트")
public class SectionAcceptanceTest extends AcceptanceTest {

    private Long 이호선;
    private Long 강남역;
    private Long 역삼역;
    private Long 선릉역;


    /** Given 지하철역과 노선 생성을 요청 하고
     *  이호선 > 강남역 - 역삼역 - 선릉역
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
        역삼역 = 지하철역_생성_요청("역삼역").jsonPath().getLong("id");
        선릉역 = 지하철역_생성_요청("선릉역").jsonPath().getLong("id");

        Map<String, String> lineCreateParams = createLineCreateParams(강남역, 역삼역);
        이호선 = 지하철_노선_생성_요청(lineCreateParams).jsonPath().getLong("id");
        지하철_노선에_지하철_구간_생성_요청(이호선, createSectionCreateParams(역삼역, 선릉역, 10));
    }

    /** When 노선의 상행 종점역을 삭제하면  (강남역 - 역삼역 - 선릉역)
     *  Then 상행 종점역이 상행 종점 구간의 하행역으로 바뀐다. (역삼역 - 선릉역)
     */
    @Test
    @DisplayName("구간 삭제: 상행 종점역")
    void deleteSection_success_upStation() {
        // give

        // when
        지하철_노선에_지하철_구간_제거_요청(이호선, 강남역);

        // then
        List<Long> stationIds = 지하철_노선_조회_요청(이호선).jsonPath().getList("stations.id", Long.class);
        assertThat(stationIds).containsExactly(역삼역, 선릉역);

    }

    /** When 노선의 하행 종점역을 삭제하면 (강남역 - 역삼역 - 선릉역)
     *  Then 노선의 하행 종점역이 하행 종점 구간의 상행역으로 바뀐다. (강남역 - 역삼역)
     */
    @Test
    @DisplayName("구간 삭제: 하행 종점역")
    void deleteSection_success_donwStation() {
        // given

        // when
        지하철_노선에_지하철_구간_제거_요청(이호선, 선릉역);

        // then
        List<Long> stationIds = 지하철_노선_조회_요청(이호선).jsonPath().getList("stations.id", Long.class);
        assertThat(stationIds).containsExactly(강남역, 역삼역);
    }

    /** When 상/하행 종점역이 아닌 역을 삭제하면 (강남역 - 역삼역 - 선릉역)
     *  Then 목록에서 해당 역이 제거된다. (강남역 - 선릉역)
     */
    @Test
    @DisplayName("구간 삭제: 상/하행 종점역이 아닌 역")
    void deleteSection_success_middleStation() {
        // given

        // when
        지하철_노선에_지하철_구간_제거_요청(이호선, 역삼역);

        // then
        List<Long> stationIds = 지하철_노선_조회_요청(이호선).jsonPath().getList("stations.id", Long.class);
        assertThat(stationIds).containsExactly(강남역, 선릉역);
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

    private Map<String, String> createSectionCreateParams(
            Long upStationId, Long downStationId, Integer distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");
        return params;
    }
}
