package nextstep.subway.unit;

import nextstep.subway.section.repository.Section;
import nextstep.subway.section.repository.Sections;
import nextstep.subway.station.repository.Station;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SectionsTest {
    @Test
    void addSection() {
        // given
        Sections sections = new Sections(new ArrayList<>(List.of(
                Section.builder()
                        .upStation(new Station("강남역"))
                        .downStation(new Station("신논현역"))
                        .distance(10L)
                        .build()
        )));

        // when
        sections.addSection(Section.builder()
                .upStation(new Station("신논현역"))
                .downStation(new Station("논현역"))
                .distance(5L)
                .build());

        // then
        assertThat(sections.size()).isEqualTo(2);
    }

    @Test
    void getAllStation() {
        // given
        Sections sections = new Sections(new ArrayList<>(List.of(
                Section.builder()
                        .upStation(new Station("강남역"))
                        .downStation(new Station("신논현역"))
                        .distance(10L)
                        .build()
        )));

        // when
        List<Station> stations = sections.getAllStation();

        // then
        assertThat(stations).isEqualTo(List.of(
                new Station("강남역"),
                new Station("신논현역")
        ));
    }

    @Test
    void deleteSectionByLastStation() {
        // given
        Sections sections = new Sections(new ArrayList<>(List.of(
                Section.builder()
                        .upStation(new Station("강남역"))
                        .downStation(new Station("신논현역"))
                        .distance(10L)
                        .build(),
                Section.builder()
                        .upStation(new Station("신논현역"))
                        .downStation(new Station("논현역"))
                        .distance(5L)
                        .build()
        )));

        // when
        sections.deleteSectionByLastStation(new Station("논현역"));

        // then
        assertThat(sections.size()).isOne();
    }
}
