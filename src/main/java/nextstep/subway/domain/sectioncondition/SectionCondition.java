package nextstep.subway.domain.sectioncondition;

import nextstep.subway.applicaion.dto.AddSectionRequest;
import nextstep.subway.domain.Line;

public interface SectionCondition {

    boolean matches(Line line, AddSectionRequest addSectionRequest);

    void add(Line line, AddSectionRequest addSectionRequest);

}
