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
        lineAB = Line.builder()
                     .id(1L)
                     .color("y")
                     .upStation(stationA)
                     .downStation(stationB)
                     .build();

        firstSection = new Section(1L, lineAB, stationA, stationB, firstSectionDistance);

        List<Section> sectionList = new ArrayList<>();
        sectionList.add(firstSection);

        sections = new Sections(sectionList);
    }

    @Nested
    class Given_구간이_있을때{

        @Nested
        class When_추가하는역이_상행종점일경우 {

            @Test
            void 길이에_상관없이_추가가능하다() {
                // when
                Section givenAddSection = new Section(2L, lineAB, stationC, stationA, firstSectionDistance);

                // then
                Assertions.assertThat(sections.possibleToAddSection(givenAddSection)).isTrue();
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
        class When_기존역사이길이보다_크면 {

            @Test
            void 추가할수없다() {
                Section givenAddSection = new Section(2L, lineAB, stationC, stationB, firstSectionDistance + 1);

                Assertions.assertThat(sections.possibleToAddSection(givenAddSection)).isFalse();
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
}
