package nextstep.subway.domain.utils;

import java.util.List;
import java.util.Optional;
import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.station.Station;

public abstract class StationConnector {
    protected List<Section> sections;
    protected List<Station> stations;
    protected Station station;

    protected Optional<Section> tempSection;

    public StationConnector(List<Section> sections,
        List<Station> stations, Station station) {
        this.sections = sections;
        this.stations = stations;
        this.station = station;
    }

    public abstract boolean hasNextStation();
    public abstract Station nextStation();
}
