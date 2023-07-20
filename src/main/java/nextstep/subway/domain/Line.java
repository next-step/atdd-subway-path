package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Line() { }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        this.sections.add(new Section(this, upStation, downStation, distance));
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

    public void addSection(Section section) {
        this.sections.add(section);
    }

    public void deleteSectionByUpStation(Station upStation) {
        for (Section savedSection : this.sections) {
            Station savedUpStation = savedSection.getUpStation();
            if (savedUpStation.getName().equals(upStation.getName())) {
                this.sections.remove(savedSection);
                return;
            }
        }
    }

    public void deleteSectionByDownStation(Station downStation) {
        for (Section savedSection : this.sections) {
            Station savedUpStation = savedSection.getUpStation();
            if (savedUpStation.getName().equals(downStation.getName())) {
                this.sections.remove(savedSection);
                return;
            }
        }
    }
}