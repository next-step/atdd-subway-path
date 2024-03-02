package nextstep.subway.acceptance.path;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.line.LineRepository;
import nextstep.subway.line.SectionRepository;
import nextstep.subway.station.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import static nextstep.subway.acceptance.fixture.LineFixture.addSection;
import static nextstep.subway.acceptance.fixture.LineFixture.newLineAndGetId;
import static nextstep.subway.acceptance.fixture.PathFixture.findPath;
import static nextstep.subway.acceptance.fixture.StationFixture.newStationAndGetId;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 경로 조회 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class PathAcceptanceTest {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private SectionRepository sectionRepository;

    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;

    /**
     * 교대역    -- 4 -- *2호선* ---   강남역
     * |                                    |
     * *3호선*                         *신분당선*
     * *2*                                 *5*
     * |                                    |
     * 남부터미널역  -- 3 -- *3호선* ---   양재
     */
    @BeforeEach
    public void setUp() {
        sectionRepository.deleteAllInBatch();
        lineRepository.deleteAllInBatch();
        stationRepository.deleteAllInBatch();

        교대역 = newStationAndGetId("교대역");
        강남역 = newStationAndGetId("강남역");
        양재역 = newStationAndGetId("양재역");
        남부터미널역 = newStationAndGetId("남부터미널역");

        이호선 = newLineAndGetId("2호선", "green", 교대역, 강남역, 4);
        신분당선 = newLineAndGetId("신분당선", "red", 강남역, 양재역, 5);
        삼호선 = newLineAndGetId("3호선", "orange", 교대역, 남부터미널역, 2);

        addSection(삼호선, 남부터미널역, 양재역, 3);
    }

    @DisplayName("지하철 경로 조회가 가능하다.")
    @Test
    void findPathTest() {
        // when
        ExtractableResponse<Response> response = findPath(교대역, 양재역);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().jsonPath().getList("stations").size()).isEqualTo(3);
        assertThat(response.body().jsonPath().getString("stations[0].name")).isEqualTo("교대역");
        assertThat(response.body().jsonPath().getString("stations[1].name")).isEqualTo("남부터미널역");
        assertThat(response.body().jsonPath().getString("stations[2].name")).isEqualTo("양재역");
        assertThat(response.body().jsonPath().getLong("distance")).isEqualTo(5);
    }
}
