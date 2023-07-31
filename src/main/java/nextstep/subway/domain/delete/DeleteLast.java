package nextstep.subway.domain.delete;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.Stations;

public class DeleteLast implements SectionDeleteStrategy {
    @Override
    public boolean match(Stations stations, Station station) {
        return stations.equalLastStation(station);
    }

    @Override
    public void delete(Sections sections, Station station) {
        Section lastSection = sections.findLast();
        if (!lastSection.equalDownStation(station)) {
            throw new IllegalArgumentException();
        }
        sections.remove(lastSection);
    }
}
