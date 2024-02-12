package nextstep.subway.line;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import nextstep.subway.section.Section;
import nextstep.subway.station.Station;

@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 20, nullable = false)
    private String color;

    @OneToMany(
            mappedBy = "line",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true
    )
    private List<Section> sections = new ArrayList<>();

    @Column
    private long distance;

    public Line() {}

    public Line(String name, String color, long distance) {
        this.name = name;
        this.color = color;
        this.distance = distance;
    }

    public long getId() {
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

    public void setName(String name) {
        this.name = name;
    }

    public void updateLine(LineRequest lineRequest) {
        this.name = lineRequest.getName();
        this.color = lineRequest.getColor();
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public void removeSection(Station station) {
        Section lastSection = getLastSection();
        if (lastSection.getDownStation() != station) {
            throw new IllegalArgumentException("삭제할 구간이 올바르지 않습니다.");
        }

        lastSection.remove();
        sections.remove(lastSection);
    }

    private Section getLastSection() {
        return sections.get(sections.size() - 1);
    }

    public boolean isSingleSection() {
        return sections.size() == 1;
    }

    public boolean isExistSection(Section anotherSection) {
        return sections.stream()
                .anyMatch(section -> section.isExistSection(anotherSection));
    }

    public boolean isConnectedSection(Section anotherSection) {
        return sections.stream()
                .anyMatch(section -> section.isConnectedSection(anotherSection));
    }

    public boolean isRemoveFinalSection(Station station) {
        return getLastSection().isMatchDownStation(station);
    }

    public void validateSaveSection(Section section) {
        if (isExistSection(section)) {
            throw new IllegalArgumentException("이미 등록된 구간입니다.");
        }

        if (!isConnectedSection(section)) {
            throw new IllegalArgumentException("구간이 올바르게 이어지지 않습니다.");
        }
    }

    public void validateDeleteSection(Station station) {
        if (isSingleSection()) {
            throw new IllegalArgumentException("삭제할 구간이 존재하지 않습니다.");
        }

        if (!isRemoveFinalSection(station)) {
            throw new IllegalArgumentException("삭제할 구간이 올바르지 않습니다.");
        }
    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", sections=" + sections +
                ", distance=" + distance +
                '}';
    }
}
