package nextstep.subway.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@Entity
public class Line {
    public static final int FIRST = 0;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        this.sections.add(new Section(this, upStation, downStation, distance));
    }

    public List<Station> getStations() {

        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());

        stations.add(FIRST, sections.get(FIRST).getUpStation());

        return stations;
    }

    public void removeSection(Station station) {
        if (!this.getSections().get(this.getSections().size() - 1).getDownStation().equals(station)) {
            throw new IllegalArgumentException();
        }

        this.getSections().remove(this.getSections().size() - 1);
    }

    public void updateLine(String name, String color) {
        if (name != null) {
            this.name = name;
        }

        if (color != null) {
            this.color = color;
        }
    }
}
