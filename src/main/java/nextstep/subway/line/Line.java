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

    public void addSection(Section section) {
        Optional<Section> sameUpStationSection = sections.stream()
                .filter(savedSection -> savedSection.isSameUpStation(section.getUpStation()))
                .findFirst();
        List<Station> stations = sections.stream()
                .flatMap(savedSection -> Stream.of(savedSection.getUpStation(), savedSection.getDownStation()))
                .collect(Collectors.toList());
        if (stations.contains(section.getDownStation()) && stations.contains(section.getUpStation())) {
            throw new AlreadyConnectedException();
        }
        if (sameUpStationSection.isPresent()) {
            Section targetSection = sameUpStationSection.get();
            if (section.getDistance() >= targetSection.getDistance()) {
                throw new InvalidDistanceException();
            }
            sections.remove(targetSection);
            Station targetDownStation = targetSection.getDownStation();
            sections.add(section);
            sections.add(new Section(section.getDownStation(), targetDownStation,
                    targetSection.getDistance() - section.getDistance()));
            return;
        }
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }
        Section topSection = sections.get(0);
        if (topSection.getUpStation().equals(section.getDownStation())) {
            List<Section> targetSections = new ArrayList<>();
            targetSections.add(section);
            targetSections.addAll(sections);
            sections = targetSections;
            return;
        }
        if (!sections.get(sections.size() - 1).canLink(section)) {
            throw new MismatchedUpstreamStationException();
        }
        sections.add(section);
    }

    public void deleteStation(Station downStation) {
        if (sections.size() == 1) {
            throw new SingleSectionRemovalException();
        }
        int lastIndex = sections.size() - 1;
        Section section = sections.get(lastIndex);
        if (section.isSameDownStation(downStation)) {
            sections.remove(section);
            return;
        }
        throw new NonDownstreamTerminusException();
    }
}
