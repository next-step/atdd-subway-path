package nextstep.subway.domain;

import nextstep.subway.exception.advice.ValidationException;

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

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
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

    public Sections getSections() {
        return new Sections(sections);
    }

    public Section getLastSection() {
        return this.getSections().getList().get(this.getSections().getList().size() - 1);
    }

    public void addSection(Section section) {
        this.sections.add(section);
    }

    public void update(String name, String color) {
        if (name != null) {
            this.name = name;
        }
        if (color != null) {
            this.color = color;
        }
    }

    public void removeSectionWithValidateStation(Station station) {
        validateBeforeRemoveSection(station);
        this.sections.remove(this.getLastSection());
    }

    private void validateBeforeRemoveSection(Station station) {
        if (!this.getLastSection().getDownStation().equals(station)) {
            throw new ValidationException("삭제하려는 역이 하행종점역이 아닙니다.");
        }
    }
}
