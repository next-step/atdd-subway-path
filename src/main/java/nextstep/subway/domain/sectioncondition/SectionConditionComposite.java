package nextstep.subway.domain.sectioncondition;

import nextstep.subway.applicaion.dto.AddSectionRequest;
import nextstep.subway.domain.Line;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class SectionConditionComposite implements SectionCondition {

    private final List<SectionCondition> sectionConditions;

    public SectionConditionComposite(final List<SectionCondition> sectionConditions) {
        this.sectionConditions = sectionConditions;
    }

    @Override
    public boolean matches(Line line, final AddSectionRequest addSectionRequest) {
        return sectionConditions.stream()
                .anyMatch(condition -> condition.matches(line, addSectionRequest));
    }

    @Override
    public void add(final Line line, final AddSectionRequest request) {
        final SectionCondition sectionCondition = findMatches(line, request);
        sectionCondition.add(line, request);
    }

    private SectionCondition findMatches(final Line line, final AddSectionRequest addSectionRequest) {
        return sectionConditions.stream()
                .filter(condition -> condition.matches(line, addSectionRequest))
                .findAny()
                .orElseThrow(IllegalStateException::new);
    }

}
