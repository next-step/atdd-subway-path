package nextstep.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.applicaion.dto.request.PathRequest;
import nextstep.subway.applicaion.dto.request.SectionRequest;
import nextstep.subway.domain.Station;
import nextstep.subway.enums.SubwayErrorMessage;
import nextstep.subway.fake.FakeStationFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;



import static org.assertj.core.api.Assertions.assertThat;

public class PathAcceptanceTest extends AcceptanceTest{

    private long 분당선_ID;
    private long 이호선_ID;
    private long 경의중앙선_ID;
    private Station 강남역;
    private Station 선릉역;
    private Station 왕십리역;
    private Station 영통역;
    private Station 역삼역;
    SectionRequest 강남역_선릉역_거리2;
    SectionRequest 선릉역_왕십리역_거리10;
    SectionRequest 선릉역_왕십리역_거리5;
    SectionRequest 영통역_역삼역_거리5;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        분당선_ID = LineSteps.지하철_노선_생성_요청("분당선", "yellow").jsonPath().getLong("id");
        이호선_ID = LineSteps.지하철_노선_생성_요청("2호선", "green").jsonPath().getLong("id");
        이호선_ID = LineSteps.지하철_노선_생성_요청("경의중앙선", "sky").jsonPath().getLong("id");

        강남역 = StationSteps.지하철역_생성_요청("강남역").as(Station.class);
        선릉역 = StationSteps.지하철역_생성_요청("선릉역").as(Station.class);
        왕십리역 = StationSteps.지하철역_생성_요청("왕십리역").as(Station.class);
        영통역 = StationSteps.지하철역_생성_요청("영통역").as(Station.class);
        역삼역 = StationSteps.지하철역_생성_요청("역삼역").as(Station.class);

        강남역_선릉역_거리2 = createSectionDto(강남역.getId(), 선릉역.getId(), 2);
        선릉역_왕십리역_거리10 =  createSectionDto(선릉역.getId(), 왕십리역.getId(), 10);
        선릉역_왕십리역_거리5 = createSectionDto(선릉역.getId(), 왕십리역.getId(), 5);
        영통역_역삼역_거리5 = createSectionDto(영통역.getId(), 역삼역.getId(), 5);
    }

    /* given
     * 2호선에 선릉역_왕십리역_거리10, 강남역_선릉역_거리2 구간을 등록하고
     * 분당선에 선릉역_왕십리역_거리5 구간을 등록한다.
     * when
     * 강남역에서 왕십리역까지의 최단 경로를 조회한다.
     * then
     * 상태코드가 200인지 확인하고 응답받은 역의 리스트 순서가 강남역, 선릉역, 왕십리역인지 확인하고 거리가 7인지 확인한다.
     */
    @Test
    void 최단경로_조회() {
        //given
        LineSteps.지하철_노선에_지하철_구간_생성_요청(이호선_ID, 강남역_선릉역_거리2);
        LineSteps.지하철_노선에_지하철_구간_생성_요청(이호선_ID, 선릉역_왕십리역_거리10);
        LineSteps.지하철_노선에_지하철_구간_생성_요청(분당선_ID, 선릉역_왕십리역_거리5);

        //when
        PathRequest pathRequest = new PathRequest(강남역.getId(), 왕십리역.getId());
        ExtractableResponse<Response> 지하철_최단경로_조회_응답 = PathSteps.지하철_최단경로_조회(pathRequest);

        //then
        지하철_최단거리_조회_검증(지하철_최단경로_조회_응답);
    }

    /* given
     * 2호선에 선릉역_왕십리역_거리10, 강남역_선릉역_거리2 구간을 등록하고
     * 분당선에 선릉역_왕십리역_거리5 구간을 등록한다.
     * when
     * 강남역에서 강남역까지의 최단 경로를 조회한다.
     * then
     * 목적지와 출발지가 동일하기 떄문에 400 상태코드를 응답받고 Exception 메세지를 응답 받는다.
     */
    @Test
    void 출발역과_목적지가_동일할_경우() {
        //given
        LineSteps.지하철_노선에_지하철_구간_생성_요청(이호선_ID, 강남역_선릉역_거리2);
        LineSteps.지하철_노선에_지하철_구간_생성_요청(이호선_ID, 선릉역_왕십리역_거리10);
        LineSteps.지하철_노선에_지하철_구간_생성_요청(분당선_ID, 영통역_역삼역_거리5);

        //when
        PathRequest pathRequest = new PathRequest(강남역.getId(), 강남역.getId());
        ExtractableResponse<Response> 지하철_최단경로_조회_응답 = PathSteps.지하철_최단경로_조회(pathRequest);

        //then
        출발지와와_목적지가_동일할_경우_검증(지하철_최단경로_조회_응답);
    }

    /* given
     * 2호선에 선릉역_왕십리역_거리10, 강남역_선릉역_거리2 구간을 등록하고
     * 경의중앙선에 영통역_역삼역_거리5 구간을 등록한다.
     * when
     * 강남역에서 영통역까지의 최단 경로를 조회한다.
     * then
     * 목적지와 출발지가 연결되지 않았기 떄문에 400 상태코드를 응답받고 Exception 메세지를 응답 받는다.
     */
    @Test
    void 경로가_이어지지_않았을_경우() {
        //given
        LineSteps.지하철_노선에_지하철_구간_생성_요청(이호선_ID, 강남역_선릉역_거리2);
        LineSteps.지하철_노선에_지하철_구간_생성_요청(이호선_ID, 선릉역_왕십리역_거리10);
        LineSteps.지하철_노선에_지하철_구간_생성_요청(경의중앙선_ID, 영통역_역삼역_거리5);

        //when
        PathRequest pathRequest = new PathRequest(강남역.getId(), 영통역.getId());
        ExtractableResponse<Response> 지하철_최단경로_조회_응답 = PathSteps.지하철_최단경로_조회(pathRequest);

        //then
        경로가_이어지지_않았을_경우_검증(지하철_최단경로_조회_응답);
    }

    private void 출발지와와_목적지가_동일할_경우_검증(ExtractableResponse<Response> 지하철_최단경로_조회_응답) {
        assertThat(지하철_최단경로_조회_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(지하철_최단경로_조회_응답.jsonPath().getString("message"))
                .isEqualTo(SubwayErrorMessage.SAME_SOURCE_AND_DESTINATION.getMessage());
    }

    private void 경로가_이어지지_않았을_경우_검증(ExtractableResponse<Response> 지하철_최단경로_조회_응답) {
        assertThat(지하철_최단경로_조회_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(지하철_최단경로_조회_응답.jsonPath().getString("message"))
                .isEqualTo(SubwayErrorMessage.UNCONNECTED_PATH.getMessage());
    }

    private void 지하철_최단거리_조회_검증(ExtractableResponse<Response> 지하철_최단경로_조회_응답) {
        assertThat(지하철_최단경로_조회_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(지하철_최단경로_조회_응답.jsonPath().getList("stations.name")).containsExactly(
                FakeStationFactory.강남역().getName(),
                FakeStationFactory.선릉역().getName(),
                FakeStationFactory.왕십리역().getName());
        assertThat(지하철_최단경로_조회_응답.jsonPath().getInt("distance")).isEqualTo(7);
    }

    private SectionRequest createSectionDto(long upStationId, long downStationId, int distance) {
        return new SectionRequest( upStationId, downStationId, distance);
    }

}
