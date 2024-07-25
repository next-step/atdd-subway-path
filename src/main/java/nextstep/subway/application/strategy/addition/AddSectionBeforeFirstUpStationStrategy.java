package nextstep.subway.application.strategy.addition;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import nextstep.subway.domain.model.Line;
import nextstep.subway.domain.model.Section;
import nextstep.subway.domain.model.Sections;
import nextstep.subway.domain.service.SectionAdditionStrategy;

@Component
@Order(2)
public class AddSectionBeforeFirstUpStationStrategy implements SectionAdditionStrategy {
    public static final String ADD_SECTION_BEFORE_FIRST_UP_STATION_FAIL_MESSAGE = "첫 번째 상행역 앞에";

    @Override
    public boolean canAddToExistingSection(Sections sections, Section existingSection, Section newSection) {
        return sections.equalsWithFirstSection(existingSection) &&
            existingSection.hasSameUpStationWith(newSection.getDownStation()) &&
            existingSection.hasDifferentBothStationsWith(newSection.getUpStation());
    }

    @Override
    public void addSection(Line line, Section newSection) {
        line.addSection(newSection);
    }

    @Override
    public String getFailureCaseMessage() {
        return ADD_SECTION_BEFORE_FIRST_UP_STATION_FAIL_MESSAGE;
    }
}


