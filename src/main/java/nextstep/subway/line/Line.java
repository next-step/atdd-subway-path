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
        // 처음 구간 추가
        if (isFirstSection(section)) {
            addFirstSection(section);
            return;
        }

        // 마지막 구간 추가
        if (isFinalSection(section)) {
            addFinalSection(section);
            return;
        }

        // 중간 구간 추가
        addMiddleSection(section);
    }

    private void addFirstSection(Section section) {
        sections.add(0, section);
    }

    private void addFinalSection(Section section) {
        sections.add(sections.size(), section);
    }

    private void addMiddleSection(Section section) {
        int insertIndex = sections.stream()
                .filter(s -> s.isConnectedSection(section))
                .findFirst()
                .map(s -> sections.indexOf(s) + 1)
                .orElseThrow(() -> new IllegalArgumentException("구간이 올바르게 이어지지 않습니다."));
        sections.add(insertIndex, section);

        // 양쪽 구간의 상/하행역 변경
        Section beforeSection = sections.get(insertIndex - 1);
        Section afterSection = sections.get(insertIndex);
        Station updateUpStation = getUpdateUpStation(beforeSection, section);
        Station updateDownStation = getUpdateDownStation(afterSection, section);
        beforeSection.updateDownStation(updateDownStation);
        afterSection.updateUpStation(updateUpStation);
    }

    private Station getUpdateUpStation(Section beforeSection, Section section) {
        if (beforeSection.getDownStation() == section.getDownStation()) {
            return section.getUpStation();
        }
        return beforeSection.getDownStation();
    }

    private Station getUpdateDownStation(Section beforeSection, Section section) {
        if (beforeSection.getUpStation() == section.getUpStation()) {
            return section.getDownStation();
        }
        return beforeSection.getUpStation();
    }

    private boolean isFirstSection(Section section) {
        return sections.isEmpty() || section.isFirstSection(sections.get(0));
    }

    private boolean isFinalSection(Section section) {
        return section.isFinalSection(sections.get(sections.size() - 1));
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

    public boolean isAlreadyRegisteredSection(Section anotherSection) {
        return sections.stream()
                .anyMatch(section -> section.isAlreadyRegisteredSection(anotherSection));
    }

    public boolean isRemoveFinalSection(Station station) {
        return getLastSection().isMatchDownStation(station);
    }

    public void validateSaveSection(Section section) {
        if (isAlreadyRegisteredSection(section)) {
            throw new IllegalArgumentException("이미 등록된 구간입니다.");
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
