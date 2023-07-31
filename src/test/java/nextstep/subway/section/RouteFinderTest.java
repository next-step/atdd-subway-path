package nextstep.subway.section;

import static common.Constants.강남역;
import static common.Constants.양재역;
import static common.Constants.판교역;
import static nextstep.subway.section.SectionBuilder.aSection;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import nextstep.subway.common.exception.BusinessException;
import nextstep.subway.station.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RouteFinderTest {

    @DisplayName("findShortestRoute() : 출발역과 도착역이 같으면 예외를 발생시킨다")
    @Test
    void RouteFinder_fail_sameSourceAndDestination() {
        Section section = aSection()
            .withStations(new Station(1L, 강남역), new Station(2L, 양재역))
            .build();

        RouteFinder routeFinder = RouteFinder.from(List.of(section));

        assertThatThrownBy(
            () -> routeFinder.findShortestRoute(new Station(1L, 강남역), new Station(1L, 강남역)))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("출발역과 도착역은 같을 수 없습니다");
    }

    @DisplayName("findShortestRoute() : 시작역에서 목적역까지의 최단거리를 검색해 해당 경로만 가지는 Sections를 반환한다")
    @Test
    void findShortestRoute() {
        // given
        Section section1 = aSection()
            .withStations(new Station(1L, 강남역), new Station(2L, 양재역))
            .build();
        Section section2 = aSection()
            .withStations(new Station(2L, 양재역), new Station(3L, 판교역))
            .build();
        Section section3 = aSection()
            .withStations(new Station(1L, 강남역), new Station(3L, 판교역))
            .withDistance(30)
            .build();

        // when
        RouteFinder routeFinder = RouteFinder.from(List.of(section1, section2, section3));

        // then
        assertThat(routeFinder.totalDistance(new Station(1L, 강남역), new Station(3L, 판교역)))
            .isEqualTo(20);
        assertThat(routeFinder.findShortestRoute(new Station(1L, 강남역), new Station(3L, 판교역)))
            .containsExactly(
                new Station(1L, 강남역), new Station(2L, 양재역), new Station(3L, 판교역));
    }

    @DisplayName("findShortestRoute() : 출발역과 도착역이 연결되지 않았다면 예외를 발생시킨다")
    @Test
    void findShortestRoute_fail_unconnected() {
        Section section1 = aSection()
            .withStations(new Station(1L, 강남역), new Station(2L, 양재역))
            .build();

        RouteFinder routeFinder = RouteFinder.from(List.of(section1));

        assertThatThrownBy(
            () -> routeFinder.findShortestRoute(new Station(1L, 강남역), new Station(3L, 판교역)))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("출발역과 도착역이 연결되지 않았습니다");
    }
}