package nextstep.subway.domain;

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

    public void addSection(Section section) {
        sections.add(section);
    }

    public List<Station> getStations() {
        final List<Station> stations = new ArrayList<>();
        stations.add(getFirstSection().getUpStation());
        for (Section section : sections) {
            stations.add(section.getDownStation());
        }
        return new ArrayList<>(stations);
    }

    public void removeLastSection() {
        if (sections.isEmpty()) {
            throw new IllegalStateException("삭제할 section이 존재하지 않습니다.");
        }
        sections.remove(getLastIndex());
    }

    public void deleteLastSection(Station station) {
        Section lastSection = sections.get(getLastIndex());
        if (!lastSection.hasDownStation(station)) {
            throw new IllegalArgumentException();
        }
        sections.remove(lastSection);
    }

    private Section getFirstSection() {
        return sections.get(0);
    }

    private int getLastIndex() {
        return sections.size() - 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(getId(), line.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
