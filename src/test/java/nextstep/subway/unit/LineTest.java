package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 단위 테스트")
class LineTest {
    @Test
    @DisplayName("지하철 노선에 구간을 추가한다.")
    void addSection() {
        //given
        Line line = new Line("2호선", "green");
        Station upStation = new Station("강남역");
        Station downStation = new Station("역삼역");

        //when
        line.addSection(upStation, downStation, 10);

        //then
        assertThat(line.getAllStations()).containsExactly(upStation, downStation);
    }

    @Test
    void getStations() {
    }

    @Test
    void removeSection() {
    }
}
