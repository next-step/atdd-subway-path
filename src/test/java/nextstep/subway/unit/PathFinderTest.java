package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class PathFinderTest {

    Line 신분당선;
    Line 팔호선;
    Line 이호선;

    Station 신논현역;
    Station 역삼역;
    Station 강남역;
    Station 문정역;
    Station 장지역;

    /**
     * 신논현역                                        문정역
     * /                                              /
     * *신분당선*                                     *8호선*
     * /                                              /
     * 강남역  --- *2호선* ---  역삼역                  장지역
     */
    @BeforeEach
    void setUp() {
        신논현역 = new Station("신논현역");
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        문정역 = new Station("문정역");
        장지역 = new Station("장지역");

        신분당선 = Line.of("신분당선", "RED");
        팔호선 = Line.of("8호선", "RED");
        이호선 = Line.of("이호선", "GREEN");

        신분당선.addSection(신논현역, 강남역, 5);
        이호선.addSection(역삼역, 강남역, 3);
        팔호선.addSection(문정역, 장지역, 3);
    }


    @DisplayName("출발역으로부터 도착역까지로의 경로에 있는 역 목록")
    @Test
    void paths() {

        Path path = PathFinder.of(List.of(신분당선, 이호선, 팔호선)).paths(역삼역, 신논현역);

        assertAll(() -> assertThat(path.getStations()).containsExactly(역삼역, 강남역, 신논현역),
                () -> assertThat(path.getDistance()).isEqualTo(8)
        );

    }

    @DisplayName("지하철 경로 조회 시 출발역과 도착역이 같을 경우 예외 발생")
    @Test
    void 출발역_도착역_같을경우_오류() {

        PathFinder pathFinder = PathFinder.of(List.of(신분당선, 이호선, 팔호선));

        assertThatThrownBy(() -> {pathFinder.paths(역삼역, 역삼역);}).isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("출발역과 도착역의 연결이 되어 있지 않은 경우")
    @Test
    void 출발역_도착역_미연결() {

        PathFinder pathFinder = PathFinder.of(List.of(신분당선, 이호선, 팔호선));

        assertThatThrownBy(() -> {pathFinder.paths(신논현역, 장지역);}).isInstanceOf(IllegalArgumentException.class);

    }

}
