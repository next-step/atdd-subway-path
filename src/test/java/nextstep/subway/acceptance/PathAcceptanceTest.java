package nextstep.subway.acceptance;

import nextstep.subway.domain.request.LineRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static nextstep.subway.utils.LineTestUtil.createSubwayLine;
import static nextstep.subway.utils.SectionTestUtil.addSection;
import static nextstep.subway.utils.SectionTestUtil.createSectionParams;
import static nextstep.subway.utils.StationTestUtil.createStation;

@DisplayName("지하철 경로 검색")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Sql("/truncate.sql")
public class PathAcceptanceTest {
    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */
    @BeforeEach
    public void setUp() {
        교대역 = createStation("교대역").jsonPath().getLong("id");
        강남역 = createStation("강남역").jsonPath().getLong("id");
        양재역 = createStation("양재역").jsonPath().getLong("id");
        남부터미널역 = createStation("남부터미널역").jsonPath().getLong("id");

        이호선 = createSubwayLine(new LineRequest("2호선", "green", 교대역, 강남역, 10)).jsonPath().getLong("id");
        신분당선 = createSubwayLine(new LineRequest("신분당선", "red", 강남역, 양재역, 10)).jsonPath().getLong("id");
        삼호선 = createSubwayLine(new LineRequest("3호선", "orange", 교대역, 남부터미널역, 2)).jsonPath().getLong("id");

        addSection(createSectionParams(남부터미널역, 양재역, 3), 삼호선);
    }
}
