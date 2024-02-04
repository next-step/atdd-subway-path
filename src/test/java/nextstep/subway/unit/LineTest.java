package nextstep.subway.unit;

import nextstep.subway.domain.entity.Line;
import nextstep.subway.domain.entity.Section;
import nextstep.subway.domain.entity.Station;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class LineTest {
    @Test
    void addSection() {
        // line 인스턴스를 만들고
        // addSection 을 호출했을 때
        Line line = new Line("2호선", "green");

        Station upStation = new Station("강남역");
        Station downStation = new Station("역삼역");
        Station newStation = new Station("역삼역");

        int distance = 10;

        line.addSection(new Section(line, upStation, downStation, distance));
        line.addSection(new Section(line, downStation, newStation, distance));

        assertAll(
                () -> assertThat(line.getSections()).hasSize(2),
                () -> assertThat(line.getDistance()).isEqualTo(20)
        );

    }

    @Test
    void getStations() {
    }

    @Test
    void removeSection() {
        // line 인스턴스를 만들고
        // deleteSection 을 호출했을 때
        Line line = new Line("2호선", "green");

        Station upStation = new Station("강남역");
        Station downStation = new Station("역삼역");
        Station newStation = new Station("역삼역");

        int distance = 10;
        line.addSection(new Section(line, upStation, downStation, distance));
        line.addSection(new Section(line, downStation, newStation, distance));

        List<Section> sections = line.getSections();
        Section section = sections.get(sections.size() - 1);
        line.deleteSection(section);


        assertAll(
                () -> assertThat(line.getSections()).hasSize(1),
                () -> assertThat(line.getDistance()).isEqualTo(10)
        );
    }
}
