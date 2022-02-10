package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("최적 경로 관리")
public class PathFinderTest {
    private static final int DEFAULT_DISTANCE = 5;

    private Line 일호선;
    private Line 이호선;
    private Line 분당선;

    @BeforeEach
    void setUp() {
        일호선 = createLineEntity("일호선", "black");
        이호선 = createLineEntity("이호선", "green");
        분당선 = createLineEntity("분당선", "red");
    }

    @DisplayName("lines으로 최적 경로 도메인을 생성한다.")
    @Test
    void createPathFinder() {
        // when
        PathFinder pathFinder = new PathFinder(Arrays.asList(일호선, 이호선));

        // then
        assertThat(pathFinder).isNotNull();
    }

    @DisplayName("노선이 없으면 예외가 발생.")
    @Test
    void createPathFinderNoneLinesException() {
        // when, then
        assertThatThrownBy(() -> new PathFinder(new ArrayList<>()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("노선이 없습니다.");
    }

    @DisplayName("부평역-판교역 최적 경로를 구한다.")
    @Test
    void shortedPathStations() {
        // given
        Station 부평역 = createStationEntity(1L, "부평역");
        Station 신도림역 = createStationEntity(2L, "신도림역");
        Station 강남역 = createStationEntity(3L, "강남역");
        Station 양재역 = createStationEntity(4L, "양재역");
        Station 판교역 = createStationEntity(5L, "판교역");

        일호선.addSection(부평역, 신도림역, DEFAULT_DISTANCE);

        이호선.addSection(신도림역, 강남역, DEFAULT_DISTANCE);

        분당선.addSection(강남역, 양재역, DEFAULT_DISTANCE);
        분당선.addSection(양재역, 판교역, DEFAULT_DISTANCE);

        PathFinder pathFinder = new PathFinder(Arrays.asList(일호선, 이호선, 분당선));
        int totalPathWeight = 20;

        // when
        List<Station> stations = pathFinder.shortPathStations(부평역, 판교역);
        int pathWeight = pathFinder.shortPathWeight(부평역, 판교역);

        // then
        assertAll(
                () -> assertThat(stations).containsExactly(부평역, 신도림역, 강남역, 양재역, 판교역),
                () -> assertThat(pathWeight).isEqualTo(totalPathWeight)
        );
    }


    @DisplayName("동일한 출발지와 도착역으로 최단 경로 조회하면 예외 발생")
    @Test
    void sourceEqualsTargetException() {
        // given
        Station 부평역 = createStationEntity(1L, "부평역");

        PathFinder pathFinder = new PathFinder(Arrays.asList(일호선));

        // when, then
        assertThatThrownBy(() -> pathFinder.shortPathWeight(부평역, 부평역))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("출발역과 도착역이 동일합니다.");
    }

    @DisplayName("연결이 안된 구간으로 요청하면 예외 발생")
    @Test
    void notConnectException() {
        // given
        Station 부평역 = createStationEntity(1L, "부평역");
        Station 신도림역 = createStationEntity(2L, "신도림역");
        Station 춘천역 = createStationEntity(3L, "춘천역");

        일호선.addSection(부평역, 신도림역, DEFAULT_DISTANCE);

        PathFinder pathFinder = new PathFinder(Arrays.asList(일호선));

        // when, then
        assertThatThrownBy(() -> pathFinder.shortPathStations(부평역, 춘천역))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("노선에 역이 존재하지 않습니다.");
    }

    private Station createStationEntity(Long id, String name) {
        return new Station(id, name);
    }

    private Line createLineEntity(String name, String color) {
        return new Line(name, color);
    }
}
