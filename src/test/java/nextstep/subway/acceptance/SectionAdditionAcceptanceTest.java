package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.fake.FakeLineFactory;
import nextstep.subway.fake.FakeStationFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@DisplayName("지하철 구간 추가 인수 테스트")
public class SectionAdditionAcceptanceTest extends AcceptanceTest{

    private Station 강남역;
    private Station 구의역;
    private Station 역삼역;
    private Station 신촌역;
    private Station 선릉역;
    private Long 신분당선_ID;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        강남역 = StationSteps.지하철역_생성_요청(FakeStationFactory.강남역().getName()).as(Station.class);
        구의역 = StationSteps.지하철역_생성_요청(FakeStationFactory.구의역().getName()).as(Station.class);
        역삼역 = StationSteps.지하철역_생성_요청(FakeStationFactory.역삼역().getName()).as(Station.class);
        선릉역 = StationSteps.지하철역_생성_요청(FakeStationFactory.선릉역().getName()).as(Station.class);
        신촌역 = StationSteps.지하철역_생성_요청(FakeStationFactory.신촌역().getName()).as(Station.class);
        신분당선_ID = LineSteps.지하철_노선_생성_요청(
                FakeLineFactory.신분당선().getName(),
                FakeLineFactory.신분당선().getColor())
                           .jsonPath().getLong("id");
    }

    /* given 구간 하나를 지하철 노선에 추가한다.
     * when 하행 종점이 상행역인 구간을 노선에 추가한다.
     * then 구간을 조회하고 추가한 구간의 하행역이 노선의 마지막 역인지 확인한다.
     */
    @Test
    void 하행_종점에_추가() {
        //given
        Map<String, String> 강남_역삼_구간 = createAdditionalBody(강남역.getId(), 역삼역.getId(), 10);
        구간_추가_요청(신분당선_ID, 강남_역삼_구간);

        //when
        Map<String, String> 역삼_구의_구간 = createAdditionalBody(역삼역.getId(), 구의역.getId(), 10);
        구간_추가_요청(신분당선_ID, 역삼_구의_구간);

        //then
        ExtractableResponse<Response> response = LineSteps.지하철_노선_조회_요청(신분당선_ID);
        List<String> stationNames = response.jsonPath().getList("stations.name", String.class);
        assertThat(stationNames.lastIndexOf(구의역.getName())).isEqualTo( stationNames.size() - 1);
    }

    /*
     *
     */
    void 구간_사이에_추가() {

    }

    /*
     *
     */
    void 상행_종점에_추가() {

    }

    /* given
     * when
     * then
     */
    @DisplayName("구간 추가 실패 테스트 - 상행과 하행역이 모두 동일한 구간을 추가")
    void 상행과_하행역이_모두_동일할_경우() {

    }


    private Map<String, String> createAdditionalBody(Long upStationId, Long downStationId, Integer distance) {
        Map<String, String> sectionBody = new HashMap<>();
        sectionBody.put("upStationId", upStationId + "");
        sectionBody.put("downStationId", downStationId + "");
        sectionBody.put("distance", distance + "");
        return sectionBody;
    }

    private void 구간_추가_요청(Long id, Map<String, String> body) {
        LineSteps.지하철_노선에_지하철_구간_생성_요청(id, body);
    }

}
