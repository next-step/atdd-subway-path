package nextstep.subway.unit;


import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Line;
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

        // when
        assertThatThrownBy(() -> distance.calculate(10))
                // then
                .isInstanceOf(AddSectionException.class)
                .hasMessage("새로 추가되는 구간 거리는 기존 구간의 거리 이상일 수 없습니다. 기존 구간 거리 = 10, 신규 구간 거리 = 10");
    }
}
