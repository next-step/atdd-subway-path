package nextstep.subway.line;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import nextstep.subway.station.Station;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 20, nullable = false)
    private String name;
    private String color;
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "line_section", joinColumns = @JoinColumn(name = "line_id"))
    private List<Section> sections = new ArrayList<>();

    public Line() {
    }

    public Line(String name, String color, List<Section> stations) {
        this.name = name;
        this.color = color;
        this.sections = stations;
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
        return sections;
    }

    public List<Station> getStations() {
        return sections.stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .distinct()
                .collect(Collectors.toList());
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }

    private static boolean sameDownStationOfTopStation(Section newSection, Section topSection) {
        return topSection.getUpStation().equals(newSection.getDownStation());
    }

    public void addSection(Section newSection) {
        if (sections.isEmpty()) {
            sections.add(newSection);
            return;
        }
        checkSection(newSection);
        Optional<Section> sameUpStationSection = getSameUpStationSection(newSection);
        if (sameUpStationSection.isPresent()) {
            insertCenter(newSection, sameUpStationSection.get());
            return;
        }
        Optional<Section> targetSection = sections.stream()
                .filter(section -> section.isSameDownStation(newSection.getDownStation()))
                .findFirst();
        if (targetSection.isPresent()) {
            insertByDownStation(newSection, targetSection.get());
            return;
        }
        Section topSection = getTopSection();
        if (sameDownStationOfTopStation(newSection, topSection)) {
            insertTop(newSection);
            return;
        }
        sections.add(newSection);
    }

    private void insertByDownStation(Section inserSection, Section targetSection) {
        int targetSectionDistance = targetSection.getDistance();
        int insertSectionDistance = inserSection.getDistance();
        if (targetSectionDistance <= insertSectionDistance) {
            throw new InvalidDistanceException();
        }
        sections.remove(targetSection);
        Station targetUpStation = targetSection.getUpStation();
        sections.add(new Section(targetUpStation, inserSection.getUpStation(),
                targetSectionDistance - insertSectionDistance));
        sections.add(inserSection);
    }

    private void checkSection(Section newSection) {
        List<Station> stations = getStations();
        boolean doesContainsDownStation = stations.contains(newSection.getDownStation());
        boolean doesContainsUpStation = stations.contains(newSection.getUpStation());
        if (doesContainsDownStation && doesContainsUpStation) {
            throw new AlreadyConnectedException();
        }
        if (!sections.isEmpty() && !doesContainsDownStation && !doesContainsUpStation) {
            throw new MissingStationException();
        }
    }

    private void insertTop(Section section) {
        List<Section> targetSections = new ArrayList<>();
        targetSections.add(section);
        targetSections.addAll(sections);
        sections = targetSections;
    }

    private Section getTopSection() {
        return sections.get(0);
    }

    private void insertCenter(Section insertSection, Section targetSection) {
        if (insertSection.getDistance() >= targetSection.getDistance()) {
            throw new InvalidDistanceException();
        }
        sections.remove(targetSection);
        Station targetDownStation = targetSection.getDownStation();
        sections.add(insertSection);
        sections.add(new Section(insertSection.getDownStation(), targetDownStation,
                targetSection.getDistance() - insertSection.getDistance()));
    }

    private Optional<Section> getSameUpStationSection(Section section) {
        return sections.stream()
                .filter(savedSection -> savedSection.isSameUpStation(section.getUpStation()))
                .findFirst();
    }

    public void deleteSection(Station bottomStation) {
        if (sections.size() == 1) {
            throw new SingleSectionRemovalException();
        }
        int lastIndex = sections.size() - 1;
        Section section = sections.get(lastIndex);
        if (section.isSameDownStation(bottomStation)) {
            sections.remove(section);
            return;
        }
        throw new NonDownstreamTerminusException();
    }
}
