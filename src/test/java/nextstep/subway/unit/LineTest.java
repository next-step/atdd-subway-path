package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    @DisplayName("새로운 구간을 정상적으로 추가했다")
    @Test
    void addSection() {
        // given
        Station upStation = new Station("신논현역");
        Station downStation = new Station("언주역");
        Station newStation = new Station("선정릉역");
        Line line = new Line("9호선", "bg-brown-600");
        line.addSection(new Section(line, upStation, downStation, 5));

        // when
        line.addSection(new Section(line, downStation, newStation, 6));

        // then
        assertThat(line.getStations()).containsExactly(upStation, downStation, newStation);
    }

    @DisplayName("노선의 역 목록이 정상 조회되었습니다.")
    @Test
    void getStations() {
        // given
        Station upStation = new Station("신논현역");
        Station downStation = new Station("언주역");
        Line line = new Line("9호선", "bg-brown-600");
        line.addSection(new Section(line, upStation, downStation, 5));

        // when
        List<Station> stations = line.getStations();

        // then
        assertThat(line.getStations()).containsExactly(upStation, downStation);
    }

    @Test
    void removeSection() {
    }
}
