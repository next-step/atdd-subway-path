package nextstep.subway.entity.group.factory.remove;

import java.util.List;
import nextstep.subway.entity.Section;

public interface SectionDeleteAction {

    void action(List<Section> sections);
}
