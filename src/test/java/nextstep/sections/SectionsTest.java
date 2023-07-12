package nextstep.sections;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

public class SectionsTest {

    @Nested
    class Given_구간이_있고_상행역이_공통일때 {

        @Nested
        class When_상행역이_공통이면 {

            @Test
            void 길이에_상관없이_추가가능하다() {
                Station stationA = new Station("stationA");
                Station stationB = new Station("stationB");
                Station stationC = new Station("stationC");
                Station stationD = new Station("stationD");

                Line lineAB = Line.builder()
                                  .id(1L)
                                  .color("y")
                                  .upStation(stationA)
                                  .downStation(stationB)
                                  .build();

                Section firstSection = new Section(1L, lineAB, stationA, stationB, 5);
                List<Section> sectionList = new ArrayList<>();
                sectionList.add(firstSection);
                Sections sections = new Sections(sectionList);

                // when
                Section givenAddSection = new Section(2L, lineAB, stationA, stationC, 5);

                // then
                Assertions.assertThat(sections.possibleToAddSection(givenAddSection)).isTrue();
            }
        }
    }
    @DisplayName("기존 구간에서 추가 가능한 구간을 찾는다")
    @Test
    void getLinkableSection() {
        Station stationA = new Station("stationA");
        Station stationB = new Station("stationB");
        Station stationC = new Station("stationC");
        Station stationD = new Station("stationD");

        Line lineAB = Line.builder()
                        .id(1L)
                        .color("y")
                          .upStation(stationA)
                        .downStation(stationB)
                        .build();

        Section firstSection = new Section(1L, lineAB, stationA, stationB, 5);
        List<Section> sectionList = new ArrayList<>();
        sectionList.add(firstSection);

        Sections sections = new Sections(sectionList);

        Section givenAddSection = new Section(2L, lineAB, stationA, stationC, 5);

        Optional<Section> maybeTargetSection = sections.findLinkableSection(givenAddSection);

        Assertions.assertThat(maybeTargetSection).isPresent();
        Assertions.assertThat(maybeTargetSection.get().equalsById(firstSection.getId())).isTrue();
    }
}
