package nextstep.subway.section;

import java.util.ArrayList;
import java.util.List;

public class LineSections {
    private List<Section> sections = new ArrayList<>();
    public LineSections(List<Section> sections) {
        this.sections = sections;
    }

    public boolean hasStation(long downStationId) {
        return sections.stream()
                .anyMatch(section -> section.getUpStationId() == downStationId
                        || section.getDownStationId() == downStationId);
    }

    public boolean hasOnlyOneSection() {
        return sections.isEmpty();
    }
}
