package nextstep.subway.domain.sectioncondition;

import nextstep.subway.applicaion.dto.AddSectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import org.springframework.stereotype.Component;

@Component
class LastDownStationCondition implements SectionCondition {

    @Override
    public boolean matches(final Line line, final AddSectionRequest request) {
        return line.isLastDownStation(request.getUpStation())
                && !line.containsStation(request.getDownStation());
    }

    @Override
    public void add(final Line line, final AddSectionRequest addSectionRequest) {
        line.addSection(createSection(line, addSectionRequest));
    }

    private Section createSection(final Line line, final AddSectionRequest addSectionRequest) {
        return new Section(line, addSectionRequest.getUpStation(), addSectionRequest.getDownStation(), addSectionRequest.getDistance());
    }

}
