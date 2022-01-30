package nextstep.subway.domain.utils;

import java.util.List;
import java.util.Optional;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

public class StationFrontConnector extends StationConnector {
    public StationFrontConnector(List<Section> sections, List<Station> stations, Station station) {
        super(sections, stations, station);
    }

    public boolean hasNextStation() {
        tempSection = findConnectedFrontSection(station);
        return tempSection.isPresent();
    }

    public Station nextStation() {
        station = tempSection.get().getUpStation();
        return station;
    }

    private Optional<Section> findConnectedFrontSection(Station upStation) {
        return sections.stream()
            .filter(section -> canBeConnectedFront(upStation, section))
            .findAny();
    }

    private boolean canBeConnectedFront(Station upStation, Section section) {
        return upStation.equals(section.getDownStation());
    }
}
