package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

    private Line() {
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

    public void addSection(Section newSection) {
//        if(!sections.isEmpty()) {
//            validateEqualUpAndDown(section);
//            validateAlreadyExist(section);
//        }

        Optional<Section> addingSection = sections.stream()
                .filter(section -> section.getUpStation().equals(newSection.getUpStation()))
                .findFirst();

        addingSection.ifPresent(section -> section.updateUpStationToDownStationOf(newSection));

        sections.add(newSection);
    }

//    private void validateEqualUpAndDown(Section section) {
//        if (!lastSection().getDownStation().equals(section.getUpStation())) {
//            throw new IllegalArgumentException("구간을 추가할 수 없습니다.");
//        }
//    }

//    private void validateAlreadyExist(Section section) {
//        if (getStations().contains(section.getDownStation())) {
//            throw new IllegalArgumentException("역이 이미 노선에 포함되어 있습니다.");
//        }
//    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
        stations.add(lastSection().getDownStation());
        return stations;
    }

    public void removeSection(Station lastStation) {
        validateOnlyOneSection();
        validateIsLast(lastStation);
        sections.remove(sections.size() - 1);
    }

    private void validateOnlyOneSection() {
        if (sections.size() == 1) {
            throw new IllegalArgumentException("한 개의 구간만이 존재합니다.");
        }
    }

    private void validateIsLast(Station lastStation) {
        if (!lastSection().getDownStation().equals(lastStation)) {
            throw new IllegalArgumentException("구간을 삭제할 수 없습니다.");
        }
    }

    private Section lastSection() {
        return sections.get(sections.size() - 1);
    }
}
