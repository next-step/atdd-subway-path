package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new LinkedList<>();

    protected Line() {/*no-op*/}

    public Line(Long id, String name, String color, Section section) {
        if (name == null || name.isBlank() || name.length() < 2) {
            throw new IllegalArgumentException("이름은 공백이 될 수 없습니다.");
        }

        if (color == null || color.isBlank() || color.length() < 2) {
            throw new IllegalArgumentException("색은 공백이 될 수 없습니다.");
        }

        if (section == null) {
            throw new IllegalArgumentException("구간이 존재해야 합니다.");
        }

        this.id = id;
        this.name = name;
        this.color = color;
        sections.add(new Section(null, this, section.getUpStation(), section.getDownStation(), section.getDistance()));
    }

    public Line(String name, String color, Section section) {
        this(null, name, color, section);
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
        return new ArrayList<>(sections);
    }

    public boolean addSection(Section section) {
        if (section.getUpStation().equals(section.getDownStation())) {
            throw new IllegalArgumentException("상행역과 하행역이 동일해서는 안됩니다.");
        }

        if (!sections.get(sections.size() - 1).getDownStation().equals(section.getUpStation())) {
            throw new IllegalArgumentException("현재 하행역과 등록하는 상행역이 같지 않습니다.");
        }

        if (sections.stream()
            .anyMatch(findSection -> findSection.getUpStation().equals(section.getDownStation())
                || findSection.getDownStation().equals(section.getDownStation()))) {
            throw new IllegalArgumentException("이미 존재하는 역입니다.");
        }

        return sections.add(section);
    }

    public void deleteSection(Long stationId) {
        if (sections.size() < 2) {
            throw new IllegalArgumentException("하나 남은 노선은 삭제할 수 없습니다.");
        }

        if (!sections.get(sections.size() - 1).getDownStation().getId().equals(stationId)) {
            throw new IllegalArgumentException("하핵역의 노선만 삭제할 수 있습니다.");
        }

        sections.remove(sections.size() - 1);
    }
}
