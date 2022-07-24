package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.domain.Station;
import nextstep.subway.fake.FakeLineFactory;
import nextstep.subway.fake.FakeStationFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

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


    /* given 노선에 구간을 추가한다.
     * when 상행 종점역이 될 구간을 추가한다.
     * then 추가한 구간의 상행역이 구간 목록의 상행 종점과 일치한지 확인한다.
     */
    @Test
    void 상행_종점에_추가() {
        Map<String, String> 구의_선릉_구간 = createAdditionalBody(구의역.getId(), 선릉역.getId(), 10);
        구간_추가_요청(신분당선_ID, 구의_선릉_구간);

        //when
        Map<String, String> 역삼_구의_구간 = createAdditionalBody(역삼역.getId(), 구의역.getId(), 10);
        구간_추가_요청(신분당선_ID, 역삼_구의_구간);

        //then
        ExtractableResponse<Response> response = LineSteps.지하철_노선_조회_요청(신분당선_ID);
        List<String> stationNames = response.jsonPath().getList("stations.name", String.class);
        assertThat(stationNames).containsExactly(역삼역.getName(), 구의역.getName(), 선릉역.getName());
    }

    @Test
    void 중간에_구간_추가() {
        Map<String, String> 구의_선릉_구간 = createAdditionalBody(구의역.getId(), 선릉역.getId(), 10);
        구간_추가_요청(신분당선_ID, 구의_선릉_구간);

        //when
        Map<String, String> 구의_역삼_구간 = createAdditionalBody(구의역.getId(), 역삼역.getId(), 3);
        구간_추가_요청(신분당선_ID, 구의_역삼_구간);

        //then
        ExtractableResponse<Response> response = LineSteps.지하철_노선_조회_요청(신분당선_ID);
        List<String> stationNames = response.jsonPath().getList("stations.name", String.class);
        assertThat(stationNames).containsExactly(구의역.getName(), 역삼역.getName(), 선릉역.getName());
    }

    /* given 노선에 구간을 추가한다.
     * when, then
     * 상행역과 하행역이 모두 포함되지 않은 구간을 추가하면,
     * 구간 추가가 실패하여 상태코드 400과 지정된 메세지를 응답 받는다.
     */
    @Test
    void 상행역과_하행역이_포함되지_않았을_경우() {
        //given
        Map<String, String> 강남_역삼_구간 = createAdditionalBody(강남역.getId(), 역삼역.getId(), 4);
        구간_추가_요청(신분당선_ID, 강남_역삼_구간);

        //then
        Map<String, String> 신촌_선릉_구간 = createAdditionalBody(신촌역.getId(), 선릉역.getId(), 5);
        ExtractableResponse<Response> 신촌_선릉_구간_추가_요청 = 구간_추가_요청(신분당선_ID, 신촌_선릉_구간);
        assertAll(
                () -> assertThat(신촌_선릉_구간_추가_요청.statusCode())
                        .isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(신촌_선릉_구간_추가_요청.jsonPath().getString("message"))
                        .isEqualTo("상행역이나 하행역 중 하나는 등록된 구간에 포함되어야 합니다.")
        );
    }

    /* given 노선에 구간을 추가한다.
     * when, then
     * 기존 구간보다 거리가 긴 구간을 중간에 추가하면,
     * 구간 추가가 실패하고 상태코드 400과 지정된 메세지를 응답 받는다.
     */
    @Test
    void 구간_추가_실패_길이가_긴_경우() {
        //given
        Map<String, String> 강남_역삼_구간 = createAdditionalBody(강남역.getId(), 역삼역.getId(), 10);
        구간_추가_요청(신분당선_ID, 강남_역삼_구간);

        //then
        Map<String, String> 강남_구의_구간 = createAdditionalBody(강남역.getId(), 구의역.getId(), 11);
        ExtractableResponse<Response> 구간_추가_요청_응답 = 구간_추가_요청(신분당선_ID, 강남_구의_구간);
        assertAll(
                () -> assertThat(구간_추가_요청_응답.statusCode())
                        .isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(구간_추가_요청_응답.jsonPath().getString("message"))
                        .isEqualTo("역 사이에 등록할 경우 기존의 거리보다 짧은 구간만 등록이 가능합니다.")
        );
    }

    /* given 노선에 구간을 추가한다.
     * when, then
     * 이미 추가된 구간과 동일한 구간을 추가하면,
     * 구간 추가가 실패하여 상태코드 400과 지정된 메세지를 응답 받는다.
     */
    @Test
    void 상행과_하행역이_모두_동일할_경우() {
        //given
        Map<String, String> 강남_역삼_구간 = createAdditionalBody(강남역.getId(), 역삼역.getId(), 10);
        구간_추가_요청(신분당선_ID, 강남_역삼_구간);

        //then
        ExtractableResponse<Response> 구간_추가_요청_응답 = 구간_추가_요청(신분당선_ID, 강남_역삼_구간);
        assertAll(
                () -> assertThat(구간_추가_요청_응답.statusCode())
                        .isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(구간_추가_요청_응답.jsonPath().getString("message"))
                        .isEqualTo("상행역과 하행역이 모두 등록 되어있는 구간은 등록할 수 없습니다.")
        );
    }


    private Map<String, String> createAdditionalBody(Long upStationId, Long downStationId, Integer distance) {
        Map<String, String> sectionBody = new HashMap<>();
        sectionBody.put("upStationId", upStationId + "");
        sectionBody.put("downStationId", downStationId + "");
        sectionBody.put("distance", distance + "");
        return sectionBody;
    }

    private ExtractableResponse<Response> 구간_추가_요청(Long id, Map<String, String> body) {
        return LineSteps.지하철_노선에_지하철_구간_생성_요청(id, body);
    }

}
