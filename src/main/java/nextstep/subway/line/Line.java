package nextstep.subway.line;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import nextstep.subway.station.Station;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 20, nullable = false)
    private String name;
    private String color;
    @ManyToMany
    private List<Station> stations;
    @OneToMany
    private List<Section> sections = new ArrayList<>();

    public Line() {
    }

    public Line(String name, String color, List<Station> stations) {
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<Station> getStations() {
        return sections.stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .distinct()
                .collect(Collectors.toList());
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(Station upstreamStation, Station downstreamStation) {
        if (!stations.get(getDownStreamTerminusStationIndex()).equals(upstreamStation)) {
            throw new MismatchedUpstreamStationException();
        }
        if (stations.contains(downstreamStation)) {
            throw new DownstreamStationIncludedException();
        }
        stations.add(downstreamStation);
    }

    private int getDownStreamTerminusStationIndex() {
        return stations.size() - 1;
    }

    public void deleteSectionByDownStreamTerminusStation(Station downStreamTerminusStation) {
        int downStreamTerminusStationIndex = getDownStreamTerminusStationIndex();
        if (!stations.get(downStreamTerminusStationIndex).equals(downStreamTerminusStation)) {
            throw new NonDownstreamTerminusException();
        }
        if (stations.size() == 2) {
            throw new SingleSectionRemovalException();
        }
        stations.remove(downStreamTerminusStationIndex);
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public List<Section> getSections() {
        return sections;
    }
}
