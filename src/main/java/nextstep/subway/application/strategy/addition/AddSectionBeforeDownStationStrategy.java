package nextstep.subway.application.strategy.addition;

import java.util.NoSuchElementException;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import nextstep.subway.domain.model.Line;
import nextstep.subway.domain.model.Section;
import nextstep.subway.domain.model.Sections;
import nextstep.subway.domain.service.SectionAdditionStrategy;

@Component
@Order(4)
public class AddSectionBeforeDownStationStrategy implements SectionAdditionStrategy {
    public static final String ADD_SECTION_BEFORE_DOWN_STATION_FAIL_MESSAGE = "하행역 앞에";

    @Override
    public boolean canAddToExistingSection(Sections sections, Section existingSection, Section newSection) {
        return areOnlyDownStationSame(existingSection, newSection) &&
            hasValidDistance(existingSection, newSection);
    }

    private boolean areOnlyDownStationSame(Section existingSection, Section newSection) {
        return existingSection.hasSameDownStationWith(newSection.getDownStation()) &&
            existingSection.hasDifferentBothStationsWith(newSection.getUpStation());
    }

    @Override
    public void addSection(Line line, Section newSection) {
        Section existingSection = findExistingSectionForNewAddition(line.getSections(), newSection)
            .orElseThrow(() -> new NoSuchElementException(SECTION_NOT_FOUND_TO_ADD_NEW_ONE));
        existingSection.updateDownStation(newSection.getUpStation(), calculateNewDistance(existingSection, newSection));
        line.addSection(newSection);
    }

    @Override
    public String getFailureCaseMessage() {
        return ADD_SECTION_BEFORE_DOWN_STATION_FAIL_MESSAGE;
    }
}

