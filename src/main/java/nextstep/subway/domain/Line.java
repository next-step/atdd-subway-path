package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
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

    public Section addSection(Section section) {
        this.sections.add(section);
        return this.sections.get(this.sections.size() - 1);
    }

    public List<Station> getStations() {
        List<Station> stations = getSections().stream()
            .map(Section::getDownStation)
            .collect(Collectors.toList());

        stations.add(0, getSections().get(0).getUpStation());
        return stations;
    }

    public void removeSection(Station station) {
        if (this.sections.size() == 1) {
            throw new IllegalArgumentException("삭제하려는 구간이 노선의 마지막 구간입니다.");
        }

        if (!getSections().get(getSections().size() - 1).getDownStation().equals(station)) {
            throw new IllegalArgumentException();
        }

        getSections().remove(getSections().size() - 1);
    }
}
