package nextstep.subway.unit;

import nextstep.subway.section.repository.Sections;
import nextstep.subway.station.repository.Station;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static nextstep.subway.unit.fixture.SectionFixture.강남역_TO_신논현역;
import static nextstep.subway.unit.fixture.SectionFixture.신논현역_TO_논현역;
import static nextstep.subway.unit.fixture.StationFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

class SectionsTest {
    @Test
    void addSection() {
        // given
        Sections sections = new Sections(new ArrayList<>(List.of(강남역_TO_신논현역())));

        // when
        sections.addSection(신논현역_TO_논현역());

        // then
        assertThat(sections.size()).isEqualTo(2);
    }

    @Test
    void getAllStation() {
        // given
        Sections sections = new Sections(new ArrayList<>(List.of(강남역_TO_신논현역())));

        // when
        List<Station> stations = sections.getAllStation();

        // then
        assertThat(stations).isEqualTo(List.of(강남역(), 신논현역()));
    }

    @Test
    void deleteSectionByLastStation() {
        // given
        Sections sections = new Sections(new ArrayList<>(List.of(
                강남역_TO_신논현역(),
                신논현역_TO_논현역()
        )));

        // when
        sections.deleteSectionByLastStation(논현역());

        // then
        assertThat(sections.size()).isOne();
    }
}
