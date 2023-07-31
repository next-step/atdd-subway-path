package nextstep.subway.domain.add;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Stations;

public class AddFirst implements SectionAddStrategy {

    @Override
    public boolean match(Sections sections, Section section) {
        Stations stations = new Stations(sections.getStations());
        return stations.equalFirstStation(section.getDownStation());
    }

    @Override
    public void add(Sections sections, Section section) {
        sections.add(0, section);
    }
}
