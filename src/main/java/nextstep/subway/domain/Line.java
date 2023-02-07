package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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
        return sections;
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        if (sections.isEmpty()) {
            extendSection(upStation, downStation, distance);
            return;
        }

        checkStationStatus(upStation, downStation);

        Section oldSection = findSameUpStation(upStation);
        if (oldSection != null) {
            splitSection(downStation, distance, oldSection);
            return;
        }

        extendSection(upStation, downStation, distance);
    }

    private Section findSameUpStation(Station upStation) {
        return sections.stream().filter(s -> s.getUpStation().equals(upStation))
            .findFirst()
            .orElse(null);
    }

    private void splitSection(Station downStation, int distance, Section oldSection) {
        Station oldUpstation = oldSection.getUpStation();
        Station oldDownStation = oldSection.getDownStation();
        int oldDistance = oldSection.getDistance();

        checkDistance(distance, oldDistance);

        sections.remove(oldSection);
        sections.addAll(List.of(
            new Section(this, oldUpstation, downStation, distance),
            new Section(this, downStation, oldDownStation, oldDistance - distance)
        ));
    }

    private void checkStationStatus(Station upStation, Station downStation) {
        List<Station> allStations = getAllStations();
        if (stationsAlreadyExist(upStation, downStation, allStations)) {
            throw new IllegalArgumentException("새로운 구간의 상행역과 하행역이 이미 모두 노선에 등록되어 있습니다.");
        }
        if (stationsNotExist(upStation, downStation, allStations)) {
            throw new IllegalArgumentException("새로운 구간의 상행역과 하행역이 모두 노선에 등록되어 있지 않습니다.");
        }
    }

    private boolean stationsNotExist(Station upStation, Station downStation, List<Station> allStations) {
        return !allStations.contains(upStation) && !allStations.contains(downStation);
    }

    private boolean stationsAlreadyExist(Station upStation, Station downStation, List<Station> allStations) {
        return allStations.contains(upStation) && allStations.contains(downStation);
    }

    private void checkDistance(int distance, int oldDistance) {
        if (distance >= oldDistance) throw new IllegalArgumentException("역 사이에 기존 역 사이 길이보다 크거나 같은 구간을 등록할 수 없습니다.");
    }

    public void extendSection(Station upStation, Station downStation, int distance) {
        this.sections.add(new Section(this, upStation, downStation, distance));
    }

    public List<Station> getAllStations() {
        return this.sections.stream()
            .map(s -> List.of(s.getUpStation(), s.getDownStation()))
            .flatMap(Collection::stream)
            .distinct()
            .collect(Collectors.toList());
    }

    public List<Station> getOrderedStations() {
        Map<Station, Station> stationMap = this.sections.stream()
            .collect(Collectors.toMap(Section::getUpStation, Section::getDownStation));

        List<Station> orderedStations = new ArrayList<>();
        Station upStation = findUpEndStation();
        orderedStations.add(upStation);

        while (true) {
            Station downStation = stationMap.getOrDefault(upStation, null);
            if (downStation == null) {
                break;
            }
            orderedStations.add(downStation);
            upStation = downStation;
        }

        return orderedStations;
    }

    private Station findUpEndStation() {
        List<Station> upStations = this.sections.stream()
            .map(Section::getUpStation)
            .collect(Collectors.toList());

        List<Station> downStations = this.sections.stream()
            .map(Section::getDownStation)
            .collect(Collectors.toList());

        return upStations.stream()
            .filter(s -> !downStations.contains(s))
            .findFirst()
            .orElse(null);
    }

    public void removeSection(Station downEndStation) {
        if (!sections.get(sections.size() - 1).getDownStation().equals(downEndStation)) {
            throw new IllegalArgumentException();
        }
        sections.remove(sections.size() - 1);
    }

    public void updateLine(String name, String color) {
        if (name != null) {
            this.name = name;
        }
        if (color != null) {
            this.color = color;
        }
    }
}
