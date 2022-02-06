package nextstep.subway.applicaion;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.handler.exception.ExploreException;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static nextstep.subway.domain.factory.EntityFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("경로 탐색 단위 테스트")
class PathFinderTest {
    private Line 이호선;
    private Line 삼호선;
    private Line 신분당선;

    private Station 강남역;
    private Station 교대역;
    private Station 서초역;
    private Station 양재역;
    private Station 매봉역;
    private Station 양재시민의숲역;

    private List<Line> allLines;
    private PathFinder pathFinder;

    @BeforeEach
    void init() {
        강남역 = createMockStation(1L, "강남역");
        교대역 = createMockStation(2L, "교대역");
        서초역 = createMockStation(3L, "서초역");
        양재역 = createMockStation(4L, "양재역");
        매봉역 = createMockStation(5L, "매봉역");
        양재시민의숲역 = createMockStation(6L, "양재시민의숲역");

        allLines = new ArrayList<>();

        이호선 = createMockLine(1L, "2호선", "green", 강남역, 교대역, 6);
        이호선.addSection(createSection(이호선, 교대역, 서초역, 4));
        allLines.add(이호선);

        삼호선 = createMockLine(2L, "3호선", "orange", 교대역, 매봉역, 11);
        삼호선.addSection(createSection(삼호선, 교대역, 양재역, 7));
        allLines.add(삼호선);

        신분당선 = createMockLine(3L, "신분당선", "red", 강남역, 양재시민의숲역, 16);
        신분당선.addSection(createSection(신분당선, 양재역, 양재시민의숲역, 6));
        allLines.add(신분당선);
    }

    @DisplayName("두 역이 입력되면 최단 경로 리스트를 반환한다.")
    @Test
    void explore() {
        // given
        pathFinder = new PathFinder(allLines);

        // when
        List<String> exploredStations = pathFinder.explore(강남역.getName(), 매봉역.getName());
        int distance = pathFinder.exploreDistance();

        // then
        assertThat(exploredStations).containsExactly(Arrays.array("강남역", "양재역", "매봉역"));
        assertThat(distance).isEqualTo(14);
    }

    @DisplayName("두 역이 이어지지 않은 경우 예외를 발생시킨다.")
    @Test
    void validateExplore() {
        // given
        Station 용산역 = createMockStation(7L, "용산역");
        Station 운정역 = createMockStation(8L, "운정역");
        Line 경의중앙선 = createLine("경의중앙선", "blue", 용산역, 운정역, 30);
        allLines.add(경의중앙선);

        pathFinder = new PathFinder(allLines);

        // when/then
        assertThatThrownBy(() -> pathFinder.explore(강남역.getName(), 용산역.getName()))
                .isInstanceOf(ExploreException.class);
    }
}