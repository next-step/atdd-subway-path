package nextstep.subway.domain.sections.strategy;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.sections.Sections;
import nextstep.subway.unit.Fixtures;

class MiddleSectionDeleteStrategyTest {
    private Line line;
    private Sections sections;

    @BeforeEach
    void setup() {
        line = new Line("신분당선", "bg-red");
        sections = new Sections();
        sections.addSection(Fixtures.createSection(1L, line, Fixtures.판교역, Fixtures.정자역, 10), line);
        sections.addSection(Fixtures.createSection(2L, line, Fixtures.정자역, Fixtures.미금역, 10), line);
        sections.addSection(Fixtures.createSection(3L, line, Fixtures.미금역, Fixtures.광교역, 10), line);
    }

    @Test
    @DisplayName("구간의 중간역에 해당하는 경우 삭제 조건을 만족한다.")
    void meetCondition() {
        assertThat(new MiddleSectionDeleteStrategy().meetCondition(sections, Fixtures.미금역.getId())).isTrue();
    }

    @Test
    @DisplayName("구간의 최하행역에 해당하는 경우 삭제 조건을 만족하지 않는다.")
    void invalidCondition() {
        assertThat(new MiddleSectionDeleteStrategy().meetCondition(sections, Fixtures.광교역.getId())).isFalse();
    }

    @Test
    @DisplayName("해당 역을 상/하행역으로 포함한 구간이 삭제되며, 이웃한 구간의 역을 포함한 새로운 구간이 생성된다.")
    void findChangeableSections() {
        ChangeableSections changeableSections = new MiddleSectionDeleteStrategy().findChangeableSections(sections, Fixtures.미금역.getId(), line);

        assertAll(
            () -> assertThat(changeableSections.getDeprecatedSections()).hasSize(2),
            () -> assertThat(changeableSections.getDeprecatedSections()).contains(
                Fixtures.createSection(2L, line, Fixtures.정자역, Fixtures.미금역, 10),
                Fixtures.createSection(3L, line, Fixtures.미금역, Fixtures.광교역, 10)),
            () -> assertThat(changeableSections.getAdditionalSections()).hasSize(1),
            () -> assertThat(changeableSections.getAdditionalSections().get(0).getUpStation()).isEqualTo(Fixtures.정자역),
            () -> assertThat(changeableSections.getAdditionalSections().get(0).getDownStation()).isEqualTo(Fixtures.광교역),
            () -> assertThat(changeableSections.getAdditionalSections().get(0).getDistance()).isEqualTo(20)
        );
    }
}
