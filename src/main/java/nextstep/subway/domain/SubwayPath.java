package nextstep.subway.domain;

import org.springframework.util.Assert;

import java.util.List;

public class SubwayPath {
    private List<Station> stations;
    private List<SectionEdge> sectionEdges;

    public SubwayPath(List<Station> stations, List<SectionEdge> sectionEdges) {
        Assert.notNull(stations, "stations must not be null");
        Assert.notNull(sectionEdges, "sectionEdges must not be null");
        this.stations = stations;
        this.sectionEdges = sectionEdges;
    }

    public List<Station> getStations() {
        return this.stations;
    }

    public int totalDistance() {
        return this.sectionEdges.stream()
                .mapToInt(SectionEdge::distance)
                .sum();
    }
}
