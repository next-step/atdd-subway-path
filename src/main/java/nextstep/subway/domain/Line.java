package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    @Embedded
    private final Sections sections = new Sections();

    protected Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Station lastUpStation, Station lastDownStation, int distance) {
        this(name, color);
        sections.add(new Section(this, lastUpStation, lastDownStation, distance));
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

    public void addSection(Section section) {
        sections.add(section);
    }

    public List<Station> getStations() {
        List<Station> allStations = new ArrayList<>();
        Section lastUpSection = findLastUpSection();
        allStations.add(lastUpSection.getUpStation());

        Optional<Section> nextSection = Optional.ofNullable(lastUpSection);
        while (nextSection.isPresent()) {
            Section section = nextSection.get();
            allStations.add(section.getDownStation());
            nextSection = findSectionWithUpStation(section.getDownStation());
        }
        return allStations;
    }

    private Optional<Section> findSectionWithUpStation(Station downStation) {
        return sections.findSectionWithUpStation(downStation);
    }

    private Section findLastUpSection() {
        return sections.findLastUpSection();
    }

    public boolean isSectionsEmpty() {
        return sections.isEmpty();
    }

    public int getSectionSize() {
        return sections.getSize();
    }

    public void removeSectionByLastDownStation(Station station) {
        if (!isLastDownStation(station)) {
            throw new IllegalArgumentException();
        }
        sections.remove(station);
    }

    private boolean isLastDownStation(Station station) {
        return sections.isLastDownStation(station);
    }

    public Section getSectionAt(int i) {
        return sections.get(i);
    }

    @Override
    public String toString() {
        return "Line{" +
                "name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
