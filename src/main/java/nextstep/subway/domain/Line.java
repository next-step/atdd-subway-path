package nextstep.subway.domain;

import nextstep.subway.exception.AddSectionException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public List<Section> getSections() {
        return sections;
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        if (getSections() == null || getSections().isEmpty()) {
            getSections().add(new Section(this, upStation, downStation, distance));
            return;
        }
        for (Section section : getSections()) {
            if (Objects.equals(section.getUpStation().getId(), upStation.getId()) && Objects.equals(section.getDownStation().getId(), downStation.getId())) {
                throw new AddSectionException("상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.");
            }
        }
    }
}
