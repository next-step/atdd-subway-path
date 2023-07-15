package nextstep.subway.unit.section;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import nextstep.subway.common.exception.CustomException;
import nextstep.subway.section.domain.SectionStations;
import nextstep.subway.station.domain.Station;

class SectionStationsTest {

    @Test
    void create() {
        Station a = new Station("a");
        assertThrows(CustomException.class, ()-> new SectionStations(a, a));
    }

    @Test
    void checkStationInSection() {
        Station a = new Station("a");
        Station b = new Station("b");
        Station c = new Station("c");

        SectionStations sectionStations = new SectionStations(a, b);

        assertThat(sectionStations.checkStationInSection(a)).isTrue();
        assertThat(sectionStations.checkStationInSection(b)).isTrue();
        assertThat(sectionStations.checkStationInSection(c)).isFalse();
    }
}
