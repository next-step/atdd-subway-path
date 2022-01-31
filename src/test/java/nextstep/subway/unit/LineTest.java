package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {
    private final Line line = new Line();
    private final Station upStation = new Station("강남역");
    private final Station downStation = new Station("잠실역");
    private final int distance = 10;

    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우")
    @Test
    void addSection() {
        // when
        line.addSection(upStation, downStation, distance);

        // then
        assertThat(line.getSections().get(0).getUpStation()).isEqualTo(upStation);
        assertThat(line.getSections().get(0).getDownStation()).isEqualTo(downStation);
        assertThat(line.getSections().get(0).getDistance()).isEqualTo(distance);
    }

    @DisplayName("노선에 속해있는 역 목록 조회")
    @Test
    void getStations() {
        // given
        line.addSection(upStation, downStation, distance);

        // when
        List<Station> stations = line.getStations();

        // then
        assertThat(stations).containsExactly(upStation, downStation);
    }

    @DisplayName("구간이 목록에서 마지막 역 삭제")
    @Test
    void removeSection() {
        // given
        line.addSection(upStation, downStation, distance);

        // when
        line.deleteSection(downStation);

        // then
        assertThat(line.getStations()).isEmpty();
        assertThat(line.getSections()).isEmpty();
    }
}
