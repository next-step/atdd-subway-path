package nextstep.subway.domain;

import nextstep.subway.exception.BadRequestException;
import nextstep.subway.exception.ERROR_CODE;

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

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
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

    public void updateLine(String name, String color) {
        this.name = name;
        this.color = color;
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
        if(isUpStation(section.getUpStation())) {
            updateUpSection(section);
            this.sections.add(section);
            return;
        }
        if(isDownStation(section.getDownStation())) {
            updateDownSection(section);
            this.sections.add(section);
            return;
        }
        this.sections.add(section);
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
    private void updateUpSection(Section newSection) {
        Section prevUpSection = sections.stream()
                .filter(section -> section.getUpStation().equals(newSection.getUpStation()))
                .findFirst()
                .get();
        isEqualException(prevUpSection.getDownStation(), newSection.getDownStation());
        // 기존에 있던 상행-하행에서 상행이 동일할 시 기존 상행이 새로운 역으로 바뀐다.
        prevUpSection.updateSection(newSection, true);

    }

    // 추가되는 구간이 하행선 역이 기존 구간의 하행선으로 등록되어있으면 구간 사이에 들어간다. 기존 구간은 상행선 역으로 변경
    private void updateDownSection(Section newSection) {
        Section prevDownSection = sections.stream()
                .filter(section -> section.getDownStation().equals(newSection.getDownStation()))
                .findFirst()
                .get();
        isEqualException(prevDownSection.getUpStation(), newSection.getUpStation());
        prevDownSection.updateSection(newSection, false);
    }

    private void isEqualException(Station station, Station newStation) {
        if(station.equals(newStation)) {
            throw new BadRequestException(ERROR_CODE.DUPLICATED_STATION);
        }
    }


    private void isAnyMatch(Section newSection) {
        Station downStation = newSection.getDownStation();
        Station upStation = newSection.getUpStation();
        List<Station> downStations = sections.stream().map(Section::getDownStation).collect(Collectors.toList());
        List<Station> upStations = sections.stream().map(Section::getUpStation).collect(Collectors.toList());
        downStations.addAll(upStations);
        if(!(downStations.contains(downStation) || downStations.contains(upStation))) {
            throw new BadRequestException(ERROR_CODE.NOT_FOUND_ANY_STATION);
        }
    }

}
