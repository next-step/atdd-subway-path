package nextstep.subway.domain.sectioncondition;

import nextstep.subway.applicaion.dto.AddSectionRequest;
import nextstep.subway.domain.Line;

public interface AddSectionCondition {

    boolean matches(Line line, AddSectionRequest addSectionRequest);

    void addSection(Line line, AddSectionRequest addSectionRequest);

}
