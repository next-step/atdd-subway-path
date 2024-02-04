package nextstep.subway.line.unit;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    private Line line;

    @BeforeEach
    void setup() {
        line = Line.of("수인분당선", "yellow");
    }

    @DisplayName("상행역, 하행역, 거리가 주어지면 해당 노선에 구간이 추가된다")
    @Test
    void addSection() {
        line.addSection(new Station("수원역"), new Station("고색역"), 10);

        assertThat(line.getAllSections().isEmpty()).isFalse();
        assertThat(line.getAllSections().size()).isEqualTo(1);
    }

    @DisplayName("해당 노선의 모든 역을 확인할 수 있다")
    @Test
    void getStations() {
        line.addSection(new Station("수원역"), new Station("고색역"), 10);

        assertThat(line.getStations().isEmpty()).isFalse();
        assertThat(line.getStations().stream().map(Station::getName)).contains("수원역", "고색역");
    }

    @DisplayName("주어진 구간을 제거할 수 있다")
    @Test
    void removeSection() {
        Station downStation = new Station("고색역");
        line.addSection(new Station("수원역"), downStation, 10);
        line.addSection(downStation, new Station("오목천역"), 10);

        line.removeSection(line.getSections().getLastSection());

        assertThat(line.getStations().isEmpty()).isFalse();
        assertThat(line.getAllSections().size()).isEqualTo(1);
        assertThat(line.getStations().stream().map(Station::getName)).isNotIn("오목천역");
    }
}
