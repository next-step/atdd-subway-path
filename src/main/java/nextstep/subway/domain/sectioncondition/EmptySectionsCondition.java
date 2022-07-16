package nextstep.subway.domain.sectioncondition;

import nextstep.subway.applicaion.dto.AddSectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import org.springframework.stereotype.Component;

@Component
class EmptySectionsCondition implements SectionCondition {

    @Override
    public boolean isSatisfiedBy(Line line, final AddSectionRequest addSectionRequest) {
        return line.hasNoSection();
    }

    @Override
    public void add(Line line, final AddSectionRequest addSectionRequest) {
        line.addSection(new Section(line, addSectionRequest.getUpStation(), addSectionRequest.getDownStation(), addSectionRequest.getDistance()));
    }

}
