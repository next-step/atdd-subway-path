package nextstep.subway.unit.line;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.Line;

class LineTest {

    @DisplayName("노선의 정보를 수정할 수 있다")
    @Test
    void updateLineTest() {
		// given
		Line line4 = new Line("4호선", "#00A5DE");

		// when
		line4.updateLine("00호선", "BLACK");

		// then
        assertThat(line4.getName()).isEqualTo("00호선");
        assertThat(line4.getColor()).isEqualTo("BLACK");
    }
}
