package nextstep.subway.domain.utils;

import java.util.List;
import java.util.Optional;
import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.station.Station;

public class StationRearConnector extends StationConnector {
    public StationRearConnector(List<Section> sections, List<Station> stations, Station station) {
        super(sections, stations, station);
    }

    public boolean hasNextStation() {
        tempSection = findConnectedRearSection(station);
        return tempSection.isPresent();
    }

    public Station nextStation() {
        station = tempSection.get().getDownStation();
        return station;
    }

    private Optional<Section> findConnectedRearSection(Station downStation) {
        return sections.stream()
            .filter(section -> canBeConnectedRear(downStation, section))
            .findAny();
    }

    private boolean canBeConnectedRear(Station downStation, Section section) {
        return downStation.equals(section.getUpStation());
    }
}
