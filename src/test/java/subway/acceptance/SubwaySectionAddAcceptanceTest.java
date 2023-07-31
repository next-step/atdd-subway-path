package subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import subway.acceptance.apiTester.SubwayLineAcceptanceTest;
import subway.acceptance.apiTester.SubwaySectionAddApiTester;
import subway.acceptance.utils.AcceptanceTest;
import subway.application.response.StationResponse;
import subway.application.response.SubwayLineResponse;

/**
 * 지하철 노선 추가 인수 테스트를 합니다.
 */
@AcceptanceTest
@DisplayName("지하철 노선 구간 추가 인수 테스트")
public class SubwaySectionAddAcceptanceTest extends SubwayLineAcceptanceTest {

    @Autowired private SubwaySectionAddApiTester addApiTester;

    private StationResponse 서울대입구역;
    private StationResponse 강남역;
    private StationResponse 성수역;
    private StationResponse 신림역;

    @BeforeEach
    protected void setUp() {
        서울대입구역 = 지하철_역_생성("서울대입구역").as(StationResponse.class);
        강남역 = 지하철_역_생성("강남역").as(StationResponse.class);
        성수역 = 지하철_역_생성("성수역").as(StationResponse.class);
        신림역 = 지하철_역_생성("신림역").as(StationResponse.class);

    }

    /**
     * @given 지하철 노선이 생성 되어있다면
     * @when 지하철 노선 종점역에 구간을 추가할 때
     * @then 지하철 노션 상세 조회에서 추가된 구간을 확인할 수 있다.
     */
    @Test
    @DisplayName("노선 종점에 구간을 추가할 때 상세 조회 시에 추가된 구간을 확인할 수 있다.")
    void addSubwayLineSection() {
        //given
        SubwayLineResponse 이호선 = 지하철_노선_생성("이호선", "red", 서울대입구역, 강남역, 5).as(SubwayLineResponse.class);

        //when
        addApiTester.노선에_구간을_추가한다(이호선, 강남역, 성수역, 5);

        //then
        지하철_노선_구간_확인(이호선, 서울대입구역, 강남역, 성수역);
    }

    /**
     * @given 지하철 노선이 생성 되어있고
     * @when 기존 구간 중간에 구간을 추가할 때
     * @then 지하철 노션 상세 조회에서 추가된 구간을 확인할 수 있다.
     */
    @Test
    @DisplayName("구간 중간에 중간을 추가할 때 기존 구간이 추가될 구간을 제외한 구간으로 축소되고 상세 조회에서 확인이 가능하다.")
    void addSubwayLineSectionAtMiddle() {
        //given
        SubwayLineResponse 이호선 = 지하철_노선_생성("이호선", "red", 서울대입구역, 강남역, 5).as(SubwayLineResponse.class);

        //when
        ExtractableResponse<Response> response = addApiTester.노선에_구간을_추가한다(이호선, 신림역, 강남역, 3);

        //then
        지하철_노선_구간_확인(이호선, 서울대입구역, 강남역, 신림역);

    }

    /**
     * @given 지하철 노선이 생성 되어있다면
     * @when 지하철 노선 기점역에 구간을 추가할 때
     * @then 지하철 노선 기점역이 수정된다.
     * @then 지하철 노션 상세 조회에서 추가된 구간을 확인할 수 있다.
     */
    @Test
    @DisplayName("추가할 구간의 하행역이 이미 노선에 존재한다면 에러가 발생한다")
    void throwWhenExistsDownStation() {
        //given
        SubwayLineResponse 이호선 = 지하철_노선_생성("이호선", "red", 서울대입구역, 강남역, 5).as(SubwayLineResponse.class);

        //when
        ExtractableResponse<Response> response = addApiTester.노선에_구간을_추가한다(이호선, 강남역, 서울대입구역, 5);

        //then
        addApiTester.추가하는_구간의_하행역이_이미_노선에_존재한다면_에러_발생(response, 이호선, 서울대입구역);

    }


    /**
     * @given 지하철 노선이 생성 되어있다면
     * @when 추가될 구간이 기존 구간과 이어지지 않는다면
     * @then 에러 메세지를 뱉는다.
     */
    @Test
    @DisplayName("추가될 구간이 기존 구간과 이어지지 않는다면 추가가 불가능하다.")
    void throwWhenNotDownStation() {
        //given
        SubwayLineResponse 이호선 = 지하철_노선_생성("이호선", "red", 서울대입구역, 강남역, 5).as(SubwayLineResponse.class);

        //when
        ExtractableResponse<Response> response = addApiTester.노선에_구간을_추가한다(이호선, 신림역, 성수역, 5);

        //then
        addApiTester.추가하는_구간의_상행역이_하행_종점역이_아니면_에러_발생(response, 이호선, 신림역);
    }
}
