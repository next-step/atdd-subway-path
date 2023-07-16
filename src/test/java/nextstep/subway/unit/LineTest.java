package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Nested
    class Given_노선에_구간이1개있을때 {

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

        @Test
        void getStations() {
            // when
            lineAB.addSection(firstSection);

            // then
            assertThat(lineAB.getSections().isEmpty())
                                           .isFalse();
        }

        @Test
        void removeSection() {
            // when
            lineAB.addSection(firstSection);

            // then
            Assertions.assertThrows(InvalidSectionDeleteException.class, () -> {
                lineAB.deleteSection(stationB);
            });
        }
    }

    @Nested
    class Given_노선에_구간이_2개이상일때 {
        private Station stationA;
        private Station stationB;
        private Station stationC;
        private Line lineAB;
        private Section firstSection;

        @BeforeEach
        void beforeEach() {
            // given
            stationA = createStation(1L, "stationA");
            stationB = createStation(2L, "stationB");
            stationC = createStation(3L, "stationC");

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
        }

        @Nested
        class When_상행_종점역을_제거하면 {

            @Test
            void 상행_종점역이_변경된다() {
                // when
                lineAB.deleteSection(stationA);

                // when
                assertThat(lineAB.equalUpStation(stationB));
                assertThat(lineAB.equalDownStation(stationC));
            }
        }

        @Nested
        class When_하행_종점역을_제거하면 {

            @Test
            void 하행_종점역이_변경된다() {
                // when
                lineAB.deleteSection(stationC);

                // when
                assertThat(lineAB.equalUpStation(stationA)).isTrue();
                assertThat(lineAB.equalDownStation(stationB)).isTrue();
            }
        }
    }

    private Station createStation(Long id, String name) {
        return new Station(id, name);
    }
}
