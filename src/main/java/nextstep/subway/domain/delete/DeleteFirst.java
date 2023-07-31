package nextstep.subway.domain.delete;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.Stations;

public class DeleteFirst implements SectionDeleteStrategy {

    @Override
    public boolean match(Stations stations, Station station) {
        return stations.equalFirstStation(station);
    }

    @Override
    public void delete(Sections sections, Station station) {
        Section firstSection = sections.findFirst();
        if (!firstSection.equalUpStation(station)) {
            throw new IllegalArgumentException();
        }
        sections.remove(firstSection);
    }
}
