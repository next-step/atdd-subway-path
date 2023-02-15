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
import nextstep.subway.exception.NonConnectedSourceTargetException;
import nextstep.subway.exception.StationNotFoundException;

class PathFinderTest {

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;
    private Station 선릉역;
    private Station 한티역;
    private Line 이호선;
    private Line 신분당선;
    private Line 분당선;
    private Line 삼호선;

    private PathFinder pathFinder;

    /**
     * 교대                         강남           선릉                 한티
     *  ● ────────── <2> ────────── ● -----X----- ● ───── <분당> ───── ●
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
        교대역 = new Station(1L, "교대역");
        강남역 = new Station(2L, "강남역");
        양재역 = new Station(3L, "양재역");
        남부터미널역 = new Station(4L, "남부터미널역");
        선릉역 = new Station(5L, "선릉역");
        한티역 = new Station(6L, "한티역");

        이호선 = new Line(1L, "2호선", "green");
        신분당선 = new Line(2L, "신분당선", "red");
        분당선 = new Line(3L, "분당선", "yellow");
        삼호선 = new Line(4L, "삼호선", "orange");

        이호선.addSection(new Section(이호선, 교대역, 강남역, 10));
        신분당선.addSection(new Section(신분당선, 강남역, 양재역, 10));
        분당선.addSection(new Section(분당선, 선릉역, 한티역, 10));
        삼호선.addSection(new Section(삼호선, 교대역, 남부터미널역, 2));
        삼호선.addSection(new Section(삼호선, 남부터미널역, 양재역, 3));

        pathFinder = new PathFinder(List.of(이호선, 신분당선, 분당선, 삼호선));
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

    @DisplayName("지하철 경로 조회 시, 출발역와 도착역은 같지 않아야 한다.")
    @Test
    void identicalSourceTarget() {
        // when & then
        assertThatThrownBy(() -> pathFinder.findPath(교대역, 교대역))
            .isInstanceOf(IdenticalSourceTargetNotAllowedException.class);
    }

    @DisplayName("지하철 경로 조회 시, 출발역와 도착역은 연결되어 있어야 한다.")
    @Test
    void notConnectedSourceTarget() {
        // when & then
        assertThatThrownBy(() -> pathFinder.findPath(교대역, 한티역))
            .isInstanceOf(NonConnectedSourceTargetException.class);
    }

    @DisplayName("지하철 경로 조회 시, 출발역과 도착역은 모두 존재하는 역이어야 한다.")
    @Test
    void identicalSourceTarget22() {
        // when & then
        assertThatThrownBy(() -> pathFinder.findPath(교대역, new Station(999L, "없는역")))
            .isInstanceOf(StationNotFoundException.class);
    }
}
