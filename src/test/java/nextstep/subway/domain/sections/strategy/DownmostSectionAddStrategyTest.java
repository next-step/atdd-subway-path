package nextstep.subway.domain.sections.strategy;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.sections.Sections;
import nextstep.subway.unit.Fixtures;

class DownmostSectionAddStrategyTest {

    private Line line;
    private Sections sections;
    private final Station newStation = Fixtures.createStation(10L, "new station");

    @BeforeEach
    void setup() {
        line = new Line("신분당선", "bg-red");
        sections = new Sections();
        sections.addSection(Section.createFixture(1L, line, Fixtures.판교역, Fixtures.정자역, 10), line);
        sections.addSection(Section.createFixture(2L, line, Fixtures.정자역, Fixtures.미금역, 10), line);
    }

    @Test
    @DisplayName("하행역에 이어서 구간을 추가할 경우 조건을 만족한다.")
    void meetCondition() {
        Section newSection = Section.createFixture(3L, line, Fixtures.미금역, newStation, 15);

        assertThat(new DownmostSectionAddStrategy().meetCondition(sections, newSection)).isTrue();
    }

    @Test
    @DisplayName("하행역에 이어서 구간을 추가하지 않을 경우 조건을 만족하지 않는다.")
    void invalidCondition() {
        Section newSection = Section.createFixture(3L, line, newStation, Fixtures.미금역, 15);

        assertThat(new DownmostSectionAddStrategy().meetCondition(sections, newSection)).isFalse();
    }

    @Test
    @DisplayName("별도로 추가하거나 삭제해야할 구간은 존재하지 않는다.")
    void findChangeableSections() {
        Section newSection = Section.createFixture(3L, line, Fixtures.미금역, newStation, 15);

        ChangeableSections changeableSections = new DownmostSectionAddStrategy().findChangeableSections(sections, newSection, line);

        assertThat(changeableSections.getAdditionalSections()).isEmpty();
        assertThat(changeableSections.getDeprecatedSections()).isEmpty();
    }
}
