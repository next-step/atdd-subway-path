package nextstep.subway.line;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import nextstep.subway.station.Station;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 20, nullable = false)
    private String name;
    private String color;
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "line_section", joinColumns = @JoinColumn(name = "line_id"))
    private List<Section> sections = new ArrayList<>();

    public Line() {
    }

    public Line(String name, String color, List<Section> stations) {
        this.name = name;
        this.color = color;
        this.sections = stations;
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

    public List<Section> getSections() {
        return sections;
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

    public void addSection(Section section) {
        if (sections.stream()
                .anyMatch(savedSection -> savedSection.containsStation(section.getDownStation()))) {
            throw new DownstreamStationIncludedException();
        }
        if (!sections.isEmpty() && !sections.get(sections.size() - 1).canLink(section)) {
            throw new MismatchedUpstreamStationException();
        }
        sections.add(section);
    }

    public void deleteStation(Station downStation) {
        if (sections.size() == 1) {
            throw new SingleSectionRemovalException();
        }
        int lastIndex = sections.size() - 1;
        Section section = sections.get(lastIndex);
        if (section.isSameDownStation(downStation)) {
            sections.remove(section);
            return;
        }
        throw new NonDownstreamTerminusException();
    }
}
