package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<Section> getSections() {
        return sections;
    }

    public Station getLastStation() {
        return sections.get(sections.size()-1).getDownStation();
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        sections.add(new Section(this,upStation, downStation, distance));
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }
        List<Station> stations = sections.stream().map(Section::getDownStation).collect(Collectors.toList());
        stations.add(0, sections.get(0).getUpStation());
        return stations;
    }


    public void deleteSection(Station station) {
        if (!getStations().contains(station)) {
            throw new IllegalArgumentException("해당 역은 존재하지 않습니다.");
        }
        if (station != getLastStation()) {
            throw new IllegalArgumentException("해당 역은 삭제할 수 없습니다");
        }
        sections.remove(sections.size()-1);
    }
}
