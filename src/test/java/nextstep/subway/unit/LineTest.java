package nextstep.subway.unit;

import static nextstep.subway.utils.MockString.봉천역;
import static nextstep.subway.utils.MockString.서울대입구역;
import static nextstep.subway.utils.MockString.신림역;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

class LineTest {
    @Test
    void addSection() {
        Line line = new Line("2호선", "초록색");
        Section section = createTestSection(line);
        line.addSection(section);

        assertThat(line.getSections().size()).isEqualTo(1);
    }

    @Test
    void getStations() {
        Station 서울대 = new Station(서울대입구역);
        Station 봉천 = new Station(봉천역);
        Station 신림 = new Station(신림역);
        Line line = new Line("2호선", "초록색");

        line.addSection(createTestSection(line, 서울대, 봉천,10));
        line.addSection(createTestSection(line, 봉천, 신림, 10));

        assertThat(line.getStations().size()).isEqualTo(3);
    }

    @Test
    void removeSection() {
        Line line = new Line("2호선", "초록색");
        Section section = createTestSection(line);
        line.addSection(section);

        line.removeSections(section);
        assertThat(line.getSections().size()).isEqualTo(0);
    }

    @Test
    void addDuplicateSection() {
        Line line = new Line("2호선", "초록색");
        Section section = createTestSection(line);
        line.addSection(section);

        assertThrows(DataIntegrityViolationException.class, () -> line.addSection(section));
    }

    @Test
    void 중간_구간_추가시_기존_구간보다_길면_에러() {
        Line line = new Line("2호선", "초록색");
        Station 서울대 = new Station(서울대입구역);
        Station 봉천 = new Station(봉천역);
        Station 신림 = new Station(신림역);
        line.addSection(createTestSection(line, 서울대, 신림,10));
        Section 신규_구간 = createTestSection(line, 봉천, 신림, 20);

        assertThrows(DataIntegrityViolationException.class, () -> line.addSection(신규_구간));
    }

    private Section createTestSection(Line line) {
        Station upStation = new Station(서울대입구역);
        Station downStation = new Station(봉천역);
        return new Section(line, upStation, downStation,10);
    }

    private Section createTestSection(Line line, Station upStation, Station downStation, int distance) {
        return new Section(line, upStation, downStation,distance);
    }
}
