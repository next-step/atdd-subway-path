package subway.acceptance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import subway.line.LineRequest;
import subway.line.LineResponse;
import subway.path.PathResponse;
import subway.section.SectionRequest;
import subway.station.Station;
import subway.station.StationResponse;
import util.RestAssuredUtil;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(value = "/table_truncate.sql")
@DisplayName("지하철 경로 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
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
    void setUp() {
        교대역 = RestAssuredUtil.post(new Station(1L, "교대역"), "stations")
                .as(StationResponse.class).getId();
        강남역 = RestAssuredUtil.post(new Station(2L, "강남역"), "stations")
                .as(StationResponse.class).getId();
        양재역 = RestAssuredUtil.post(new Station(3L, "양재역"), "stations")
                .as(StationResponse.class).getId();
        남부터미널역 = RestAssuredUtil.post(new Station(4L, "남부터미널역"), "stations")
                .as(StationResponse.class).getId();

        이호선 = RestAssuredUtil.post(new LineRequest(1L, "2호선", "green", 10L, 교대역, 강남역), "/lines")
                .as(LineResponse.class).getId();
        신분당선 = RestAssuredUtil.post(new LineRequest(2L, "신분당선", "red", 10L, 강남역, 양재역), "/lines")
                .as(LineResponse.class).getId();
        삼호선 = RestAssuredUtil.post(new LineRequest(3L, "3호선", "orange", 2L, 교대역, 남부터미널역), "/lines")
                .as(LineResponse.class).getId();
        RestAssuredUtil.post(new SectionRequest(남부터미널역, 양재역, 3L), "/lines/" + 삼호선 + "/sections");
    }

    /**
     * Given 출발역과 도착역이 주어질 때,
     * When 지하철 경로를 조회하면
     * Then 출발역과 도착역 사이의 역 목록과 거리를 조회할 수 있다.
     */
    @DisplayName("지하철 경로를 조회하면 출발역과 도착역 사이의 역 목록과 거리를 조회할 수 있다.")
    @Test
    void 두_역_사이의_지하철_경로를_조회하면_출발역과_도착역_사이_역_목록과_거리를_조회할_수_있다() {
        //given
        Long source = 교대역;
        Long target = 양재역;

        //when
        PathResponse pathRes
                = RestAssuredUtil.get("/paths?source=" + source + "&target=" + target)
                .as(PathResponse.class);

        //then
        assertThat(pathRes.getStations()).hasSize(3);
        assertThat(pathRes.getStations().get(0).getName()).isEqualTo("교대역");
        assertThat(pathRes.getStations().get(1).getName()).isEqualTo("남부터미널역");
        assertThat(pathRes.getStations().get(2).getName()).isEqualTo("양재역");
        assertThat(pathRes.getDistance()).isEqualTo(5L);
    }
}
