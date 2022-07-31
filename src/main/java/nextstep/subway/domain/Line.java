package nextstep.subway.domain;

import nextstep.subway.exception.ExceptionMessage;
import nextstep.subway.exception.SubwayException;

import javax.persistence.*;
import java.util.*;

import static java.util.Objects.nonNull;

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

        Optional<Section> addingSection = sections.stream()
                .filter(section -> section.getUpStation().equals(newSection.getUpStation()))
                .findFirst();


        if (addingSection.isPresent()) {
            Section section = addingSection.get();
            validateSameDistanceBothSections(section, newSection);
            section.updateUpStationToDownStationOf(newSection);
        }

        sections.add(newSection);
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
            Section nextSection = getNextSectionOf(nextUpStation);
            if (nonNull(nextSection)) {
                stations.add(nextSection.getUpStation());
                nextUpStation = nextSection.getDownStation();
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
            Section frontSection = getFrontSectionOf(firstStation);
            if (nonNull(frontSection)) {
                firstStation = frontSection.getUpStation();
                firstSection = frontSection;
            } else {
                isFirst = true;
            }
        }
        return firstSection;
    }

    private Section getFrontSectionOf(Station frontStation) {
        return sections.stream()
                .filter(section -> section.getDownStation().equals(frontStation))
                .findFirst()
                .orElse(null);
    }

    private Section getNextSectionOf(Station nextUpStation) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(nextUpStation))
                .findFirst()
                .orElse(null);
    }

    public void removeSection(Station lastStation) {
        validateOnlyOneSection();
        validateIsLast(lastStation);
        sections.remove(sections.size() - 1);
    }

    private void validateOnlyOneSection() {
        if (sections.size() == 1) {
            throw new SubwayException(ExceptionMessage.ONLY_ONE_SECTION);
        }
    }

    private void validateIsLast(Station lastStation) {
        if (!lastSection().getDownStation().equals(lastStation)) {
            throw new SubwayException(ExceptionMessage.CANNOT_DELETE_SECTION);
        }
    }

    private Section lastSection() {
        return sections.get(sections.size() - 1);
    }
}
