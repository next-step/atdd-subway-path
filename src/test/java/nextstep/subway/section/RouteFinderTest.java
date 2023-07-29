package nextstep.subway.section;

import static common.Constants.강남역;
import static common.Constants.양재역;
import static common.Constants.판교역;
import static nextstep.subway.section.SectionBuilder.aSection;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import nextstep.subway.station.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RouteFinderTest {

    @DisplayName("시작역에서 목적역까지의 최단거리를 검색해 해당 경로만 가지는 Sections를 반환한다")
    @Test
    void findShortestRoute() {
        // given
        // TODO: 안되면 역 객체 추출해서 해보기
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
        RouteFinder routeFinder = new RouteFinder(List.of(section1, section2, section3), new Station(1L, 강남역), new Station(3L, 판교역));

        // then
        assertThat(routeFinder.totalDistance()).isEqualTo(20);
        assertThat(routeFinder.findShortestRoute()).containsExactly(new Station(1L, 강남역), new Station(2L, 양재역), new Station(3L, 판교역));
    }

}