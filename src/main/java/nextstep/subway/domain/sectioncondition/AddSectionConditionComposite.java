package nextstep.subway.domain.sectioncondition;

import nextstep.subway.applicaion.dto.AddSectionRequest;
import nextstep.subway.domain.Line;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

@Primary
@Component
class AddSectionConditionComposite implements AddSectionCondition {

    private final List<AddSectionCondition> addSectionConditions;

    public AddSectionConditionComposite(final List<AddSectionCondition> addSectionConditions) {
        this.addSectionConditions = addSectionConditions;
    }

    @Override
    public boolean matches(Line line, final AddSectionRequest addSectionRequest) {
        return addSectionConditions.stream()
                .anyMatch(condition -> condition.matches(line, addSectionRequest));
    }

    @Override
    public void addSection(final Line line, final AddSectionRequest request) {
        final AddSectionCondition addSectionCondition = findMatches(line, request);
        addSectionCondition.addSection(line, request);
    }

    private AddSectionCondition findMatches(final Line line, final AddSectionRequest addSectionRequest) {
        return addSectionConditions.stream()
                .filter(condition -> condition.matches(line, addSectionRequest))
                .findAny()
                .orElseThrow(IllegalStateException::new);
    }

}
