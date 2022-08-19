package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.applicaion.dto.PathResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PathFinderTest {
    private PathFinder pathFinder;

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;

    private Line 이호선;
    private Line 삼호선;
    private Line 신분당선;

    @BeforeEach
    void setup() {
        교대역 = new Station("교대역");
        강남역 = new Station("강남역");
        남부터미널역 = new Station("남부터미널역");
        양재역 = new Station("양재역");

        이호선 = new Line("2호선", "bg-green-600");
        신분당선 = new Line("신분당선", "bg-red-600");
        삼호선 = new Line("3호선", "bg-orange-600");

        이호선.addSection(구간_생성(이호선, 교대역, 강남역, 10));
        신분당선.addSection(구간_생성(신분당선, 강남역, 양재역, 100));
        삼호선.addSection(구간_생성(삼호선, 교대역, 남부터미널역, 2));
        삼호선.addSection(구간_생성(삼호선, 남부터미널역, 양재역, 3));

        List<Line> lines = Arrays.asList(이호선, 신분당선, 삼호선);
        pathFinder = new PathFinder(lines);
    }

    @DisplayName("출발역과 도착역이 같은 호선으로 경로를 조회한다.")
    @Test
    void startAndEndStationSameLineFindSuccess() {
        // when
        PathResponse pathResponse = pathFinder.findPath(교대역, 강남역);

        // then
        List<String> names = pathResponse.getStations().stream()
                .map(station -> station.getName())
                .collect(Collectors.toList());

        assertAll(
                () -> assertThat(names).containsExactly(교대역.getName(), 강남역.getName()),
                () -> assertThat(pathResponse.getDistance()).isEqualTo(10)
        );
    }

    @DisplayName("출발역과 도착역이 다른 호선인 경우의 경로를 조회한다.")
    @Test
    void startAndEndStationNotSameLineFindSuccess() {
        // when
        PathResponse pathResponse = pathFinder.findPath(교대역, 양재역);

        // then
        List<String> names = pathResponse.getStations().stream()
                .map(station -> station.getName())
                .collect(Collectors.toList());

        assertAll(
                () -> assertThat(names).containsExactly(교대역.getName(), 남부터미널역.getName(), 양재역.getName()),
                () -> assertThat(pathResponse.getDistance()).isEqualTo(5)
        );
    }

    @DisplayName("같은 호선이지만 환승을 포함한 최단 거리의 경로를 조회한다.")
    @Test
    void shortestPathFindSuccess() {
        // when
        PathResponse pathResponse = pathFinder.findPath(강남역, 양재역);

        // then
        List<String> names = pathResponse.getStations().stream()
                .map(station -> station.getName())
                .collect(Collectors.toList());

        assertAll(
                () -> assertThat(names).containsExactly(강남역.getName(), 교대역.getName(), 남부터미널역.getName(), 양재역.getName()),
                () -> assertThat(pathResponse.getDistance()).isEqualTo(15)
        );
    }

    @DisplayName("출발역과 도착역이 연결되지 않은 경우 경로 조회에 실패한다.")
    @Test
    void startAndEndStationIsNotLinkFindLineFail() {
        // given
        Station 신논현역 = new Station("신논현역");
        Station 언주역 = new Station("언주역");
        Line 구호선 = new Line("9호선", "bg-brown-600");
        구호선.addSection(구간_생성(구호선, 신논현역, 언주역, 10));

        List<Line> lines = Arrays.asList(이호선, 삼호선, 신분당선, 구호선);

        // when & then
        PathFinder pathFinder = new PathFinder(lines);
        assertThatThrownBy(() -> pathFinder.findPath(신논현역, 교대역))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("존재하지 않는 출발역이나 도착역을 조회하는 경우 조회 실패한다.")
    @Test
    void startOrEndStationNotExistFindLineFail() {
        // given
        Station 신논현역 = new Station("신논현역");

        // when & then
        assertThatThrownBy(() -> pathFinder.findPath(신논현역, 양재역))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private Section 구간_생성(Line line, Station upStation, Station downStation, int distance) {
        return new Section(line, upStation, downStation, distance);
    }
}