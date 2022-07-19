package nextstep.subway.domain.sectioncondition.add;

import nextstep.subway.applicaion.dto.AddSectionRequest;
import nextstep.subway.domain.Line;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

@Primary
@Component
class SectionAddConditionComposite implements SectionAddCondition {

    private final List<SectionAddCondition> sectionAddConditions;

    public SectionAddConditionComposite(final List<SectionAddCondition> sectionAddConditions) {
        this.sectionAddConditions = sectionAddConditions;
    }

    @Override
    public boolean matches(Line line, final AddSectionRequest addSectionRequest) {
        return sectionAddConditions.stream()
                .anyMatch(condition -> condition.matches(line, addSectionRequest));
    }

    @Override
    public void addSection(final Line line, final AddSectionRequest request) {
        final SectionAddCondition sectionAddCondition = findMatches(line, request);
        sectionAddCondition.addSection(line, request);
    }

    private SectionAddCondition findMatches(final Line line, final AddSectionRequest addSectionRequest) {
        return sectionAddConditions.stream()
                .filter(condition -> condition.matches(line, addSectionRequest))
                .findAny()
                .orElseThrow(IllegalStateException::new);
    }

}
