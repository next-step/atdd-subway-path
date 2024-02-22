package nextstep.subway.unit;

import nextstep.subway.dto.path.PathResponse;
import nextstep.subway.dto.station.StationResponse;
import nextstep.subway.entity.Line;
import nextstep.subway.entity.Section;
import nextstep.subway.entity.Sections;
import nextstep.subway.entity.Station;
import nextstep.subway.service.PathFinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PathFinderTest {
    private PathFinder pathFinder;
    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;
    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;

    @BeforeEach
    void setUp() {
        교대역 = new Station(1L, "교대역");
        강남역 = new Station(2L, "강남역");
        양재역 = new Station(3L, "양재역");
        남부터미널역 = new Station(4L, "남부터미널역");
    }

    @DisplayName("출발역과 도착역을 통해 최단경로를 조회한다.")
    @Test
    void getShortestPath() {
        // given
        이호선 = new Line("2호선", "green");
        이호선.addSection(new Section(이호선, 교대역, 강남역, 10));

        신분당선 = new Line("신분당선", "red");
        신분당선.addSection(new Section(신분당선, 강남역, 양재역, 10));

        삼호선 = new Line("3호선", "orange");
        삼호선.addSection(new Section(삼호선, 교대역, 남부터미널역, 2));
        삼호선.addSection(new Section(삼호선, 남부터미널역, 양재역, 3));

        List<Sections> sectionsList = List.of(이호선, 신분당선, 삼호선).stream()
            .map(Line::getSections)
            .collect(Collectors.toList());

        // when
        pathFinder = new PathFinder(sectionsList);
        PathResponse 최단경로_조회_응답 = pathFinder.getShortestPath(교대역, 양재역);

        // then
        List<Long> 최단구간_역_목록 = 최단경로_조회_응답.getStations().stream()
            .map(StationResponse::getId)
            .collect(Collectors.toList());

        assertThat(최단구간_역_목록).containsExactly(교대역.getId(), 남부터미널역.getId(), 양재역.getId());
        assertThat(최단경로_조회_응답.getDistance()).isEqualTo(5);
    }

    @DisplayName("출발역과 도착역이 연결되지 않을 시 예외가 발생한다.")
    @Test
    void isNotConnectedStations() {
        // given
        이호선 = new Line("2호선", "green");
        이호선.addSection(new Section(이호선, 교대역, 강남역, 10));

        신분당선 = new Line("신분당선", "red");
        신분당선.addSection(new Section(신분당선, 남부터미널역, 양재역, 10));

        List<Sections> sectionsList = List.of(이호선, 신분당선).stream()
            .map(Line::getSections)
            .collect(Collectors.toList());

        pathFinder = new PathFinder(sectionsList);

        assertThatThrownBy(() -> pathFinder.getShortestPath(교대역, 양재역))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("출발역과 도착역이 연결되어 있어야 한다.");
    }
}
