package nextstep.subway.unit;

import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.exception.InvalidSectionDeleteException;
import nextstep.subway.station.domain.Station;

class LineTest {

    private Station stationA;
    private Station stationB;
    private Line lineAB;
    private Section firstSection;

    @BeforeEach
    void beforeEach() {
        // given
        stationA = createStation(1L, "stationA");
        stationB = createStation(2L, "stationB");

        lineAB = Line.builder()
                     .id(10L)
                     .name("lineAB")
                     .color("yellow")
                     .upStation(stationA)
                     .downStation(stationB)
                     .sections(new Sections(new ArrayList<>()))
                     .build();

        firstSection = Section.builder().line(lineAB).distance(10)
                              .upStation(stationA)
                              .downStation(stationB)
                              .build();
    }

    @Test
    void addSection() {
        // when
        // then
        Assertions.assertDoesNotThrow(() -> {
            lineAB.addSection(firstSection);
        });

    }

    private Station createStation(Long id, String name) {
        return new Station(id, name);
    }

    @Test
    void getStations() {
        // when
        lineAB.addSection(firstSection);

        // then
        org.assertj.core.api.Assertions.assertThat(lineAB.getSections().isEmpty())
                                       .isFalse();
    }

    @Test
    void removeSection() {
        // when
        lineAB.addSection(firstSection);

        // then
        Assertions.assertThrows(InvalidSectionDeleteException.class, () -> {
            lineAB.deleteSection(stationB.getId());
        });
    }
}
