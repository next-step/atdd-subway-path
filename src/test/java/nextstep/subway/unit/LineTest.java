package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.*;

@DisplayName("노선 관리")
class LineTest {
    private static final String FIRST_NAME = "1호선";
    private static final String DEFAULT_COLOR = "bg-red-600";

    @DisplayName("노선은 이름과 색상으로 생성된다.")
    @Test
    void createLine() {
        Line line = new Line(FIRST_NAME, DEFAULT_COLOR);

        assertThat(line).isNotNull();
    }

    @DisplayName("노선의 정보를 수정한다")
    @CsvSource(value = {"2호선,bg-blue-600"}, delimiter = ',')
    @ParameterizedTest
    void updateLine(String name, String color) {
        Line line = new Line(FIRST_NAME, DEFAULT_COLOR);

        line.updateLine(name, color);

        assertThat(line.getColor()).isEqualTo(color);
        assertThat(line.getName()).isEqualTo(name);
    }
}
