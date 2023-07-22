package nextstep.sections;

import java.util.ArrayList;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

public class SectionsPathTest {
    Station stationA = new Station(10L, "stationA");
    Station stationB = new Station(11L, "stationB");
    Station stationC = new Station(12L, "stationC");
    Station stationD = new Station(13L, "stationD");
    Station stationE = new Station(14L, "stationE");
    Station stationF = new Station(15L, "stationF");

    Line lineAB = null;

    Line lineEF = null;

    Section sectionAB = null;
    Section sectionEF = null;

    @BeforeEach
    public void beforeEach() {
        sectionAB = new Section(1L, lineAB, stationA, stationB, 20);
        Sections sectionsForAB = new Sections(new ArrayList<>());
        sectionsForAB.appendSection(sectionAB);

        lineAB = Line.builder()
                     .id(1L)
                     .color("y")
                     .upStation(stationA)
                     .downStation(stationB)
                     .sections(sectionsForAB)
                     .build();


        sectionEF = new Section(2L, lineEF, stationE, stationF, 10);
        Sections sectionsForEF = new Sections(new ArrayList<>());
        sectionsForEF.appendSection(sectionAB);

        lineEF = Line.builder()
                     .id(2L)
                     .color("yt")
                     .upStation(stationE)
                     .downStation(stationF)
                     .sections(sectionsForEF)
                     .build();
    }

    @Nested
    class Given_구간이_다수일때 {

        @Nested
        class When_노선에_존재하지_않는_출발역으로_경로를조회하면 {

            @Test
            void 조회되지_않는다() {
                Station givenSourceStation = stationC;
                Station givenTargetStation = stationB;

                Sections subSections = lineAB.getSections().getSubSections(givenSourceStation, givenTargetStation);
                Assertions.assertThat(subSections.isEmpty())
                          .isTrue();
            }
        }

        @Nested
        class When_출발역과_도착역으로_경로를조회하면 {

            @Test
            void 조회된다() {
                Station givenSourceStation = stationA;
                Station givenTargetStation = stationB;

                Sections subSections = lineAB.getSections()
                                             .getSubSections(givenSourceStation, givenTargetStation);

                Assertions.assertThat(subSections.containsSection(sectionAB))
                          .isTrue();
            }
        }

    }


}
