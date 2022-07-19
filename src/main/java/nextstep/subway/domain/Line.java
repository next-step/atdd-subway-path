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
            throw new IllegalArgumentException();
        }

        if (color == null || color.isBlank() || color.length() < 2) {
            throw new IllegalArgumentException();
        }

        if (section == null) {
            throw new IllegalArgumentException();
        }

        this.id = id;
        this.name = name;
        this.color = color;
        section.updateLine(this);
        sections.add(section);
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
        if (!sections.get(sections.size() - 1).getDownStation().equals(section.getUpStation())) {
            throw new IllegalArgumentException();
        }

        if (sections.stream()
            .anyMatch(findSection -> findSection.getUpStation().equals(section.getDownStation())
                || findSection.getDownStation().equals(section.getDownStation()))) {
            throw new IllegalArgumentException();
        }

        section.updateLine(this);
        return sections.add(section);
    }

    public void deleteSection(Long stationId) {
        if (sections.size() < 2) {
            throw new IllegalArgumentException();
        }

        if (!sections.get(sections.size() - 1).getDownStation().getId().equals(stationId)) {
            throw new IllegalArgumentException();
        }

        sections.remove(sections.size() - 1);
    }
}
