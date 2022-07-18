package nextstep.subway.domain.sectioncondition;

import nextstep.subway.applicaion.dto.AddSectionRequest;
import nextstep.subway.domain.Line;
import org.springframework.stereotype.Component;

@Component
class EmptySectionsAddCondition implements SectionAddCondition {

    @Override
    public boolean matches(Line line, final AddSectionRequest addSectionRequest) {
        return line.hasNoSection();
    }

    @Override
    public void addSection(Line line, final AddSectionRequest request) {
        line.addSection(request.toSection());
    }

}
