package nextstep.subway.domain.sectioncondition;

import nextstep.subway.applicaion.dto.AddSectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import org.springframework.stereotype.Component;

@Component
class LastDownStationAddCondition implements AddSectionCondition {

    @Override
    public boolean matches(final Line line, final AddSectionRequest request) {
        return line.isLastDownStation(request.getUpStation())
                && !line.containsStation(request.getDownStation());
    }

    @Override
    public void addSection(final Line line, final AddSectionRequest addSectionRequest) {
        line.addSection(addSectionRequest.toSection());
    }

}
