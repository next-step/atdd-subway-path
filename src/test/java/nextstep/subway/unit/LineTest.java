package nextstep.subway.unit;

import java.util.List;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("구간 단위테스트")
class LineTest {

    private Station 봉천역;
    private Station 서울대입구역;
    private Line line;

    @BeforeEach
    void setup() {
        봉천역 = new Station(1L, "봉천역");
        서울대입구역 = new Station(2L, "서울대입구역");
        line = new Line("2호선", "#00ff00");
    }

    @Test
    @DisplayName("지하철 노선에 신규 구간을 추가한다.")
    void addSection() {
        // given
        Section 구간 = new Section(line, 봉천역, 서울대입구역, 10);

        // when && then
        line.addSection(구간);
    }

    @Test
    @DisplayName("지하철 노선에 포함된 역 목록을 가져온다.")
    void getStations() {

        // given
        Section 구간 = new Section(line, 봉천역, 서울대입구역, 10);
        line.addSection(구간);

        // when
        List<Station> stations = line.getStations();

        Assertions.assertThat(stations)
            .asList()
            .containsExactly(봉천역, 서울대입구역);
    }

    @Test
    @DisplayName("지하철 노선에 포함된 구간을 제거한다.")
    void removeSection() {
        // given
        Section 구간 = new Section(line, 봉천역, 서울대입구역, 10);
        line.addSection(구간);

        // when
        line.removeSection(서울대입구역);

        // then
        Assertions.assertThat(line.getStations())
            .asList()
            .doesNotContain(서울대입구역);
    }
}
