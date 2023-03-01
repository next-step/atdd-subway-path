package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LineTest {
    @Test
    void addSection() {
        // given
        Line 강남_2호선 = new Line("2호선", "green");
        Station 강남역 = new Station();
        Station 역삼역 = new Station();
        Section 강남_역삼_구간 = new Section(강남_2호선, 강남역, 역삼역, 10);

        // when
        강남_2호선.addSection(강남_역삼_구간);

        // then
        assertThat(강남_2호선.getSections()).containsExactly(강남_역삼_구간);
    }

    @Test
    void getStations() {
    }

    @Test
    void removeSection() {
    }
}
