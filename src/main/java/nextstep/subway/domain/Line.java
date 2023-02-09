package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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

    public void addSection(Section section) {
        sections.add(section);
    }

    public void removeSection(Station station) {
        if (sections.size() == 1) {
            throw new IllegalStateException("지하철노선은 1개 구간 이하로 구성될 수 없습니다.");
        }

        if (!sections.get(sections.size() - 1).getDownStation().equals(station)) {
            throw new IllegalArgumentException("해당 노선의 하행종점역만 제거할 수 있습니다.");
        }

        sections.remove(sections.size() - 1);
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();

        sections.forEach(section -> {
            stations.add(section.getUpStation());
            if (sections.indexOf(section) == sections.size() - 1) {
                stations.add(section.getDownStation());
            }
        });

        return stations;
    }
}
