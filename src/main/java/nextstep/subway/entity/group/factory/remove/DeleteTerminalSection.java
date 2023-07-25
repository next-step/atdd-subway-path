package nextstep.subway.entity.group.factory.remove;

import java.util.List;
import nextstep.subway.entity.Section;

public class DeleteTerminalSection implements SectionDeleteAction {

    private final List<Section> removeSections;

    public DeleteTerminalSection(List<Section> removeSections) {
        this.removeSections = removeSections;
    }

    @Override
    public void action(List<Section> sections) {
        int first = 0;
        sections.remove(removeSections.get(first));
    }
}
