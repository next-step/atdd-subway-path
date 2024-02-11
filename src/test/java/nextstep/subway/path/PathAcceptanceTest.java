package nextstep.subway.path;

import nextstep.subway.testhelper.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지하철 경로 검색")
public class PathAcceptanceTest extends AcceptanceTest {

    private Long 잠실역_ID;
    private Long 강남역_ID;
    private Long 삼성역_ID;
    private Long 선릉역_ID;
    private Long 일호선_잠실역_부터_강남역_ID;
    private Long 이호선_잠실역_부터_삼성역_ID;
    private Long 삼호선_강남역_부터_선릉역_ID;

    @BeforeEach
    public void setUp() {
        super.setUp();
        StationFixture stationFixture = new StationFixture();
        잠실역_ID = stationFixture.get잠실역_ID();
        강남역_ID = stationFixture.get강남역_ID();
        삼성역_ID = stationFixture.get삼성역_ID();
        선릉역_ID = stationFixture.get선릉역_ID();

        LineFixture lineFixture = new LineFixture(stationFixture);
        일호선_잠실역_부터_강남역_ID = lineFixture.get일호선_잠실역_부터_강남역_ID();
        이호선_잠실역_부터_삼성역_ID = lineFixture.get이호선_잠실역_부터_삼성역_ID();
        삼호선_강남역_부터_선릉역_ID = lineFixture.get삼호선_강남역_부터_선릉역_ID();
        SectionFixture sectionFixture = new SectionFixture(stationFixture);
        LineApiCaller.지하철_노선에_구간_추가(sectionFixture.get선릉역_부터_삼성역_구간_params(), "/lines/" + 삼호선_강남역_부터_선릉역_ID.toString());
    }

    @Test
    void test() {
    }

}
