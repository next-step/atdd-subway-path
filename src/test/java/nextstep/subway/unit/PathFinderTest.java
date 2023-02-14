package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.IdenticalSourceTargetNotAllowedException;

class PathFinderTest {

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;
    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;

    private PathFinder pathFinder;

    /**
     * 교대                         강남
     *  ● ────────── <2> ────────── ●
     *  └───────┐                   │
     *         <3>                  │
     *          └─────●─────┐    <신분당>
     *            남부터미널  │       │
     *                     <3>      │
     *                      └────── ●
     *                             양재
     */
    @BeforeEach
    void setUp() {
        교대역 = new Station("교대역");
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        남부터미널역 = new Station("남부터미널역");

        이호선 = new Line("2호선", "green");
        신분당선 = new Line("신분당선", "red");
        삼호선 = new Line("삼호선", "orange");

        이호선.addSection(new Section(이호선, 교대역, 강남역, 10));
        신분당선.addSection(new Section(신분당선, 강남역, 양재역, 10));
        삼호선.addSection(new Section(삼호선, 교대역, 남부터미널역, 2));
        삼호선.addSection(new Section(삼호선, 남부터미널역, 양재역, 3));

        pathFinder = new PathFinder(List.of(이호선, 신분당선, 삼호선));
    }

    @DisplayName("출발역와 도착역 사이의 최단 경로를 조회한다.")
    @Test
    void findPath() {
        // when
        Path path = pathFinder.findPath(교대역, 양재역);

        // then
        assertAll(
            () -> assertThat(path.getStations()).containsExactly(교대역, 남부터미널역, 양재역),
            () -> assertThat(path.getDistance()).isEqualTo(5)
        );
    }

    @DisplayName("출발역와 도착역이 같은 경로는 조회할 수 없다.")
    @Test
    void identicalSourceTarget() {
        // when & then
        assertThatThrownBy(() -> pathFinder.findPath(교대역, 교대역))
            .isInstanceOf(IdenticalSourceTargetNotAllowedException.class);
    }
}
