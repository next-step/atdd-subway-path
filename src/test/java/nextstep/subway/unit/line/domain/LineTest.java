package nextstep.subway.unit.line.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.domain.Line;

@DisplayName("Line 단위 테스트")
class LineTest {
    private Line line;

    @BeforeEach
    void setUp() {
        this.line = new Line(1L, "초기 노선", "bg-red-500");
    }

    @DisplayName("구간 정보 수정")
    @Test
    void edit() {
        String changedName = "변경된 이름";
        String changedColor = "bg-red-500";

        line.edit(changedName, changedColor);

        assertThat(line.getName()).isEqualTo(changedName);
        assertThat(line.getColor()).isEqualTo(changedColor);
    }
}
