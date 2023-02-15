package nextstep.subway.vo;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.station.Station;

import java.util.Collections;
import java.util.List;

public abstract class PathFinder {
    private List<Station> stations;
    private List<Section> sections;

    public PathFinder(List<Station> stations, List<Section> sections) {
        this.stations = stations;
        this.sections = sections;
    }

    public abstract PathResponse findPath(Station source, Station target);

    protected List<Station> getStations() {
        return Collections.unmodifiableList(stations);
    }

    protected List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }
}
