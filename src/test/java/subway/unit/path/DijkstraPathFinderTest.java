package subway.unit.path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.acceptance.station.StationFixture;
import subway.exception.SubwayBadRequestException;
import subway.line.model.Line;
import subway.line.model.Section;
import subway.path.component.DijkstraPathFinder;
import subway.path.component.PathFinder;
import subway.path.model.Path;
import subway.station.dto.StationResponse;
import subway.station.model.Station;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static subway.acceptance.station.StationFixture.getStation;

@DisplayName("DijkstraPathFinder 단위 테스트")
public class DijkstraPathFinderTest {
    private PathFinder pathFinder;

    private Line 이호선;
    private Line 삼호선;
    private Line 신분당선;
    private List<Section> 구간목록;

    /**
     * <pre>
     * 교대역  ---- *2호선* --- d:10 ------  강남역
     * |                                    |
     * *3호선*                            *신분당선*
     * d:2                                 d:10
     * |                                   |
     * 남부터미널역  --- *3호선* -- d:3 --- 양재역
     *
     * 건대역 ---- *A호선* --- d:7 ---- 성수역 ---- d:3 ---- 왕십리역
     * </pre>
     */

    @BeforeEach
    void beforeEach() {
        pathFinder = new DijkstraPathFinder();

        StationFixture.기본_역_생성();

        이호선 = Line.builder().name("2호선").color("random").build();
        Section 이호선_1구간 = Section.builder().upStation(getStation("교대역")).downStation(getStation("강남역")).distance(10L).build();
        이호선.addSection(이호선_1구간);

        삼호선 = Line.builder().name("2호선").color("random").build();
        Section 삼호선_1구간 = Section.builder().upStation(getStation("교대역")).downStation(getStation("남부터미널역")).distance(2L).build();
        삼호선.addSection(삼호선_1구간);
        Section 삼호선_2구간 = Section.builder().upStation(getStation("남부터미널역")).downStation(getStation("양재역")).distance(3L).build();
        삼호선.addSection(삼호선_2구간);

        신분당선 = Line.builder().name("2호선").color("random").build();
        Section 신분당선_1구간 = Section.builder().upStation(getStation("강남역")).downStation(getStation("양재역")).distance(10L).build();
        신분당선.addSection(신분당선_1구간);

        List<Line> lines = List.of(이호선, 삼호선, 신분당선);
        구간목록 = lines.stream()
                .flatMap(line -> line.getLineSections().getSections().stream())
                .collect(Collectors.toList());

    }


    /**
     * Given 구간이 있을 때
     * When 경로 조회를 하면
     * Then 경로 내 역이 목록으로 반환된다
     * Then 경로의 총 길이가 반환된다
     */
    @DisplayName("경로 조회")
    @Test
    void getShortestPath() {
        // when
        Path shortestPath = pathFinder.findPath(구간목록, getStation("교대역"), getStation("양재역"));

        // then
        assertThat(shortestPath.getStations())
                .extracting(StationResponse::getName)
                .containsExactlyInAnyOrder("교대역", "남부터미널역", "양재역");

        // then
        assertThat(shortestPath.getDistance()).isEqualTo(5L);
    }

    /**
     * Given 구간이 있을 때
     * When 동일한 구간을 조회하면
     * Then 조회 되지 않는다.
     */
    @DisplayName("경로 조회 : 요청 구간 동일")
    @Test
    void getShortestPathWithSameOrigin() {
        // when/then
        assertThatThrownBy(() -> pathFinder.findPath(구간목록, getStation("교대역"), getStation("교대역")))
                .isInstanceOf(SubwayBadRequestException.class);
    }

    /**
     * Given 구간이 있을 때
     * When 연결되지 않은 구간을 조회하면
     * Then 조회 되지 않는다.
     */
    @DisplayName("경로 조회 : 연결되지 않은 구간")
    @Test
    void getShortestPathNotConnectedSection() {
        // when/then
        assertThatThrownBy(() -> pathFinder.findPath(구간목록, getStation("교대역"), getStation("왕십리역")))
                .isInstanceOf(SubwayBadRequestException.class);
    }

    /**
     * Given 구간이 있을 때
     * When 존재하지 않는 역을 조회하면
     * Then 조회 되지 않는다.
     */
    @DisplayName("경로 조회 : 존재하지 않는 역")
    @Test
    void getShortestPathNotExistStation() {
        // when/then
        assertThatThrownBy(() -> pathFinder.findPath(구간목록, new Station(99L, "그런역"), new Station(98L, "저런역")))
                .isInstanceOf(SubwayBadRequestException.class);
    }

}
