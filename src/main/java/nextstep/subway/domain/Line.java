package nextstep.subway.domain;

import nextstep.subway.exception.ExceptionMessage;
import nextstep.subway.exception.SubwayException;

import javax.persistence.*;
import java.util.*;

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
        if (sections.isEmpty()) {
            sections.add(newSection);
            return;
        }

        validateAllStationsAlreadyExist(newSection);
        validateAllStationsDoesNotExist(newSection);

        addMiddleSectionWhenSameUpStation(newSection);
        addMiddleSectionWhenSameDownStation(newSection);

        sections.add(newSection);
    }

    private void addMiddleSectionWhenSameUpStation(Section newSection) {
        Optional<Section> addingFrontSection = sections.stream()
                .filter(section -> section.getUpStation().equals(newSection.getUpStation()))
                .findAny();

        if (addingFrontSection.isPresent()) {
            Section section = addingFrontSection.get();
            validateSameDistanceBothSections(section, newSection);
            section.updateUpStationToDownStationOf(newSection);
        }
    }

    private void addMiddleSectionWhenSameDownStation(Section newSection) {
        Optional<Section> addingBehindSection = sections.stream()
                .filter(section -> section.getDownStation().equals(newSection.getDownStation()))
                .findAny();

        if (addingBehindSection.isPresent()) {
            Section section = addingBehindSection.get();
            validateSameDistanceBothSections(section, newSection);
            section.updateDownStationToUpStationWhenAdd(newSection);
        }
    }

    private void validateSameDistanceBothSections(Section section, Section newSection) {
        if (newSection.sameOrBiggerThen(section)) {
            throw new SubwayException(ExceptionMessage.TOO_LONG_DISTANCE_OF_SECTION);
        }
    }

    private void validateAllStationsAlreadyExist(Section newSection) {
        Station upStation = newSection.getUpStation();
        Station downStation = newSection.getDownStation();
        List<Station> stations = getStations();

        if (stations.containsAll(Arrays.asList(upStation, downStation))) {
            throw new SubwayException(ExceptionMessage.ALREADY_EXIST_STATIONS);
        }
    }

    private void validateAllStationsDoesNotExist(Section newSection) {
        Station upStation = newSection.getUpStation();
        Station downStation = newSection.getDownStation();
        List<Station> stations = getStations();

        if (!stations.contains(upStation) && !stations.contains(downStation)) {
            throw new SubwayException(ExceptionMessage.DOES_NOT_EXIST_STATIONS);
        }
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = new ArrayList<>();
        Section firstSection = getFirstSection();
        stations.add(firstSection.getUpStation());

        boolean isLast = false;
        Station nextUpStation = firstSection.getDownStation();
        while (!isLast) {
            Optional<Section> behindSection = getBehindSectionOf(nextUpStation);
            if (behindSection.isPresent()) {
                stations.add(behindSection.get().getUpStation());
                nextUpStation = behindSection.get().getDownStation();
            } else {
                isLast = true;
            }
        }

        stations.add(nextUpStation);

        return stations;
    }

    private Section getFirstSection() {
        Section firstSection = sections.get(0);

        boolean isFirst = false;
        Station firstStation = firstSection.getUpStation();
        while (!isFirst) {
            Optional<Section> frontSection = getFrontSectionOf(firstStation);
            if (frontSection.isPresent()) {
                firstStation = frontSection.get().getUpStation();
                firstSection = frontSection.get();
            } else {
                isFirst = true;
            }
        }
        return firstSection;
    }

    public void removeSection(Station station) {
        validateOnlyOneSection();
        validateIsExist(station);

        Optional<Section> upSection = getFrontSectionOf(station);
        Optional<Section> downSection = getBehindSectionOf(station);

        if (upSection.isPresent() && downSection.isPresent()) {
            upSection.get().updateDownStationToUpStationWhenRemove(downSection.get());
            sections.remove(downSection.get());
        } else if (upSection.isPresent()) {
            sections.remove(upSection.get());
        } else downSection.ifPresent(section -> sections.remove(section));
    }

    private void validateOnlyOneSection() {
        if (sections.size() == 1) {
            throw new SubwayException(ExceptionMessage.ONLY_ONE_SECTION);
        }
    }

    private void validateIsExist(Station station) {
        List<Station> stations = getStations();
        if (!stations.contains(station)) {
            throw new SubwayException(ExceptionMessage.DOES_NOT_EXIST_STATION);
        }
    }

    private Optional<Section> getFrontSectionOf(Station station) {
        return sections.stream()
                .filter(section -> section.getDownStation().equals(station))
                .findAny();
    }

    private Optional<Section> getBehindSectionOf(Station station) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(station))
                .findAny();
    }
}
