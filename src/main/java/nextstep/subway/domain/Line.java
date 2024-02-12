package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST,
        CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(Long id, String name, String color) {
        this.id = id;
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
        return sections.stream().sorted(Comparator.comparing(Section::getOrderNo))
            .collect(Collectors.toList());
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        // 중간에 끼워 넣는 경우, 맨 끝에 넣는 경우
        if (sections.isEmpty()) {
            Section newSection = Section.createFirstSection(this, upStation, downStation, distance);
            this.sections.add(newSection);
            return;
        }
        Section tempSection = Section.createTempSection(this, upStation, downStation, distance);
        if (isLastSectionAndNewSectionConnectionValid(tempSection)) {
            addTailSection(tempSection);
            return;
        }
        addMiddleSection(tempSection);
    }

    private void addTailSection(Section tempSection) {
        Section newSection = new Section(this, tempSection.getUpStation(),
            tempSection.getDownStation(), tempSection.getDistance(),
            getLastSection().getOrderNo() + 1);
        this.sections.add(newSection);
    }

    private boolean isLastSectionAndNewSectionConnectionValid(Section tempSection) {
        // 마지막 지하철 구간의 하행역과 추가할 구간의 상행역이 같고, 추가할 구간의 하행역이 없을 때
        if (getLastSection().getDownStation().equals(tempSection.getUpStation())
            && getSections().stream().noneMatch(section ->
            section.getDownStation().equals(tempSection.getDownStation()))) {
            return true;
        }
        return false;
    }

    private void addMiddleSection(Section tempSection) {
        Section foundSection = getSections().stream()
            .filter(section -> section.getUpStation().equals(tempSection.getUpStation()))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
        validateMiddleSectionAndNewSectionConnectionValid(tempSection);
        foundSection.changeUpStation(tempSection.getDownStation());
        // 거리는 기존 FoundSection 거리 - 새로운 Section 거리의 나머지
        foundSection.changeDistance(foundSection.getDistance() - tempSection.getDistance());
        Section newSection = Section.createMiddleSection(this, tempSection.getUpStation(),
            tempSection.getDownStation(), tempSection.getDistance(), foundSection.getOrderNo());
        this.sections.add(foundSection.getOrderNo() - 1, newSection);
        for (int i = foundSection.getOrderNo(); i < getSections().size(); i++) {
            getSections().get(i).changeOrderNo(getSections().get(i).getOrderNo() + 1);
        }
    }

    private void validateMiddleSectionAndNewSectionConnectionValid(Section tempSection) {
        // 같은 하행역이 하나도 없는지 확인
        if (getSections().stream()
            .anyMatch(section -> section.getDownStation().equals(tempSection.getDownStation()))) {
            throw new IllegalArgumentException();
        }
    }

    // 맨 끝에 넣는 경우

    private Section getLastSection() {
        return getSections().get(getSections().size() - 1);
    }

    public Set<Station> getStations() {
        Set<Station> stations = new HashSet<>();
        for (Section section : sections) {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        }
        return stations;
    }

    public void removeSection(Section section) {
        this.sections.remove(section);
    }

    public void removeSection(int index) {
        this.sections.remove(index);
    }

}
