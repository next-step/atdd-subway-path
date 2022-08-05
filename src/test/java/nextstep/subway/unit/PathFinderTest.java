package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PathFinderTest {

    Line 신분당선;
    Station 신논현역;
    Station 논현역;

    Station 역삼역;
    Station 강남역;
    Station 양재역;
    Station 양재시민의숲;

    @BeforeEach
    void setUp() {
        신분당선 = Line.of("신분당선", "RED");
        신논현역 = new Station("신논현역");
        논현역 = new Station("논현역");
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        양재역 = new Station("양재역");
        양재시민의숲 = new Station("양재시민의숲");
    }


    @DisplayName("출발역으로부터 도착역까지로의 경로에 있는 역 목록")
    @Test
    void paths() {
        Line 이호선 = Line.of("신분당선", "RED");
        신분당선.addSection(강남역, 신논현역, 5);
        이호선.addSection(역삼역, 강남역, 3);

        PathFinder pathFinder = new PathFinder(List.of(신분당선, 이호선));

        assertThat(pathFinder.paths(역삼역, 신논현역).getPaths()).containsExactly(역삼역, 강남역, 신논현역);
        assertThat(pathFinder.paths(역삼역, 신논현역).getDistance()).isEqualTo(8);
    }
    
}
