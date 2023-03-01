package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {
    private Line 강남_2호선;
    private Station 강남역;
    private Station 역삼역;
    private Station 삼성역;

    @BeforeEach
    void setUp() {
        강남_2호선 = new Line("2호선", "green");
        강남역 = new Station();
        역삼역 = new Station();
        삼성역 = new Station();
    }

    @Test
    void addSection() {
        // given
        Section 강남_역삼_구간 = new Section(강남_2호선, 강남역, 역삼역, 10);

        // when
        강남_2호선.addSection(강남_역삼_구간);

        // then
        assertThat(강남_2호선.getSections()).containsExactly(강남_역삼_구간);
    }

    @Test
    void getStations() {
        // given
        Section 강남_역삼_구간 = new Section(강남_2호선, 강남역, 역삼역, 10);
        Section 역삼_삼성_구간 = new Section(강남_2호선, 역삼역, 삼성역, 12);
        강남_2호선.addSection(강남_역삼_구간);
        강남_2호선.addSection(역삼_삼성_구간);

        // when
        List<Station> stations = 강남_2호선.getStations();

        // then
        assertThat(stations).containsExactly(강남역, 역삼역, 삼성역);
    }

    @Test
    void removeSection() {
    }
}
