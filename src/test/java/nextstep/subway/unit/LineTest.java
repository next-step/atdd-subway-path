package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;

class LineTest {
    @Test
    @DisplayName("라인 상태 변경 테스트")
    void changeLine() {
        //given
        Line line = new Line("2호선", "green");

        //when
        line.changeState("4호선", "sky");

        //then
        assertThat(line.getName()).isEqualTo("4호선");
        assertThat(line.getColor()).isEqualTo("sky");
    }
}
