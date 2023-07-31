package nextstep.subway.domain.delete;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.Stations;

import java.util.List;

public class DeleteMiddle implements SectionDeleteStrategy {

    @Override
    public boolean match(Stations stations, Station station) {
        return false;
    }

    @Override
    public void delete(Sections sections, Station station) {
        List<Section> targets = sections.findIncluded(station);
        Section updateSection = targets.get(0);
        Section deleteSection = targets.get(1);
        updateSection.removeStation(deleteSection);
        sections.remove(deleteSection);
    }
}
