package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.exception.CannotDeleteSectionException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class LineTest {

    @Test
    void 구간_추가() {
        // given
        long upStationId = 1L;
        long downStationId = 2L;

        Line line = new Line("신분당선", "red");

        // when
        line.addSection(upStationId, downStationId, 6);

        // then
        List<Section> sections = line.getSections();
        assertThat(sections).hasSize(1);

        Section addedSection = sections.get(0);
        assertThat(addedSection.getUpStationId()).isEqualTo(upStationId);
        assertThat(addedSection.getDownStationId()).isEqualTo(downStationId);
    }

    @Test
    void 구간_삭제() {
        // given
        long upStationId = 1L;
        long downStationId = 2L;

        Line line = new Line("신분당선", "red");
        line.addSection(upStationId, downStationId, 6);

        // when
        line.removeSection(downStationId);

        // then
        assertThat(line.getSections()).isEmpty();
    }

    @Test
    void 구간_삭제_종점이_아니면_예외() {
        // given
        long upStationId = 1L;
        long downStationId = 2L;

        Line line = new Line("신분당선", "red");
        line.addSection(upStationId, downStationId, 6);

        // when + then
        assertThatThrownBy(() -> line.removeSection(upStationId))
                .isInstanceOf(CannotDeleteSectionException.class);
    }
}
