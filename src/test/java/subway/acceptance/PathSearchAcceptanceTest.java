package subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import subway.acceptance.apiTester.PathSearchApiTester;
import subway.acceptance.apiTester.SubwayLineAcceptanceTest;
import subway.acceptance.apiTester.SubwaySectionAddApiTester;
import subway.acceptance.utils.AcceptanceTest;
import subway.application.response.StationResponse;
import subway.application.response.SubwayLineResponse;

/**
 * 지하철 경로 탐색 인수 테스트를 합니다.
 */
@AcceptanceTest
@DisplayName("지하철 경로 탐색 인수 테스트")
public class PathSearchAcceptanceTest extends SubwayLineAcceptanceTest {

    @Autowired private PathSearchApiTester pathSearchApiTester;
    @Autowired private SubwaySectionAddApiTester sectionAddApiTester;

    private StationResponse 서울대입구역;
    private StationResponse 강남역;
    private StationResponse 성수역;
    private StationResponse 신림역;
    private SubwayLineResponse 이호선;
    private SubwayLineResponse 사호선;

    @BeforeEach
    protected void setUp() {
        서울대입구역 = 지하철_역_생성("서울대입구역").as(StationResponse.class);
        강남역 = 지하철_역_생성("강남역").as(StationResponse.class);
        성수역 = 지하철_역_생성("성수역").as(StationResponse.class);
        신림역 = 지하철_역_생성("신림역").as(StationResponse.class);
    }

    /**
     * @given A-B 구간을 갖고 있는 AB 노선이 존재하고
     * @given B-C-D 구간을 갖고 있는 BCD 노선이 존재한다고 한다면
     * @when 같은 B역을 기준으로 경로탐색을 할 때
     * @then 에러 메시지를 출력한다.
     */
    @Test
    @DisplayName("같은 역을 기준으로 경로탐색을 할 수 없다.")
    public void cantSearchPathBetweenSameStation() {
        //given
        이호선 = 지하철_노선_생성("2호선", "green", 서울대입구역, 강남역, 5).as(SubwayLineResponse.class);

        //given
        사호선 = 지하철_노선_생성("4호선", "blue", 강남역, 성수역, 5).as(SubwayLineResponse.class);
        sectionAddApiTester.노선에_구간을_추가한다(사호선, 성수역, 신림역, 8);

        //when
        ExtractableResponse<Response> 경로_조회_요청 = pathSearchApiTester.경로_조회_요청(서울대입구역, 서울대입구역);

        //then
        pathSearchApiTester.경로_조회_실패됨(경로_조회_요청);
    }


    /**
     * @given A-B 구간을 5로 거리로 갖고 있는 AE 노선이 존재하고
     * @given C-D 구간을 12로 거리고 갖고 있는 BCD 노선이 존재한다고 한다면
     * @when 서로 이어지지 않은 A역과 D역을 기준으로 경로탐색을 할 때
     * @then 에러 메시지를 출력한다.
     */
    @Test
    @DisplayName("서로 이어지지 않은 두 역을 기준으로 경로탐색을 할 수 없다.")
    public void cantSearchPathBetweenNotConnectedStations() {
        //given
        이호선 = 지하철_노선_생성("2호선", "green", 서울대입구역, 강남역, 5).as(SubwayLineResponse.class);

        //given
        사호선 = 지하철_노선_생성("4호선", "blue", 성수역, 신림역, 12).as(SubwayLineResponse.class);

        //when
        ExtractableResponse<Response> 경로_조회_요청 = pathSearchApiTester.경로_조회_요청(서울대입구역, 신림역);

        //then
        pathSearchApiTester.경로_조회_실패됨(경로_조회_요청);
    }


    /**
     * @given A-B-C 구간을 갖는 ABC 노선이 존재하고
     * @given B-C-D 구간을 갖는  BCD 노선이 존재한다고 한다면
     * @when A역과 C역을 기준으로 경로탐색을 할 때
     * @then 최단 거리로 갈 수 있다.
     */
    @Test
    @DisplayName("두 역을 기준으로 경로탐색을 하면 최단 거리로 갈 수 있다.")
    public void searchPathBetweenDuplicatedSection() {
        //given
        이호선 = 지하철_노선_생성("2호선", "green", 서울대입구역, 강남역, 5).as(SubwayLineResponse.class);
        sectionAddApiTester.노선에_구간을_추가한다(이호선, 강남역, 신림역, 12);

        //given
        사호선 = 지하철_노선_생성("4호선", "blue", 강남역, 신림역, 8).as(SubwayLineResponse.class);
        sectionAddApiTester.노선에_구간을_추가한다(사호선, 신림역, 성수역, 12);

        //when
        ExtractableResponse<Response> 경로_조회_요청 = pathSearchApiTester.경로_조회_요청(서울대입구역, 신림역);

        //then
        pathSearchApiTester.경로_조회_응답됨(경로_조회_요청, 13, 서울대입구역, 강남역, 신림역);
    }
}
