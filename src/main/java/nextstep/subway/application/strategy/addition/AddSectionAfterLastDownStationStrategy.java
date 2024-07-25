package nextstep.subway.application.strategy.addition;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import nextstep.subway.domain.model.Line;
import nextstep.subway.domain.model.Section;
import nextstep.subway.domain.model.Sections;
import nextstep.subway.domain.service.SectionAdditionStrategy;

@Component
@Order(5)
public class AddSectionAfterLastDownStationStrategy implements SectionAdditionStrategy {
    public static final String ADD_SECTION_AFTER_LAST_DOWN_STATION_FAIL_MESSAGE = "마지막 하행역 뒤에";

    @Override
    public boolean canAddToExistingSection(Sections sections, Section existingSection, Section newSection) {
        return sections.equalsWithLastSection(existingSection) &&
            existingSection.hasSameDownStationWith(newSection.getUpStation()) &&
            existingSection.hasDifferentBothStationsWith(newSection.getDownStation());
    }

    @Override
    public void addSection(Line line, Section newSection) {
        line.addSection(newSection);
    }

    @Override
    public String getFailureCaseMessage() {
        return ADD_SECTION_AFTER_LAST_DOWN_STATION_FAIL_MESSAGE;
    }
}


