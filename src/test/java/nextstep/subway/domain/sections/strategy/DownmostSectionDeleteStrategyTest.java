package nextstep.subway.domain.sections.strategy;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.sections.Sections;
import nextstep.subway.unit.Fixtures;

class DownmostSectionDeleteStrategyTest {
    private Line line;
    private Sections sections;

    @BeforeEach
    void setup() {
        line = new Line("신분당선", "bg-red");
        sections = new Sections();
        sections.addSection(Fixtures.createSection(1L, line, Fixtures.판교역, Fixtures.정자역, 10), line);
        sections.addSection(Fixtures.createSection(2L, line, Fixtures.정자역, Fixtures.미금역, 10), line);
    }

    @Test
    @DisplayName("구간의 최하행역에 대해서 삭제할 조건을 만족한다.")
    void meetCondition() {
        assertThat(new DownmostSectionDeleteStrategy().isValidCondition(sections, Fixtures.미금역.getId())).isTrue();
    }

    @Test
    @DisplayName("구간의 최하행역이 아닐 경우 삭제할 조건을 만족하지 못한다.")
    void invalidCondition() {
        assertThat(new DownmostSectionDeleteStrategy().isValidCondition(sections, Fixtures.정자역.getId())).isFalse();
    }

    @Test
    @DisplayName("변경될 구간에 대하여 최하행역을 포함한 구간을 지울 구간으로 포함한다.")
    void findChangeableSections() {
        ChangeableSections changeableSections = new DownmostSectionDeleteStrategy().findChangeableSections(sections, Fixtures.미금역.getId(), line);

        assertAll(
            () -> assertThat(changeableSections.getDeprecatedSections()).hasSize(1),
            () -> assertThat(changeableSections.getDeprecatedSections()).contains(Fixtures.createSection(2L, line, Fixtures.정자역, Fixtures.미금역, 10)),
            () -> assertThat(changeableSections.getAdditionalSections()).isEmpty()
        );
    }
}
