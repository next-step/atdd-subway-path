package nextstep.subway.domain.sectioncondition;

import nextstep.subway.applicaion.dto.AddSectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import org.springframework.stereotype.Component;

@Component
class EmptySectionsAddCondition implements AddSectionCondition {

    @Override
    public boolean matches(Line line, final AddSectionRequest addSectionRequest) {
        return line.hasNoSection();
    }

    @Override
    public void addSection(Line line, final AddSectionRequest request) {
        line.addSection(request.toSection());
    }

}
