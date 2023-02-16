package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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

    public boolean hasSections() {
        return sections.size() > 0;
    }

    public void addSection(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }

        // 상행 종점
        if (isFirstSection(section)) {
            sections.add(0, section);
            return;
        }

        // 하행 종점
        if (isLastSection(section)) {
            sections.add(section);
            return;
        }

        // 사이

        sections.add(section);
    }

    public int getSectionsCount() {
        return sections.size();
    }

    public Section getFirstSection() {
        return sections.get(0);
    }

    public List<Station> getStations() {
        Set<Station> stations = new LinkedHashSet<>();
        for (Section section : sections) {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        }

        return new ArrayList<>(stations);
    }

    public void removeSection(Station station) {
        if (!isLastStation(station)) {
            throw new IllegalArgumentException();
        }

        sections.remove(sections.size() - 1);
    }

    public int getLength() {
        if (sections.isEmpty()) {
            return 0;
        }

        return sections.stream().map(Section::getDistance).reduce(0, Integer::sum);
    }

    private boolean isFirstStation(Station station) {
        return sections.get(0).getUpStation().equals(station);
    }

    private boolean isLastStation(Station station) {
        return sections.get(sections.size() - 1).getDownStation().equals(station);
    }

    private boolean isFirstSection(Section section) {
        return isFirstStation(section.getDownStation());
    }

    private boolean isLastSection(Section section) {
        return isLastStation(section.getUpStation());
    }

}
