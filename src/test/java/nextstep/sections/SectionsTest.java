package nextstep.sections;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

public class SectionsTest {
    Station stationA = new Station(10L, "stationA");
    Station stationB = new Station(11L, "stationB");
    Station stationC = new Station(12L, "stationC");
    Station stationD = new Station(13L, "stationD");
    Line lineAB = null;

    Section firstSection = null;
    Sections sections = null;
    int firstSectionDistance = 10;

    @BeforeEach
    public void beforeEach() {
        firstSection = new Section(1L, lineAB, stationA, stationB, firstSectionDistance);

        List<Section> sectionList = new ArrayList<>();
        sectionList.add(firstSection);

        sections = new Sections(sectionList);

        lineAB = Line.builder()
                     .id(1L)
                     .color("y")
                     .upStation(stationA)
                     .downStation(stationB)
                     .sections(sections)
                     .build();
    }

    @Nested
    class Given_구간이_있을때 {

        @Nested
        class When_추가하는역이_상행종점일경우 {

            @Test
            void 길이에_상관없이_추가가능하다() {
                // when
                Section givenAddSection = new Section(2L, lineAB, stationC, stationA, firstSectionDistance);

                // then
                Assertions.assertThat(sections.possibleToAddSection(givenAddSection)).isTrue();
            }

            @Test
            void 구간을_추가하고_상행종점이_변경된다() {
                Section givenAddSection = new Section(2L, lineAB, stationC, stationA, firstSectionDistance);

                // then
                Assertions.assertThat(sections.possibleToAddSection(givenAddSection)).isTrue();

                lineAB.addSection(givenAddSection);

                Assertions.assertThat(lineAB.equalUpStation(stationC.getId())).isTrue();
            }
        }

        @Nested
        class When_추가하는역이_하행종점일경우 {

            @Test
            void 추가가능하다() {
                // when
                Section givenAddSection = new Section(2L, lineAB, stationB, stationC, firstSectionDistance);

                Assertions.assertThat(sections.possibleToAddSection(givenAddSection)).isTrue();
            }

            @Test
            void 추가하고_하행종점이_변경된다() {
                Section givenAddSection = new Section(2L, lineAB, stationB, stationC, firstSectionDistance);

                Assertions.assertThat(sections.possibleToAddSection(givenAddSection)).isTrue();

                lineAB.addSection(givenAddSection);

                Assertions.assertThat(lineAB.equalDownStation(stationC.getId())).isTrue();
            }
        }
    }

    @Nested
    class Given_구간이있고_하행종점이같은_새로운구간을_추가할때 {

        @Nested
        class When_기존구간의_길이와_같으면 {

            @Test
            void 추가할수없다() {
                Section givenAddSection = new Section(2L, lineAB, stationC, stationB, firstSectionDistance);

                Assertions.assertThat(sections.possibleToAddSection(givenAddSection)).isFalse();
            }
        }

        @Nested
        class When_기존구간의_길이보다_크면 {

            @Test
            void 추가할수없다() {
                Section givenAddSection = new Section(2L, lineAB, stationC, stationB, firstSectionDistance + 1);

                Assertions.assertThat(sections.possibleToAddSection(givenAddSection)).isFalse();
            }
        }

        @Nested
        class When_기존구간의_길이보다_작으면 {

            @Test
            void 추가가능하다() {
                Section givenAddSection = new Section(2L, lineAB, stationC, stationB, firstSectionDistance / 2);

                Assertions.assertThat(sections.possibleToAddSection(givenAddSection)).isTrue();

                lineAB.addSection(givenAddSection);

                Assertions.assertThat(lineAB.equalDownStation(stationC.getId())).isTrue();
            }
        }
    }

    @Nested
    class Given_구간이있고_상행종점이같은_새로운구간을_추가할때 {

        @Nested
        class When_기존구간의_길이와_같으면 {

            @Test
            void 추가할수없다() {
                Section givenAddSection = new Section(2L, lineAB, stationA, stationD, firstSectionDistance);

                Assertions.assertThat(sections.possibleToAddSection(givenAddSection)).isFalse();
            }
        }

        @Nested
        class When_기존역사이길이보다_크면 {

            @Test
            void 추가할수없다() {
                Section givenAddSection = new Section(2L, lineAB, stationA, stationD, firstSectionDistance + 1);

                Assertions.assertThat(sections.possibleToAddSection(givenAddSection)).isFalse();
            }
        }
    }

    @Nested
    class Given_구간이있을때 {

        @Nested
        class When_이미등록된구간을등록하면 {

            @Test
            void 추가할수없다() {
                Section givenAddSection = new Section(2L, lineAB, stationA, stationB, firstSectionDistance);

                Assertions.assertThat(sections.possibleToAddSection(givenAddSection)).isFalse();
            }
        }

        @Nested
        class When_새로운구간과_접점이없는_구간을_등록하면 {

            @Test
            void 추가할수없다() {
                Section givenAddSection = new Section(2L, lineAB, stationC, stationD, firstSectionDistance);

                Assertions.assertThat(sections.possibleToAddSection(givenAddSection)).isFalse();
            }
        }
    }

    @Nested
    class Given_노선에_구간을_추가했을때 {
        @Nested
        class When_구간을조회하면 {

            @Test
            void 상행역_종점역_순으로_조회된다() {
                Section givenAddSection = new Section(2L, lineAB, stationC, stationB, firstSectionDistance / 2);

                Assertions.assertThat(sections.possibleToAddSection(givenAddSection)).isTrue();

                lineAB.addSection(givenAddSection);


                List<Section> orderedSections = lineAB.orderedSections();

                Assertions.assertThat(orderedSections.get(0).getUpStation()).isEqualTo(stationA);
                Assertions.assertThat(orderedSections.get(0).getDownStation()).isEqualTo(stationC);
                Assertions.assertThat(orderedSections.get(1).getUpStation()).isEqualTo(stationC);
                Assertions.assertThat(orderedSections.get(1).getDownStation()).isEqualTo(stationB);
            }
        }
    }

    @Nested
    class Given_노선에_구간이_2개이상일때 {

        @Nested
        class When_중간역을_제거하면 {

            @Test
            void 거리가_합쳐진다() {
                Section givenAddSection = new Section(2L, lineAB, stationB, stationC,5);

                lineAB.addSection(givenAddSection);

                lineAB.deleteSection(stationB.getId());

                Assertions.assertThat(lineAB.getSections().allDistance()).isEqualTo(15);
            }
        }

        @Nested
        class When_종점역을_제거하면 {

            @Test
            void 제거된다 () {
                Section givenAddSection = new Section(2L, lineAB, stationB, stationC,5);

                lineAB.addSection(givenAddSection);

                lineAB.deleteSection(stationB.getId());

                Assertions.assertThat(lineAB.getSections().hasOneSection()).isTrue();
            }
        }
    }


}
