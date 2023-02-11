package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("구간 단위 테스트")
class LineTest {

    private int distance;
    private Line line;
    private Station upStation;
    private Station downStation;
    private Station removeStation;

    @BeforeEach
    void setUp() {
        this.distance = 10;
        this.line = new Line("2호선", "bg-red-500");
        this.upStation = new Station("강남역");
        this.downStation = new Station("역삼역");
        this.removeStation = new Station("선릉역");
    }

    @DisplayName("노선에 구간을 추가한다.")
    @Test
    void addSection() {
        Section section = new Section(line, upStation, downStation, distance);

        line.addSection(section);

        assertThat(line.getSections()).containsExactly(section);
    }

    @DisplayName("노선의 지하철 역 목록을 반환한다.")
    @Test
    void getStations() {
        line.addSection(new Section(line, upStation, downStation, distance));

        List<Station> stations = line.getStations();

        assertThat(stations).hasSize(2).containsExactly(upStation, downStation);
    }

    @DisplayName("노선에 구간을 제거한다.")
    @Test
    void removeSection() {
        Section expected = new Section(line, upStation, downStation, distance);
        line.addSection(expected);
        Section removeSection = new Section(line, downStation, removeStation, distance);
        line.addSection(removeSection);

        line.removeSection(removeStation);

        assertThat(line.getSections()).hasSize(1).containsExactly(expected);
    }
}
