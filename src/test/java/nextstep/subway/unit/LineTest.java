package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    private int distance;
    private Line line;
    private Station upStation;
    private Station downStation;

    @BeforeEach
    void setUp() {
        this.distance = 10;
        this.line = new Line("2호선", "bg-red-500");
        this.upStation = new Station("강남역");
        this.downStation = new Station("역삼역");
    }

    @DisplayName("노선에 구간을 추가한다.")
    @Test
    void addSection() {
        Section section = new Section(line, upStation, downStation, distance);

        line.addSection(section);

        assertThat(line.getSections()).containsExactly(section);
    }

    @Test
    void getStations() {
    }

    @Test
    void removeSection() {
    }
}
