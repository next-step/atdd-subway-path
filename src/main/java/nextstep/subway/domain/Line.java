package nextstep.subway.domain;

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
            valifateSameDistanceBothSections(section, newSection);
            section.updateUpStationToDownStationOf(newSection);
        }

        sections.add(newSection);
    }

    private void valifateSameDistanceBothSections(Section section, Section newSection) {
        if (section.sameDistanceWith(newSection)) {
            throw new IllegalArgumentException();
        }
    }

    private void validateAllStationsAlreadyExist(Section newSection) {
        Station upStation = newSection.getUpStation();
        Station downStation = newSection.getDownStation();
        List<Station> stations = getStations();

        if (stations.containsAll(Arrays.asList(upStation, downStation))) {
            throw new IllegalArgumentException();
        }
    }

    private void validateAllStationsDoesNotExist(Section newSection) {
        Station upStation = newSection.getUpStation();
        Station downStation = newSection.getDownStation();
        List<Station> stations = getStations();

        if (!stations.contains(upStation) && !stations.contains(downStation)) {
            throw new IllegalArgumentException();
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
