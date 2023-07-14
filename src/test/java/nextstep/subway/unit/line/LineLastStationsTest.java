package nextstep.subway.unit.line;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import nextstep.subway.line.domain.LineLastStations;
import nextstep.subway.section.domain.SectionStations;
import nextstep.subway.station.domain.Station;

class LineLastStationsTest {

    private final Station stationA = new Station("a");
    private final Station stationB = new Station("b");
    private final Station stationC = new Station("C");

    @Test
    void checkCanAddSection() {
        LineLastStations stations = new LineLastStations(stationA, stationB);
        SectionStations sectionStationsA = new SectionStations(stationB, stationC);
        SectionStations sectionStationsB = new SectionStations(stationA, stationC);
        SectionStations sectionStationsC = new SectionStations(stationB, stationA);

        assertThat(stations.isLastDownwardIsSameWithSectionUpwardStation(sectionStationsA)).isTrue();
        assertThat(stations.isLastDownwardIsSameWithSectionUpwardStation(sectionStationsB)).isFalse();
        assertThat(stations.isLastDownwardIsSameWithSectionUpwardStation(sectionStationsC)).isTrue();
    }

    @Test
    void updateDownStation() {
        LineLastStations stations = new LineLastStations(stationA, stationB);

        stations.updateDownLastStation(stationC);

        assertThat(stations.getDownLastStation()).isEqualTo(stationC);
    }
}
