package nextstep.subway.domain.sectioncondition;

import nextstep.subway.applicaion.dto.AddSectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import org.springframework.stereotype.Component;

@Component
class FirstUpStationCondition implements SectionCondition {

    @Override
    public boolean matches(final Line line, final AddSectionRequest request) {
        return line.isFirstStation(request.getDownStation())
                && !line.hasStation(request.getUpStation());
    }

    @Override
    public void add(final Line line, final AddSectionRequest request) {
        line.addSection(0, createSection(line, request));
    }

    private Section createSection(final Line line, final AddSectionRequest request) {
        return new Section(line, request.getUpStation(), request.getDownStation(), request.getDistance());
    }

}
