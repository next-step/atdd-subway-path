package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String color;

    @OneToMany(
            mappedBy = "line",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
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
        return Collections.unmodifiableList(sections);
    }

    public List<Station> getStations() {
        var stations = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
        stations.add(sections.get(sections.size() - 1).getDownStation());

        return Collections.unmodifiableList(stations);
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void init(Section section) {
        section.setLine(this);
        sections.add(section);
    }

    public void addSection(Section section) {
        verifyCanBeAdded(section);
        section.setLine(this);
        sections.add(section);
    }

    public void remove(Station station) {
        verifyCanBeDeleted(station);
        sections.remove(sections.size() - 1);
    }

    private void verifyCanBeAdded(Section section) {
        if (sections.isEmpty()) {
            throw new IllegalArgumentException("비어있는 노선에 구간을 추가할 수 없습니다.");
        }

        var lastStop = sections.get(sections.size() - 1);
        if (!lastStop.getDownStation().equals(section.getUpStation())) {
            throw new IllegalArgumentException("새로운 구간의 상행역이 해당 노선의 하행 종점역이 아닙니다.");
        }

        var downStation = section.getDownStation();
        if (sections.stream().map(Section::getUpStation).anyMatch(station -> station.equals(downStation))
                || sections.stream().map(Section::getDownStation).anyMatch(station -> station.equals(downStation))) {
            throw new IllegalArgumentException("새로운 구간의 하행역이 해당 노선에 이미 등록되어있습니다.");
        }
    }

    private void verifyCanBeDeleted(Station station) {
        if (sections.size() <= 1) {
            throw new IllegalStateException("노선에 구간이 부족하여 역을 삭제할 수 없습니다.");
        }

        if (!sections.get(sections.size() - 1).getDownStation().equals(station)) {
            throw new IllegalArgumentException("삭제하려는 역은 해당 노선 마지막 구간의 하행역이 아닙니다.");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (this.id == null || obj == null || getClass() != obj.getClass())
            return false;

        var line = (Line) obj;
        return id.equals(line.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
