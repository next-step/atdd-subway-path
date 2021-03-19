package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PathFinderTest {

    private PathFinder pathFinder;

    private Station 강남역;
    private Station 양재역;
    private Station 교대역;
    private Station 남부터미널역;

    @BeforeEach
    void setUp() {
        강남역 = getStation();
        양재역 = new Station("양재역");
        교대역 = new Station("교대역");
        남부터미널역 = new Station("남부터미널역");

        Line 신분당선 = new Line("신분당선", "bg-red-600", 강남역, 양재역, 10);
        Line 이호선 = new Line("이호선", "bg-red-600", 교대역, 강남역, 10);
        Line 삼호선 = new Line("삼호선", "bg-red-600", 교대역, 양재역, 5);

        삼호선.addSection(new Section(삼호선, 교대역, 남부터미널역, 3));

        List<Section> sections = Arrays.asList(신분당선, 이호선, 삼호선).stream()
                .map(it -> it.getSections())
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        pathFinder = PathFinder.create(sections);
    }

    private Station getStation() {
        return new Station("강남역");
    }

    @DisplayName("시작역, 끝역 동일하면 실패")
    @Test
    void failStationsSame() {
        // when
        assertThatThrownBy(() -> pathFinder.calculateShortestPath(강남역, 강남역));
    }
}