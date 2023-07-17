package nextstep.subway.unit;

import nextstep.subway.line.entity.Line;
import nextstep.subway.line.entity.Section;
import nextstep.subway.station.entity.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {

    Station 강남역;
    Station 역삼역;
    Station 선릉역;
    Line 이호선;

    @BeforeEach
    void setUp() {
        강남역 = new Station(1L, "강남역");
        역삼역 = new Station(2L, "역삼역");
        선릉역 = new Station(3L, "선릉역");
        이호선 = new Line("이호선", "Green", 10, 강남역, 역삼역);
    }

    @Test
    void addSection() {
        // when
        이호선.addSection(new Section(이호선, 역삼역, 선릉역, 20));

        // then
        assertThat(이호선.getStations()).contains(선릉역);
    }

    @Test
    void getStations() {
        // when
        List<Station> stations = 이호선.getStations();

        // then
        assertThat(stations).containsExactly(강남역, 역삼역);
    }

    @Test
    void removeSection() {
        // given
        이호선.addSection(new Section(이호선, 역삼역, 선릉역, 20));

        // when
        이호선.removeSection(선릉역);

        // then
        assertThat(이호선.getStations()).doesNotContain(선릉역);
    }
}
