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

    @Nested
    class Given_구간이_있을때{
        Station stationA = new Station(10L, "stationA");
        Station stationB = new Station(11L, "stationB");
        Station stationC = new Station(12L, "stationC");
        Station stationD = new Station(13L, "stationD");
        Line lineAB = null;

        Section firstSection = null;
        Sections sections = null;

        @BeforeEach
        public void beforeEach() {
            lineAB = Line.builder()
                         .id(1L)
                         .color("y")
                         .upStation(stationA)
                         .downStation(stationB)
                         .build();

            firstSection = new Section(1L, lineAB, stationA, stationB, 5);

            List<Section> sectionList = new ArrayList<>();
            sectionList.add(firstSection);

            sections = new Sections(sectionList);
        }

        @Nested
        class When_추가하는역이_상행종점일경우 {

            @Test
            void 길이에_상관없이_추가가능하다() {
                // when
                Section givenAddSection = new Section(2L, lineAB, stationC, stationA, 5);

                // then
                Assertions.assertThat(sections.possibleToAddSection(givenAddSection)).isTrue();
            }
        }

        @Nested
        class When_추가하는역이_하행종점일경우 {

            @Test
            void 추가가능하다() {
                // when
                Section givenAddSection = new Section(2L, lineAB, stationB, stationC, 5);

                Assertions.assertThat(sections.possibleToAddSection(givenAddSection)).isTrue();
            }
        }

    }
}
