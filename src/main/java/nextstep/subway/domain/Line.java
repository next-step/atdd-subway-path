package nextstep.subway.domain;

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

    public void addSection(Station requestUpStation, Station requestDownStation, int distance) {
        for (Section originSection : sections) {
            validateAddSection(originSection, requestUpStation, requestDownStation);

            // 구간 변경
            if (originSection.getUpStation().equals(requestUpStation)) {
                Station originDownStation = originSection.getDownStation();
                int originDistance = originSection.getDistance();
                originSection.changeDownStation(requestDownStation, distance);
                sections.add(new Section(this, requestDownStation, originDownStation, originDistance - distance));
                return;
            }
        }

        sections.add(new Section(this, requestUpStation, requestDownStation, distance));
    }

    private void validateAddSection(Section originSection, Station requestUpStation, Station requestDownStation) {
        if (originSection.isDuplicateSection(requestUpStation, requestDownStation)) {
            throw new IllegalDuplicateSectionException();
        }
    }

}
