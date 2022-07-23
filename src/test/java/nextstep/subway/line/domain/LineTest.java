package nextstep.subway.line.domain;

import nextstep.subway.line.domain.exception.CannotAddSectionException;
import nextstep.subway.line.domain.exception.CannotDeleteSectionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineTest {

    @Test
    void 구간_추가() {
        // given
        Long upStationId = 1L;
        Long downStationId = 2L;

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

    @DisplayName("기존 구간 사이에 추가할 구간의 길이가 기존 구간의 길이보다 크거나 같을 수 없다.")
    @Test
    void 구간_추가_예외1() {
        // given
        Line line = new Line("신분당선", "red");
        Long firstStationId = 1L;
        Long secondStationId = 2L;
        Long thirdStationId = 3L;
        int distance = 6;

        line.addSection(firstStationId, thirdStationId, distance);

        // when + then
        assertThatThrownBy(() -> line.addSection(secondStationId, thirdStationId, distance))
                .isInstanceOf(CannotAddSectionException.class);

        assertThatThrownBy(() -> line.addSection(firstStationId, secondStationId, distance))
                .isInstanceOf(CannotAddSectionException.class);
    }

    @Test
    void 구간_삭제() {
        // given
        Long upStationId = 1L;
        Long downStationId = 2L;

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
        Long upStationId = 1L;
        Long downStationId = 2L;

        Line line = new Line("신분당선", "red");
        line.addSection(upStationId, downStationId, 6);

        // when + then
        assertThatThrownBy(() -> line.removeSection(upStationId))
                .isInstanceOf(CannotDeleteSectionException.class);
    }
}
