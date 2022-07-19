package nextstep.subway.domain.sectioncondition.add;

import nextstep.subway.applicaion.dto.AddSectionRequest;
import nextstep.subway.domain.Line;
import org.junit.jupiter.api.Test;

import static nextstep.subway.utils.LineTestSources.section;
import static nextstep.subway.utils.StationTestSources.station;
import static org.assertj.core.api.Assertions.assertThat;

class EmptySectionsConditionTest {

    @Test
    void isSatisfiedBy_Empty() {
        // given
        final EmptySectionsAddCondition emptySectionsCondition = new EmptySectionsAddCondition();

        final Line line = new Line();

        // when
        final boolean result = emptySectionsCondition.matches(line, new AddSectionRequest(station(1), station(2), 10));

        // then
        assertThat(result).isTrue();
    }

    @Test
    void isSatisfiedBy_NotEmpty() {
        // given
        final EmptySectionsAddCondition emptySectionsCondition = new EmptySectionsAddCondition();

        final Line line = new Line();
        line.addSection(section(station(1), station(2)));

        // when
        final boolean result = emptySectionsCondition.matches(line, new AddSectionRequest(station(2), station(3), 10));

        // then
        assertThat(result).isFalse();
    }

}