package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.util.PathFinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class PathTest {

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;
    private Station 흑석역;
    private Station 동작역;

    private Long 교대강남구간거리;
    private Long 강남양재구간거리;
    private Long 양재남부터미널구간거리;
    private Long 남부터미널교대구간거리;
    private Long 흑석동작구간거리;

    private Line 이호선;
    private Line 삼호선;
    private Line 신분당선;
    private Line 구호선;

    private Section 교대강남구간;
    private Section 강남양재구간;
    private Section 양재남부터미널구간;
    private Section 남부터미널교대구간;
    private Section 흑석동작구간;
    private PathFinder pathFinder;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*   //    흑석역  --- *9호선* ---   동작역
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */
    @BeforeEach
    public void setGivenData() {
        //given
        교대역 = Station.builder()
                .id(1L)
                .name("교대역")
                .build();
        강남역 = Station.builder()
                .id(2L)
                .name("강남역")
                .build();
        양재역 = Station.builder()
                .id(3L)
                .name("양재역")
                .build();
        남부터미널역 = Station.builder()
                .id(4L)
                .name("남부터미널역")
                .build();
        흑석역 = Station.builder()
                .id(5L)
                .name("흑석역")
                .build();
        동작역 = Station.builder()
                .id(6L)
                .name("동작역")
                .build();

        이호선 = new Line("이호선","Green");
        삼호선 = new Line("삼호선","Orange");
        신분당선 = new Line("신분당선","Red");
        구호선 = new Line("구호선","Gold");

        교대강남구간거리 = 10L;
        강남양재구간거리 = 15L;
        양재남부터미널구간거리 = 5L;
        남부터미널교대구간거리 = 5L;
        흑석동작구간거리 = 10L;

        교대강남구간 = new Section(이호선, 교대역, 강남역, 교대강남구간거리);
        강남양재구간 = new Section(이호선, 강남역, 양재역, 강남양재구간거리);
        양재남부터미널구간 = new Section(이호선, 양재역, 남부터미널역, 양재남부터미널구간거리);
        남부터미널교대구간 = new Section(이호선, 남부터미널역, 교대역, 남부터미널교대구간거리);
        흑석동작구간 = new Section(이호선, 흑석역, 동작역, 흑석동작구간거리);

        이호선.addSection(교대강남구간);
        신분당선.addSection(강남양재구간);
        삼호선.addSection(양재남부터미널구간);
        삼호선.addSection(남부터미널교대구간);
        구호선.addSection(흑석동작구간);

        pathFinder = new PathFinder();
        pathFinder.init(List.of(이호선,삼호선,신분당선));
    }

    @DisplayName("지하철 경로 조회")
    @Test
    void getPath() {
        // when
        List<Station> shortestPath = pathFinder.getPath(교대역, 양재역);

        // then
        assertThat(shortestPath.stream().map(Station::getName))
                .containsExactly("교대역", "남부터미널역", "양재역");
    }

    @DisplayName("지하철 경로 총 거리 조회")
    @Test
    void getDistance() {
        // when
        double distance= pathFinder.getDistance(교대역, 양재역);

        // then
        assertThat(distance).isEqualTo(남부터미널교대구간거리+양재남부터미널구간거리);
    }

    @DisplayName("출발역과 도착역이 동일한 경우 조회")
    @Test
    void getPath_source_and_target_is_identical() {
        // when, then
        assertThatThrownBy(() ->  pathFinder.getDistance(교대역, 교대역))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("경로조회는 출발역과 도착역이 동일할 수 없음.");
    }

    @DisplayName("출발역과 도착역이 연결이 되어 있지 않은 경우를 조회")
    @Test
    void getPath_not_connected() {
        // when, then
        assertThatThrownBy(() ->  pathFinder.getDistance(교대역, 동작역))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("출발역과 도착역이 연결되어 있지 않음.");
    }






}
