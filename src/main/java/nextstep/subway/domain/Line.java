package nextstep.subway.domain;

import javax.persistence.*;
import java.util.*;
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
        return Collections.unmodifiableList(sections);
    }

    public void update(final String name, final String color) {
        if (name != null) {
            this.name = name;
        }

        if (color != null) {
            this.color = color;
        }
    }

    public void removeLastSection() {
        sections.remove(sections.size() - 1);
    }

    public List<Station> getStations() {
        if (hasNoSection()) {
            return Collections.emptyList();
        }

        return Collections.unmodifiableList(findAllStationsInOrder());
    }

    public boolean hasNoSection() {
        return sections.isEmpty();
    }

    private Station getFirstUpStation() {
        final List<Station> allDownStations = getAllDownStations();
        return sections.stream()
                .filter(v -> !allDownStations.contains(v.getUpStation()))
                .findFirst()
                .map(Section::getUpStation)
                .orElseThrow(IllegalStateException::new);
    }

    private List<Station> getAllDownStations() {
        return sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
    }

    private List<Station> findAllStationsInOrder() {
        final List<Station> stationList = new ArrayList<>();
        final List<Section> sectionList = getSections();

        Station target = getFirstUpStation();
        while (target != null) {
            stationList.add(target);
            target = findConnectedDownStation(sectionList, target);
        }

        return stationList;
    }

    private Station findConnectedDownStation(final List<Section> sectionList, final Station target) {
        return sectionList.stream()
                .filter(v -> v.getUpStation().equals(target))
                .map(Section::getDownStation)
                .findAny()
                .orElse(null);
    }

    public void addSection(final Section section) {
        sections.add(section);
    }

    public void addSection(final int index, final Section section) {
        sections.add(index, section);
    }

    public boolean isLastDownStation(final Station station) {
        final List<Station> stations = getStations();

        return stations.get(stations.size() - 1).equals(station);
    }

    public boolean isFirstStation(final Station station) {
        final List<Station> stations = getStations();
        return stations.get(0).equals(station);
    }

    public boolean containsStation(final Station station) {
        return getStations().contains(station);
    }
}
