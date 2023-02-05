package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    protected Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public void updateLine(String name, String color) {
        if (name != null) {
            this.name = name;
        }

        if (color != null) {
            this.color = color;
        }
    }

    public void deleteLastSection() {
        sections.remove(sections.size() - 1);
    }

    public boolean isDownMostStation(Station station) {
        return sections.get(sections.size() - 1).getDownStation().equals(station);
    }

    public boolean canAddSection(Long upStationId, Long downStationId, int distance) {
        return upStationId != null && downStationId != null && distance != 0;
    }

    public boolean isEmptySections() {
        return sections.isEmpty();
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
        List<Station> stations = sections.stream()
            .map(Section::getDownStation)
            .collect(Collectors.toList());

        if (!stations.isEmpty()) {
            stations.add(0, sections.get(0).getUpStation());
        }
        
        return stations;
    }
}
