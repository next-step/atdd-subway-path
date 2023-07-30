package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
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
        List<Section> sortSections = new ArrayList<>();
        Optional<Section> upSection = sections.stream()
                .filter(section -> !isDownStation(section.getUpStation()))
                .findFirst();
        while(upSection.isPresent()) {
            sortSections.add(upSection.get());
            upSection = findNextSection(upSection.get().getDownStation());
        }
        return sortSections;
    }

    public void addSections(Section section) {
        this.sections.add(section);
    }
    // 다음 역을 찾는 메소드
    private Optional<Section> findNextSection(Station downStation) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(downStation))
                .findFirst();
    }
    // 상행역으로 등록되어 있는지 찾는 메소드
    private boolean isUpStation(Station station) {
        return sections.stream().map(Section::getUpStation).collect(Collectors.toList()).contains(station);
    }

    // 하행역으로 등록되어있는지 찾는 메소드
    private boolean isDownStation(Station station) {
        return sections.stream().map(Section::getDownStation).collect(Collectors.toList()).contains(station);
    }

    public void addSection(Section section) {
        isAnyMatch(section);
        this.sections.add(section);
        if(isUpStation(section.getUpStation())) {
            updatePrevDownSection(section);
            return;
        }
        if(isDownStation(section.getDownStation())) {
            updatePrevUpSection(section);
            return;
        }
    }

    public void removeSection(Station station) {
        Section removedSection = this.sections
                .stream()
                .filter(section -> section.getDownStation().equals(station))
                .findFirst()
                .orElse(sections.get(0));
        this.sections.remove(removedSection);
    }

    // 추가되는 구간의 상행선 역이 기존 구간의 상행선으로 등록되어있으면 구간 사이에 들어간다. 기존 구간은 하행선 역으로 변경
    private void updatePrevDownSection(Section newSection) {
        Section downSection = sections.stream()
                .filter(section -> section.getUpStation().equals(newSection.getUpStation()))
                .findFirst()
                .get();
        isEqualException(downSection.getDownStation(), newSection.getDownStation());
        downSection.updateSection(newSection, true);

    }

    // 추가되는 구간이 하행선 역이 기존 구간의 하행선으로 등록되어있으면 구간 사이에 들어간다. 기존 구간은 상행선 역으로 변경
    private void updatePrevUpSection(Section newSection) {
        Section upSection = sections.stream()
                .filter(section -> section.getUpStation().equals(newSection.getUpStation()))
                .findFirst()
                .get();
        isEqualException(upSection.getUpStation(), newSection.getUpStation());
        upSection.updateSection(newSection, false);
    }

    private void isEqualException(Station station, Station newStation) {
        if(station.equals(newStation)) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 등록되어 있다면 추가할 수 없다.");
        }
    }


    private void isAnyMatch(Section section) {
        boolean isMatch = sections.stream().anyMatch(section1 -> isUpStation(section.getDownStation()));
        isMatch = sections.stream().anyMatch(section1 -> isDownStation(section.getUpStation()));
        if(!isMatch) {
            throw new IllegalArgumentException("상행선과 하행선 둘 중 하나도 포함되어 있지 않은 구간을 추가할 수 없다.");
        }
    }

}
