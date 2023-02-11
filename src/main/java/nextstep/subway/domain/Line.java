package nextstep.subway.domain;

import nextstep.subway.domain.exception.IllegalDistanceSectionException;
import nextstep.subway.domain.exception.IllegalDuplicateSectionException;

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

    public void addSection(Station requestUpStation, Station requestDownStation, int requestDistance) {
        for (int i = 0; i < sections.size(); i++) {
            Section originSection = sections.get(i);
            if (originSection.isDuplicateSection(requestUpStation, requestDownStation)) {
                throw new IllegalDuplicateSectionException();
            }

            // 첫 번째
            if (originSection.getUpStation().equals(requestDownStation)) {
                sections.add(0, new Section(this, requestUpStation, requestDownStation, requestDistance));
                return;
            }

            // 중간
            if (originSection.getUpStation().equals(requestUpStation)) {
                Station originDownStation = originSection.getDownStation();
                int originDistance = originSection.getDistance();

                if (originSection.getDistance() <= requestDistance) {
                    throw new IllegalDistanceSectionException();
                }

                originSection.changeDownStation(requestDownStation, requestDistance);
                sections.add(new Section(this, requestDownStation, originDownStation, originDistance - requestDistance));
                return;
            }
        }

        sections.add(new Section(this, requestUpStation, requestDownStation, requestDistance));
    }

}
