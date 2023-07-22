package nextstep.sections;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.domain.Sections;
import nextstep.subway.path.DijkstraShortestPathFinder;
import nextstep.subway.path.ShortestPath;
import nextstep.subway.path.ShortestPathFinder;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

public class ShortestPathFinderTest {

    ShortestPathFinder shortestPathFinder = new DijkstraShortestPathFinder();

    /**
     * A ---(10)--- B --(3)-- C
     * | (3)                  | (2)
     * D --(20)-- E --(2) -- F
     */
    @Test
    void find() {
        List<Section> givenSections = new ArrayList<>();
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Station stationC = new Station(3L, "C");
        Station stationD = new Station(4L, "D");
        Station stationE = new Station(5L, "E");
        Station stationF = new Station(6L, "F");

        Section sectionAB = Section.builder()
                               .id(1L)
                               .distance(10)
                               .upStation(stationA)
                               .downStation(stationB)
                               .build();
        givenSections.add(sectionAB);

        Section sectionBC = Section.builder()
                               .id(2L)
                               .distance(3)
                               .upStation(stationB)
                               .downStation(stationC)
                               .build();
        givenSections.add(sectionBC);

        Section sectionAD = Section.builder()
                               .id(3L)
                               .distance(3)
                               .upStation(stationA)
                               .downStation(stationD)
                               .build();
        givenSections.add(sectionAD);

        Section sectionDE = Section.builder()
                               .id(4L)
                               .distance(20)
                               .upStation(stationD)
                               .downStation(stationE)
                               .build();
        givenSections.add(sectionDE);

        Section sectionEF = Section.builder()
                               .id(5L)
                               .distance(2)
                               .upStation(stationE)
                               .downStation(stationF)
                               .build();
        givenSections.add(sectionEF);

        Section sectionCF = Section.builder()
                               .id(6L)
                               .distance(2)
                               .upStation(stationC)
                               .downStation(stationF)
                               .build();
        givenSections.add(sectionCF);

        ShortestPath shortestPath = shortestPathFinder.find(new Sections(givenSections), stationD, stationC);

        Assertions.assertThat(shortestPath.getDistance()).isEqualTo(16);
        Assertions.assertThat(shortestPath.getStations().get(0)).isEqualTo(stationD);
        Assertions.assertThat(shortestPath.getStations().get(1)).isEqualTo(stationA);
        Assertions.assertThat(shortestPath.getStations().get(2)).isEqualTo(stationB);
        Assertions.assertThat(shortestPath.getStations().get(3)).isEqualTo(stationC);
    }
}
