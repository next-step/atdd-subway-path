package nextstep.subway.domain;

import nextstep.subway.exception.EndStationsAreNotLinkedException;
import nextstep.subway.exception.PathSameEndStationException;
import nextstep.subway.exception.StationIsNotRegisteredException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

public class PathTest {
    private Line 삼호선;
    private Line 이호선;
    private Line 신분당선;

    private Station 남부터미널역;
    private Station 교대역;
    private Station 강남역;
    private Station 양재역;

    @BeforeEach
    void setUp() {
        삼호선 = new Line("3호선", "bg-orange-500");
        이호선 = new Line("2호선", "bg-green-500");
        신분당선 = new Line("신분당선", "bg-red-500");

        남부터미널역 = new Station(1L, "남부터미널역");
        교대역 = new Station(2L, "교대역");
        강남역 = new Station(3L, "강남역");
        양재역 = new Station(4L, "양재역");

        삼호선.addSection(new Section(삼호선, 교대역, 남부터미널역, 5));
        삼호선.addSection(new Section(삼호선, 남부터미널역, 양재역, 5));
        이호선.addSection(new Section(이호선, 교대역, 강남역, 3));
        신분당선.addSection(new Section(신분당선, 강남역, 양재역, 10));
    }

    /**
     * (3)
     * 교대역(2)   --- *2호선* ---       강남역(3)
     * |                                |
     * *3호선* (5)                  *신분당선* (10)
     * |                  (5)           |
     * 남부터미널역(1) --- *3호선* --- 양재역(4)
     */
    @Test
    public void findShortestPath() {
        // given
        Path path = new Path(List.of(삼호선, 이호선, 신분당선));

        // when
        ShortestPath shortestPath = path.findShortestPath(강남역.getId(), 남부터미널역.getId());
        var expectedWeight = 8;
        // then
        assertAll(
                () -> assertThat(shortestPath.getWeight()).isEqualTo(expectedWeight),
                () -> assertThat(shortestPath.getVertexList()).containsExactly(강남역.getId(), 교대역.getId(), 남부터미널역.getId())
        );
    }

    @DisplayName("출발역과 도착역이 같은 경우")
    @Test
    public void findShortestPathThrowsSameEndStationException() {
        // given
        Path path = new Path(List.of(삼호선, 이호선, 신분당선));

        // when
        // then
        assertThrowsExactly(PathSameEndStationException.class, () -> path.findShortestPath(강남역.getId(), 강남역.getId()));
    }

    @DisplayName("출발역이나 도착역이 등록되어있지 않은 경우")
    @Test
    public void findShortestPathThrowsStationNotFoundException() {
        // given
        Station 서울대입구역 = new Station(5L, "서울대입구역");

        // when
        Path path = new Path(List.of(삼호선, 이호선, 신분당선));

        // then
        assertThrowsExactly(StationIsNotRegisteredException.class, () -> path.findShortestPath(강남역.getId(), 서울대입구역.getId()));
    }

    @DisplayName("출발역과 도착역이 연결이 되어 있지 않은 경우")
    @Test
    public void findShortestPathThrowsStationIsNotLinkedException() {
        // given
        Line 사호선 = new Line("4호선", "bg-blue-500");
        Station 사당역 = new Station(6L, "사당역");
        Station 회현역 = new Station(7L, "회현역");
        사호선.addSection(new Section(사호선, 사당역, 회현역, 5));

        // when
        Path path = new Path(List.of(삼호선, 이호선, 신분당선, 사호선));

        // then
        assertThrowsExactly(EndStationsAreNotLinkedException.class, () -> path.findShortestPath(강남역.getId(), 사당역.getId()));
    }
}
