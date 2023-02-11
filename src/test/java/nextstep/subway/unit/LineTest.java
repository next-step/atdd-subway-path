package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    // TODO: beforeEach

    @DisplayName("구간 추가 - 역 사이에 새로운 역을 등록할 경우 - 새로운 구간의 상행역과 기존 구간의 상행역이 같은 경우")
    @Test
    void addSection_1() {
        Station 서울역 = new Station("서울역");
        Station 시청역 = new Station("시청역");
        Station 종각역 = new Station("종각역");

        Line line = new Line("1호선", "남색", 서울역, 시청역, 10);

        line.addSection(서울역, 종각역, 3);

        assertThat(line.getStations()).containsExactly(서울역, 종각역, 시청역);
    }

    @DisplayName("구간 추가 - 역 사이에 새로운 역을 등록할 경우 - 새로운 구간의 하행역과 기존 구간의 하행역이 같은 경우")
    @Test
    void addSection_2() {
        Station 서울역 = new Station("서울역");
        Station 시청역 = new Station("시청역");
        Station 종각역 = new Station("종각역");

        Line line = new Line("1호선", "남색", 서울역, 시청역, 10);

        line.addSection(종각역, 시청역, 3);

        assertThat(line.getStations()).containsExactly(서울역, 종각역, 시청역);
    }

    @DisplayName("구간 추가 - 새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addSection_3() {
        Station 서울역 = new Station("서울역");
        Station 시청역 = new Station("시청역");
        Station 종각역 = new Station("종각역");

        Line line = new Line("1호선", "남색", 서울역, 시청역, 10);

        line.addSection(종각역, 서울역, 3);

        assertThat(line.getStations()).containsExactly(종각역, 서울역, 시청역);
    }
}
