package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
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

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        this.sections = addSection(upStation, downStation, distance);
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

    public List<Section> addSection(Station upStation, Station downStation, int distance) {
        Section newSection = new Section(
                this,
                upStation,
                downStation,
                distance
        );
        this.getSections()
            .add(newSection);

        return sections;
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();

        sections.forEach(section -> {
            Station upStation = section.getUpStation();
            Station downStation = section.getDownStation();
            if (upStation != null && !stations.contains(upStation)) {
                stations.add(upStation);
            }
            if (downStation != null && !stations.contains(downStation)) {
                stations.add(downStation);
            }
        });

        return stations;
    }

    public void removeSection() {
        Section lastSection = sections.get(sections.size() - 1);
        sections.remove(lastSection);
    }
}
