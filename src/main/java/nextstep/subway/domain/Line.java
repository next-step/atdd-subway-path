package nextstep.subway.domain;

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

    protected Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void add(Section section) {
        section.setLine(this);
        sections.add(section);
    }

    public List<Station> getStations() {
        return sections.stream()
                .map(section -> List.of(section.getUpStation(), section.getDownStation()))
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toUnmodifiableList());
    }

    public void removeSection(long stationId) {
        if (sections.size() == 1) {
            throw new IllegalArgumentException("구간이 1개인 경우 삭제할 수 없습니다.");
        }

        Section lastSection = sections.get(sections.size() - 1);;
        if (!lastSection.isDownStationId(stationId)) {
            throw new IllegalArgumentException("마지막 구간만 제거할 수 있습니다.");
        }

        sections.remove(lastSection);
    }

    public void update(String name, String color) {
        if (name != null) {
            this.name = name;
        }

        if (color != null) {
            this.color = color;
        }
    }
}
