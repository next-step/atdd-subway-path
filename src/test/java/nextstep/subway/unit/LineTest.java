package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

class LineTest {

    @DisplayName("노선에 구간을 추가할 수 있다")
    @Test
    void addSection() {
        Line line = new Line("4호선", "#00A5DE");

        Station upStation = new Station("금정");
        Station downStation = new Station("오이도");
        Section newSection = new Section(line, upStation, downStation, 13);

        Section addedSection = line.addSection(newSection);
        assertThat(addedSection).isEqualTo(newSection);
    }

    @Test
    void getStations() {
    }

    @Test
    void removeSection() {
    }
}
