package nextstep.subway.unit;


import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.ui.exception.AddSectionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DistanceTest {

    @DisplayName("지하철역 사이에 새로운 구간 추가")
    @Test
    void addLineBetweenSection() {
        // given
        Distance distance = new Distance(10);
        Station 가양역 = new Station("가양역");
        Station 증미역 = new Station("증미역");
        Line line = new Line("9호선", "금색");
        Section section = new Section(line, 가양역, 증미역, 10);

        // when
        assertThatThrownBy(() -> distance.calculate(section))
                // then
                .isInstanceOf(AddSectionException.class);
    }
}
