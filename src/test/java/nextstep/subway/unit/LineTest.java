package nextstep.subway.unit;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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

    @Nested
    class Given_노선에_구간이_2개이상일때 {

        @Nested
        class When_상행_종점역을_제거하면 {
            @Test
            void 상행_종점역이_변경된다() {
                // given
                Station stationA = createStation(1L, "stationA");
                Station stationB = createStation(2L, "stationB");
                Station stationC = createStation(3L, "stationC");

                firstSection = new Section(1L, lineAB, stationA, stationB, 10);

                List<Section> sectionList = new ArrayList<>();
                sectionList.add(firstSection);

                Sections sections = new Sections(sectionList);

                lineAB = Line.builder()
                             .id(1L)
                             .color("y")
                             .upStation(stationA)
                             .downStation(stationB)
                             .sections(sections)
                             .build();

                Section givenAddSection = new Section(2L, lineAB, stationB, stationC,5);

                lineAB.addSection(givenAddSection);

                // when
                lineAB.deleteSection(stationA.getId());

                // when
                org.assertj.core.api.Assertions.assertThat(lineAB.equalUpStation(stationB.getId()));
                org.assertj.core.api.Assertions.assertThat(lineAB.equalDownStation(stationC.getId()));
            }
        }

        @Nested
        class When_하행_종점역을_제거하면 {

            @Test
            void 하행_종점역이_변경된다() {
                // given
                Station stationA = createStation(1L, "stationA");
                Station stationB = createStation(2L, "stationB");
                Station stationC = createStation(3L, "stationC");

                firstSection = new Section(1L, lineAB, stationA, stationB, 10);

                List<Section> sectionList = new ArrayList<>();
                sectionList.add(firstSection);

                Sections sections = new Sections(sectionList);

                lineAB = Line.builder()
                             .id(1L)
                             .color("y")
                             .upStation(stationA)
                             .downStation(stationB)
                             .sections(sections)
                             .build();

                Section givenAddSection = new Section(2L, lineAB, stationB, stationC,5);

                lineAB.addSection(givenAddSection);

                // when
                lineAB.deleteSection(stationC.getId());

                // when
                org.assertj.core.api.Assertions.assertThat(lineAB.equalUpStation(stationA.getId()));
                org.assertj.core.api.Assertions.assertThat(lineAB.equalDownStation(stationB.getId()));
            }
        }
    }
}
