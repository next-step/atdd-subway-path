package nextstep.subway.domain.sectioncondition;

import nextstep.subway.applicaion.dto.AddSectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class SectionConditionComposite implements SectionCondition {

    private final List<SectionCondition> sectionConditions;

    public SectionConditionComposite(final List<SectionCondition> sectionConditions) {
        this.sectionConditions = sectionConditions;
    }

    @Override
    public boolean isSatisfiedBy(Line line, final AddSectionRequest addSectionRequest) {
        return sectionConditions.stream()
                .anyMatch(condition -> condition.isSatisfiedBy(line, addSectionRequest));
    }

    @Override
    public void add(final Line line, final AddSectionRequest addSectionRequest) {
        final SectionCondition sectionCondition = sectionConditions.stream()
                .filter(condition -> condition.isSatisfiedBy(line, addSectionRequest))
                .findAny()
                .orElseThrow(IllegalStateException::new);

        sectionCondition.add(line, addSectionRequest);
    }

}
