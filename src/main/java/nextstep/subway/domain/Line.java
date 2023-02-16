package nextstep.subway.domain;

import lombok.Getter;
import nextstep.subway.applicaion.dto.LineRequest;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        Section section = new Section(this, upStation, downStation, distance);
        sections.add(section);
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }
        List<Station> stations = new ArrayList<>();
        stations.add(sections.get(0).getUpStation());
        sections.stream()
                .forEach(section -> {
                    stations.add(section.getDownStation());
                });
        return stations;
    }

    public void removeSection(Station station) {
        if (!sections.get(sections.size() - 1).getDownStation().equals(station)) {
            throw new IllegalArgumentException();
        }
        sections.remove(sections.size() - 1);
    }

    public void updateNameOrColor(LineRequest request) {
        if(request.getName() != null) {
            this.name = request.getName();
        }
        if(request.getColor() != null) {
            this.color = request.getColor();
        }
    }
}
