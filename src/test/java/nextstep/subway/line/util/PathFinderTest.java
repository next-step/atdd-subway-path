package nextstep.subway.line.util;

import nextstep.subway.line.Line;
import nextstep.subway.line.section.Section;
import nextstep.subway.station.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class PathFinderTest {

    private Station 강남역;
    private Station 교대역;
    private Station 사당역;
    private Station 신림역;

    private Line 일호선;
    private Line 이호선;
    private Line 삼호선;
    private Line 신분당선;

    private PathFinder pathFinder;


    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        교대역 = new Station("교대역");
        사당역 = new Station("사당역");
        신림역 = new Station("신림역");

        이호선 = new Line("2호선", "green", 강남역, 교대역);
        Section section = new Section(강남역, 교대역, 10, 이호선);
        이호선.addSection(section);

        삼호선 = new Line("3호선", "orange", 교대역, 사당역);
        Section section2 = new Section(교대역, 사당역, 3, 삼호선);
        삼호선.addSection(section2);

        일호선 = new Line("1호선", "red", 사당역, 신림역);
        Section section3 = new Section(사당역, 신림역, 1, 일호선);
        일호선.addSection(section3);

        신분당선 = new Line("신분당선", "yellow", 신림역, 강남역);
        Section section4 = new Section(신림역, 강남역, 2, 신분당선);
        신분당선.addSection(section4);

        pathFinder = new PathFinder(이호선, 삼호선, 일호선, 신분당선);
    }

    @Test
    @DisplayName("출발역으로부터 도착역까지의 경로에 있는 역 목록")
    void findPath() {
        Path path = pathFinder.findPath(강남역, 교대역);
        assertThat(path.getPath()).contains(강남역, 교대역);
        assertThat(path.getDistance()).isEqualTo(6);
    }

    @Test
    @DisplayName("예외_출발역과 도착역이 같습니다.")
    void error_sameStation() {
        Assertions.assertThatThrownBy(() -> pathFinder.findPath(강남역, 강남역))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("존재하지 않는 역입니다.")
    void error_notExistStation() {
        Station 잠실역 = new Station("잠실역");
        Assertions.assertThatThrownBy(() -> pathFinder.findPath(강남역, 잠실역))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("경로가 존재하지 않습니다.")
    void error_notExistPath() {
        Assertions.assertThatThrownBy(() -> pathFinder.findPath(강남역, 사당역))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
