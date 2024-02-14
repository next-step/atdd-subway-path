package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import nextstep.subway.domain.exception.LineException;

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
        if (sections.isEmpty()) {
            return new ArrayList<>();
        }
        List<Section> result = new LinkedList<>();
        Optional<Section> headSection = getHeadSection();
        if (headSection.isEmpty()) {
            return result;
        }
        result.add(headSection.get());
        Section _headSection = getHeadSection().get();
        while (!_headSection.isTail()) {
            Station downStation = _headSection.getDownStation();
            Section nextSection = searchSection(sections, downStation).orElseThrow(
                () -> new LineException(LineException.NOT_EXIST_SECTION));
            result.add(nextSection);
            _headSection = nextSection;
        }
        return result;
    }

    private Optional<Section> searchSection(List<Section> sections, Station downStation) {
        return sections.stream()
            .filter(section -> section.getUpStation().equals(downStation))
            .findFirst();
    }

    private Optional<Section> getHeadSection() {
        return this.sections.stream().filter(Section::isHead).findFirst();
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        if (sections.isEmpty()) {
            sections.add(Section.createHeadAndTailSection(this, upStation, downStation, distance));
            return;
        }
        Section headSection = getHeadSection().orElseThrow(
            () -> new LineException(LineException.NOT_EXIST_SECTION));
        Section tailSection = getTailSection();
        if (tailSection.isSameDownStation(upStation)) {
            addTail(upStation, downStation, distance, tailSection);
            return;
        }
        if (headSection.isSameUpStation(downStation)) {
            addHead(upStation, downStation, distance, headSection);
            return;
        }
        if (isNotExistsUpStationInSections(upStation)) {
            throw new LineException(LineException.NOT_ADDABLE_SECTION);
        }
        sections.stream().filter(section -> section.getUpStation().equals(upStation))
            .findFirst()
            .ifPresent(section -> {
                addMiddle(section, upStation, downStation, distance);
            });
    }

    private void addTail(Station upStation, Station downStation, int distance,
        Section tailSection) {
        validateSameDownStationNotExists(downStation);
        tailSection.changeTail(false);
        sections.add(Section.createTailSection(this, upStation, downStation, distance));
    }

    private void addHead(Station upStation, Station downStation, int distance,
        Section headSection) {
        validateSameUpStationNotExists(upStation);
        headSection.changeHead(false);
        sections.add(Section.createHeadSection(this, upStation, downStation, distance));
    }
    
    private boolean isNotExistsUpStationInSections(Station upStation) {
        return sections.stream().noneMatch(section -> section.getUpStation().equals(upStation));
    }

    private void addMiddle(Section targetSection, Station upStation, Station downStation,
        int distance) {
        validateSameDownStationNotExists(downStation);
        if (targetSection.isHead()) {
            targetSection.changeHead(false);
            addMiddleToSections(targetSection, downStation, distance,
                Section.createHeadSection(this, upStation, downStation, distance));
            return;
        }
        addMiddleToSections(targetSection, downStation, distance,
            new Section(this, upStation, downStation, distance, false, false));
    }

    private void addMiddleToSections(Section targetSection, Station downStation, int distance,
        Section upStation) {
        targetSection.changeUpStation(downStation);
        targetSection.changeDistance(targetSection.getDistance() - distance);
        sections.add(sections.indexOf(targetSection),
            upStation);
    }

    private void validateSameUpStationNotExists(Station upStation) {
        if (getSections().stream()
            .anyMatch(section -> section.getUpStation().equals(upStation))) {
            throw new LineException(LineException.ALREADY_REGISTERED_STATION_EXCEPTION);
        }
    }

    private void validateSameDownStationNotExists(Station downStation) {
        if (getSections().stream()
            .anyMatch(section -> section.getDownStation().equals(downStation))) {
            throw new LineException(LineException.ALREADY_REGISTERED_STATION_EXCEPTION);
        }
    }

    private Section getTailSection() {
        return this.sections.stream().filter(Section::isTail).findFirst()
            .orElseThrow(() -> new LineException(LineException.NOT_EXIST_SECTION));
    }

    public Set<Station> getStations() {
        Set<Station> stations = new HashSet<>();
        for (Section section : sections) {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        }
        return stations;
    }

    public void removeSection(int index) {
        this.sections.remove(index);
        if (sections.size() == 1) {
            sections.get(0).changeTail(true);
        }
    }

}
