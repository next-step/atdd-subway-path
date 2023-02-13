package nextstep.subway.domain;

import nextstep.subway.domain.exception.DuplicateAddSectionException;
import nextstep.subway.domain.exception.IllegalAddSectionException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
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

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    public void addSection(Station requestUpStation, Station requestDownStation, int requestDistance) {
        for (Section section : sections) {
            if (section.isDuplicateSection(requestUpStation, requestDownStation)) {
                throw new DuplicateAddSectionException();
            }

            if (section.getUpStation().equals(requestDownStation) || section.getDownStation().equals(requestUpStation)) {
                sections.add(new Section(this, requestUpStation, requestDownStation, requestDistance));
                return;
            }

            if (section.getUpStation().equals(requestUpStation)) {
                section.changeUpStation(requestDownStation, requestDistance);
                sections.add(new Section(this, requestUpStation, requestDownStation, requestDistance));
                return;
            }
        }

        throw new IllegalAddSectionException();
    }

    public List<Station> getStations() {
        return sections.stream().sorted()
            .map(section -> List.of(section.getUpStation(), section.getDownStation()))
            .flatMap(Collection::stream)
            .distinct()
            .collect(Collectors.toList());
    }

}
