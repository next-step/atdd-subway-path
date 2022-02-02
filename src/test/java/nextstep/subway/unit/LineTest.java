package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class LineTest {
    private static final String FIRST_NAME = "1호선";
    private static final String DEFAULT_COLOR = "bg-red-600";

    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우")
    @Test
    void addSection() {
        // given
        Line line = new Line(FIRST_NAME, DEFAULT_COLOR);
        Station upStation = new Station("강남역");
        Station downStation = new Station("역삼역");
        Section section = new Section(line, upStation, downStation, 5);

        // when
        line.addSection(section);

        // then
        assertThat(line.getSections()).hasSize(1);
    }

    @DisplayName("노선에 속해있는 역 목록 조회")
    @Test
    void getStations() {
        // given
        Line line = new Line(FIRST_NAME, DEFAULT_COLOR);
        Station upStation = new Station("강남역");
        Station downStation = new Station("역삼역");
        Section section = new Section(line, upStation, downStation, 5);

        line.addSection(section);

        // when
        List<Station> stations = line.getStations();

        // then
        assertThat(stations).hasSize(2);
    }

    @DisplayName("구간이 목록에서 마지막 역 삭제")
    @Test
    void removeSection() {
        // given
        Line line = new Line(FIRST_NAME, DEFAULT_COLOR);
        Station upStation = new Station("강남역");
        Station downStation = new Station("역삼역");
        Section section = new Section(line, upStation, downStation, 5);

        line.addSection(section);

        // then
        line.deleteSection(downStation);

        // when
        assertThat(line.getSections()).hasSize(0);
    }
}
