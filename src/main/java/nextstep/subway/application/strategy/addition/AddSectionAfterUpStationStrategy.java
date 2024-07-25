package nextstep.subway.application.strategy.addition;

import java.util.NoSuchElementException;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import nextstep.subway.domain.model.Line;
import nextstep.subway.domain.model.Section;
import nextstep.subway.domain.model.Sections;
import nextstep.subway.domain.service.SectionAdditionStrategy;

@Component
@Order(3)
public class AddSectionAfterUpStationStrategy implements SectionAdditionStrategy {
    public static final String ADD_SECTION_AFTER_UP_STATION_FAIL_MESSAGE = "상행역 뒤에";

    @Override
    public boolean canAddToExistingSection(Sections sections, Section existingSection, Section newSection) {
        return areOnlyUpStationsSame(existingSection, newSection) &&
            hasValidDistance(existingSection, newSection);
    }

    private boolean areOnlyUpStationsSame(Section existingSection, Section newSection) {
        return existingSection.hasSameUpStationWith(newSection.getUpStation()) &&
            existingSection.hasDifferentBothStationsWith(newSection.getDownStation());
    }

    @Override
    public void addSection(Line line, Section newSection) {
        Section existingSection = findExistingSectionForNewAddition(line.getSections(), newSection)
            .orElseThrow(() -> new NoSuchElementException(SECTION_NOT_FOUND_TO_ADD_NEW_ONE));
        existingSection.updateUpStation(newSection.getDownStation(), calculateNewDistance(existingSection, newSection));
        line.addSection(newSection);
    }

    @Override
    public String getFailureCaseMessage() {
        return ADD_SECTION_AFTER_UP_STATION_FAIL_MESSAGE;
    }
}
