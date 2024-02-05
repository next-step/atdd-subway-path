package nextstep.subway.unit;

import nextstep.subway.entity.Line;
import nextstep.subway.entity.Section;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

class LineTest {
    private static final Long 첫번째_역 = 1L;
    private static final Long 두번째_역 = 2L;
    private static final Long 세번째_역 = 3L;
    private Line 노선;

    @BeforeEach
    void setup() {
        노선 = new Line("노선", "파란색", 첫번째_역, 두번째_역, 10);
    }

    @Test
    @DisplayName("지하철의 노선의 구간을 등록한다.")
    void addSection() {
        노선.addSection(new Section(노선, 두번째_역, 세번째_역, 1));

        assertThat(노선.getSections().getSize()).isEqualTo(2);
    }

    @Test
    @DisplayName("지하철 노선의 구간을 제거한다.")
    void deleteSection() {
        노선.addSection(new Section(노선, 두번째_역, 세번째_역, 1));
        노선.deleteSection(세번째_역);

        assertFalse(노선.hasStation(세번째_역));
    }
}
