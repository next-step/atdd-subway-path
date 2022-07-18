package nextstep.subway.domain.sectioncondition;

import nextstep.subway.applicaion.dto.AddSectionRequest;
import nextstep.subway.domain.Line;
import org.springframework.stereotype.Component;

@Component
class FirstUpStationAddCondition implements SectionAddCondition {

    @Override
    public boolean matches(final Line line, final AddSectionRequest request) {
        return line.isFirstStation(request.getDownStation())
                && !line.containsStation(request.getUpStation());
    }

    @Override
    public void addSection(final Line line, final AddSectionRequest request) {
        line.addSection(0, request.toSection());
    }

}
