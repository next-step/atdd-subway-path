package nextstep.subway.unit;

import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.Sections;
import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.station.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionsTest {

    @Test
    @DisplayName("Section의 Station 출력 (순서 : 상행->하행)")
    void test1() {
        Station 신논역 = new Station(1L, "신논역");
        Station 신사역 = new Station(2L, "신사역");
        Station 강남역 = new Station(3L, "강남역");
        Station 판교역 = new Station(4L, "판교역");
        Station 논현역 = new Station(5L, "논현역");
        Station 삼성역 = new Station(6L, "삼성역");

        Line line = new Line(1L, "line", "color");

        Section section1 = new Section(1L, line, 신논역, 신사역, 5);
        Section section2 = new Section(2L, line, 신사역, 강남역, 5);
        Section section3 = new Section(3L, line, 강남역, 판교역, 5);
        Section section4 = new Section(4L, line, 판교역, 논현역, 5);
        Section section5 = new Section(5L, line, 논현역, 삼성역, 5);

        Sections sections = new Sections();
        sections.addSection(section1);
        sections.addSection(section2);
        sections.addSection(section3);
        sections.addSection(section4);
        sections.addSection(section5);

        List<Station> stations = sections.getStations();
        assertThat(stations)
                .extracting(Station::getId)
                .containsExactly(1L, 2L, 3L, 4L, 5L, 6L);
    }


}
