package nextstep.subway.domain.sections.strategy;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.exception.CannotAddSectionException;
import nextstep.subway.domain.sections.Sections;
import nextstep.subway.unit.Fixtures;

class MiddleSectionAddStrategyTest {
    private Line line;
    private Sections sections;
    private final Station newStation = Fixtures.createStation(10L, "new station");

    @BeforeEach
    void setup() {
        line = new Line("신분당선", "bg-red");
        sections = new Sections();
        sections.addSection(Fixtures.createSection(1L, line, Fixtures.판교역, Fixtures.정자역, 10), line);
        sections.addSection(Fixtures.createSection(2L, line, Fixtures.정자역, Fixtures.미금역, 10), line);
    }

    @Test
    @DisplayName("상행역이 겹치고 새로운 구간의 길이가 본 구간의 길이보다 작을 경우 조건을 만족한다.")
    void meetCondition() {
        Section newSection = Fixtures.createSection(3L, line, Fixtures.판교역, newStation, 7);

        assertThat(new MiddleSectionAddStrategy().meetCondition(sections, newSection)).isTrue();
    }

    @ParameterizedTest(name = "새로운 구간의 길이가 본 구간의 길이보다 같거나 클 경우 조건을 만족하지 않는다; {0}")
    @ValueSource(ints = {10, 12})
    void distanceLongerThanLineSection(int distance) {
        Section newSection = Fixtures.createSection(3L, line, Fixtures.판교역, newStation, distance);

        assertThatThrownBy(() -> new MiddleSectionAddStrategy().meetCondition(sections, newSection))
            .isInstanceOf(CannotAddSectionException.class)
            .hasMessageContaining("새로운 구간의 길이는 본 구간의 길이보다 짧아야 합니다");
    }

    @Test
    @DisplayName("상행역이 같은 구간이 없을 경우 조건을 만족하지 않는다.")
    void notSameUpStation() {
        Section newSection = Fixtures.createSection(3L, line, newStation, Fixtures.판교역, 5);

        assertThat(new MiddleSectionAddStrategy().meetCondition(sections, newSection)).isFalse();
    }

    @Test
    @DisplayName("새로 추가해야하는 구간과 제거해야하는 기존 구간이 존재한다.")
    void findChangeableSections() {
        Section newSection = Fixtures.createSection(3L, line, Fixtures.판교역, newStation, 7);

        ChangeableSections changeableSections = new MiddleSectionAddStrategy().findChangeableSections(sections, newSection, line);
        List<Section> additionalSections = changeableSections.getAdditionalSections();
        List<Section> deprecatedSections = changeableSections.getDeprecatedSections();

        assertThat(additionalSections.get(0).getDistance()).isEqualTo(3);
        assertThat(deprecatedSections).isEqualTo(List.of(Fixtures.createSection(1L, line, Fixtures.판교역, Fixtures.정자역, 10)));
    }
}
