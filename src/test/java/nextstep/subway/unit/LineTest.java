package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {
    @Test
    void addSection() {
        final Line line = new Line();
        final Section section = new Section(line, new Station("강남역"), new Station("역삼역"), 2);

        line.addSection(section);

        assertThat(line.getSections()).contains(section);
    }

    @Test
    void getStations() {
        final Line line = new Line("2호선", "bg-green");
        final Station 강남역 = new Station("강남역");
        final Station 역삼역 = new Station("역삼역");
        final Station 선릉역 = new Station("선릉역");

        final Section 강남_역삼_구간 = new Section(line, 강남역, 역삼역, 2);
        final Section 역삼_선릉_구간 = new Section(line, 역삼역, 선릉역, 3);

        line.addSection(강남_역삼_구간);
        line.addSection(역삼_선릉_구간);

        assertThat(line.getSections()).contains(강남_역삼_구간, 역삼_선릉_구간);
    }

    @Test
    void removeSection() {
        final Line line = new Line("2호선", "bg-green");
        final Station 강남역 = new Station("강남역");
        final Station 역삼역 = new Station("역삼역");
        final Station 선릉역 = new Station("선릉역");

        final Section 강남_역삼_구간 = new Section(line, 강남역, 역삼역, 2);
        final Section 역삼_선릉_구간 = new Section(line, 역삼역, 선릉역, 3);
        line.addSection(강남_역삼_구간);
        line.addSection(역삼_선릉_구간);

        line.deleteSection(역삼_선릉_구간);

        assertThat(line.getSections()).doesNotContain(역삼_선릉_구간);
    }
}
