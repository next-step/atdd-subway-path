package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    private Station upStation;
    private Station downStation;
    private Line line;

    @BeforeEach
    void setUp() {
        upStation = new Station("upStation");
        downStation = new Station("downStation");
        line = new Line("color", "name");
        line.addSection(new Section(line, upStation, downStation, 10));
    }

    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우")
    @Test
    void addSection() {
        // given
        final Station extraStation = new Station("extraStation");
        final Section section = new Section(line, downStation, extraStation, 1);

        // when
        line.addSection(section);

        // then
        assertThat(line.getStations()).containsExactly(upStation, downStation, extraStation);
    }

    @DisplayName("노선에 속해있는 역 목록 조회")
    @Test
    void getStations() {
        assertThat(line.getStations()).containsExactly(upStation, downStation);
    }

    @DisplayName("구간이 목록에서 마지막 역 삭제")
    @Test
    void removeSection() {
        // given
        final Station extraStation = new Station("extraStation");
        final Section section = new Section(line, downStation, extraStation, 1);
        line.addSection(section);

        // when
        line.removeSection(extraStation);

        // then
        assertThat(line.getStations()).containsExactly(upStation, downStation);
    }
}
