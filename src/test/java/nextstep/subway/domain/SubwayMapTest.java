package nextstep.subway.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.domain.exception.StationsNotConnectedException;
import nextstep.subway.unit.Fixtures;

class SubwayMapTest {
    private static final Station 교대역 = Fixtures.createStation(10L, "교대역");
    private static final Station 강남역 = Fixtures.createStation(11L, "강남역");
    private static final Station 양재역 = Fixtures.양재역;
    private static final Station 남부터미널역 = Fixtures.createStation(12L, "남부터미널역");

    private static List<Station> stations;
    private static List<Section> sections;

    /**
     * 교대역    ------   강남역
     * |                        |
     * |                        |
     * |                        |
     * 남부터미널역  ----   양재역
     */

    @BeforeAll
    static void setup() {
        stations = new ArrayList<>(Arrays.asList(교대역, 강남역, 남부터미널역, 양재역));
        sections = new ArrayList<>(Arrays.asList(
            Fixtures.createSection(1L, null, 교대역, 강남역, 10),
            Fixtures.createSection(1L, null, 강남역, 양재역, 10),
            Fixtures.createSection(1L, null, 남부터미널역, 양재역, 3),
            Fixtures.createSection(1L, null, 교대역, 남부터미널역, 2)
        ));
    }

    @DisplayName("역과 구간이 주어졌을 때 최단거리를 구한다.")
    @Test
    void findShortestPath() {
        SubwayPath subwayPath = new SubwayMap(stations, sections).findShortestPath(교대역, 양재역);

        assertAll(
            () -> assertThat(subwayPath.getStations()).containsExactly(교대역, 남부터미널역, 양재역),
            () -> assertThat(subwayPath.getTotalDistance()).isEqualTo(5)
        );
    }

    @DisplayName("연결되지 않은 역 사이의 거리를 구하려고 할 때 예외를 던진다.")
    @Test
    void findShortestPathBetweenNotConnectedStation() {
        List<Station> invalidStations = new ArrayList<>(stations);
        invalidStations.add(Fixtures.광교역);
        assertThatThrownBy(() -> new SubwayMap(invalidStations, sections).findShortestPath(교대역, Fixtures.광교역))
            .isInstanceOf(StationsNotConnectedException.class);
    }
}
