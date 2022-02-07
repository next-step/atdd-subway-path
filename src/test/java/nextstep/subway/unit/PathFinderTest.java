package nextstep.subway.unit;

import com.google.common.collect.Lists;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("지하철 경로 검색")
class PathFinderTest {
    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;
    private Station 청계산입구역;
    private Station 안산역;
    private Station 오이도역;
    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;
    private Line 사호선;
    private Map<Long, Station> stationMap = new HashMap<>();

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     * |
     * 청계산입구역
     */
    @BeforeEach
    public void setUp() {
        교대역 = new Station(1, "교대역");
        강남역 = new Station(2, "강남역");
        양재역 = new Station(3, "양재역");
        남부터미널역 = new Station(4, "남부터미널역");
        청계산입구역 = new Station(5, "청계산입구역");
        안산역 = new Station(10, "안산역");
        오이도역 = new Station(11, "오이도역");

        이호선 = new Line("2호선", "red");
        신분당선 = new Line("신분당선", "red");
        삼호선 = new Line("3호선", "red");
        사호선 = new Line("4호선", "blue");

        이호선.addSection(교대역, 강남역, 10);
        신분당선.addSection(강남역, 양재역, 10);
        신분당선.addSection(양재역, 청계산입구역, 7);
        삼호선.addSection(교대역, 남부터미널역, 5);
        삼호선.addSection(남부터미널역, 양재역, 4);
        사호선.addSection(안산역, 오이도역, 100);

        stationMap.put(교대역.getId(), 교대역);
        stationMap.put(강남역.getId(), 강남역);
        stationMap.put(양재역.getId(), 양재역);
        stationMap.put(남부터미널역.getId(), 남부터미널역);
        stationMap.put(청계산입구역.getId(), 청계산입구역);
        stationMap.put(안산역.getId(), 안산역);
        stationMap.put(오이도역.getId(), 오이도역);
    }

    @DisplayName("최단 경로를 조회")
    @Test
    public void getPathsStation() {
        // given
        List<Line> lines = Lists.newArrayList(신분당선, 이호선, 삼호선);

        // when
        PathResponse response = PathFinder.create(lines, stationMap).findPath(교대역.getId(), 청계산입구역.getId());

        // then
        List<Long> expected = Lists.newArrayList(교대역.getId(), 남부터미널역.getId(), 양재역.getId(), 청계산입구역.getId());
        assertThat(response.getStations()).hasSize(4)
                .extracting(StationResponse::getId)
                .containsExactlyElementsOf(expected);
    }

    @DisplayName("출발역과 도착역이 같은 경우")
    @Test
    public void getPathsStationError1() {
        // given
        List<Line> lines = Lists.newArrayList(신분당선, 이호선, 삼호선);

        // when
        assertThatThrownBy(() -> PathFinder.create(lines, stationMap).findPath(교대역.getId(), 교대역.getId()))
                .isInstanceOf(RuntimeException.class);
    }

    @DisplayName("존재하지 않은 출발역이나 도착역을 조회 할 경우")
    @Test
    public void getPathsStationError2() {
        // given
        List<Line> lines = Lists.newArrayList(신분당선, 이호선, 삼호선);

        // when
        assertThatThrownBy(() -> PathFinder.create(lines, stationMap).findPath(10L, 교대역.getId()))
                .isInstanceOf(RuntimeException.class);
    }

    @DisplayName("출발역과 도착역이 연결이 되어 있지 않은 경우")
    @Test
    public void getPathsStationError3() {
        // given
        List<Line> lines = Lists.newArrayList(신분당선, 이호선, 삼호선);

        // when
        assertThatThrownBy(() -> PathFinder.create(lines, stationMap).findPath(교대역.getId(), 오이도역.getId()))
                .isInstanceOf(RuntimeException.class);
    }
}
