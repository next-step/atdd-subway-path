package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Line extends BaseEntity {
    private static final int MIN_SIZE = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
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

    public Line(String name, String color, Section section) {
        this.name = name;
        this.color = color;
        this.sections.add(section);
        section.setLine(this);
    }

    public void updateLine(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void addSection(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            section.setLine(this);
            return;
        }

        Station upStation = section.getUpStation();
        Station downStation = section.getDownStation();
        int newDistance = section.getDistance();

        List<Station> stations = getStations();

        boolean hasUpStation = stations.contains(upStation);
        boolean hasDownStation = stations.contains(downStation);

        if (hasUpStation && hasDownStation) {
            throw new IllegalArgumentException("");
        }

        if (!hasUpStation && !hasDownStation) {
            throw new IllegalArgumentException("");
        }

        if (hasUpStation) {
            int index = indexOfUpStation(upStation);

            if(index != -1){
                Section oldSection = sections.get(index);
                oldSection.updateSection(downStation, oldSection.getDownStation(), oldSection.getDistance() - newDistance);
            }
        }

        if(hasDownStation) {
            int index = indexOfDownStation(downStation);

            if(index != -1){
                Section oldSection = sections.get(index);
                oldSection.updateSection(oldSection.getUpStation(), upStation, oldSection.getDistance() - newDistance);
            }
        }

        sections.add(section);
        section.setLine(this);
    }

    private int indexOfUpStation(Station station) {
        for (int i = 0; i < sections.size(); i++) {
            Station upStation = sections.get(i).getUpStation();
            if (upStation.equals(station)) {
                return i;
            }
        }
        return -1;
    }

    private int indexOfDownStation(Station station) {
        for (int i = 0; i < sections.size(); i++) {
            Station downStation = sections.get(i).getDownStation();
            if (downStation.equals(station)) {
                return i;
            }
        }
        return -1;
    }

    public void removeStation(Station newStation) {
        if (sections.size() == MIN_SIZE) {
            throw new IllegalArgumentException("구간이 1개일 경우 삭제가 불가능합니다.");
        }
        List<Station> stations = getStations();
        if (!stations.get(stations.size() - 1).equals(newStation)) {
            throw new IllegalArgumentException("마지막 역만 삭제가 가능합니다.");
        }
        sections.remove(sections.size() - 1);
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
        List<Section> sorted = sort();
        List<Station> stations = sorted.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());

        stations.add(sorted.get(sorted.size() - 1).getDownStation());

        return stations;
    }

    private List<Section> sort() {
        List<Section> sorted = new ArrayList<>();
        Station first = findFirst();
        Station last = findLast();

        Station now = first;

        do {
            Section section = find(now);
            sorted.add(section);
            now = section.getDownStation();
        } while (!now.equals(last));

        return sorted;
    }

    private Section find(Station station) {
        return sections.stream()
                .filter(section -> station.equals(section.getUpStation()))
                .findFirst()
                .get();
    }

    private Station findFirst() {
        List<Station> upStations = getUpStations();
        List<Station> downStations = getDownStations();

        return upStations.stream()
                .filter(station -> !downStations.contains(station))
                .findFirst()
                .get();
    }

    private Station findLast() {
        List<Station> upStations = getUpStations();
        List<Station> downStations = getDownStations();

        return downStations.stream()
                .filter(station -> !upStations.contains(station))
                .findFirst()
                .get();
    }

    private List<Station> getUpStations() {
        return sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
    }

    private List<Station> getDownStations() {
        return sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
    }
}
