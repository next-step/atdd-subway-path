package nextstep.subway.application.strategy.addition;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import nextstep.subway.domain.model.Line;
import nextstep.subway.domain.model.Section;
import nextstep.subway.domain.service.SectionAdditionStrategy;

@Component
@Order(1)
public class AddSectionToEmptySectionsStrategy implements SectionAdditionStrategy {
    public static final String ADD_SECTION_TO_EMPTY_SECTIONS_FAIL_MESSAGE = "빈 구간 리스트에";

    @Override
    public boolean canApply(Line line, Section newSection) {
        return line.getSections().isEmpty();
    }

    @Override
    public void addSection(Line line, Section newSection) {
        line.addSection(newSection);
    }

    @Override
    public String getFailureCaseMessage() {
        return ADD_SECTION_TO_EMPTY_SECTIONS_FAIL_MESSAGE;
    }
}


