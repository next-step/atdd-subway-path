package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    protected Line() {
    }

    public Line(String name, String color) {
        this(null, name, color);
    }

    public Line(Long id, String name, String color) {
        this.id = id;
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

    public void addSection(Section newSection) {
        if (this.sections.size() == 0) {
            this.sections.add(new Section(this, newSection.getUpStation(), newSection.getDownStation(), newSection.getDistance()));
            return;
        }
        this.sections.stream()
                .filter(section ->
                        getFinalUpStation().equals(newSection.getDownStation())
                        || getFinalDownStation().equals(newSection.getUpStation())
                        || section.getUpStation().equals(newSection.getUpStation())
                        || section.getDownStation().equals(newSection.getDownStation()))
                .findFirst()
                .ifPresentOrElse(section -> {
                    // 상행, 하행 중복된 구간을 추가하는 경우
                    if (section.equals(newSection)) {
                        throw new IllegalArgumentException("상행, 하행이 중복된 구간을 등록할 수 없습니다.");
                    }
                    // 상행 종점에 추가된 경우
                    if (getFinalUpStation().equals(newSection.getDownStation())) {
                        this.sections.add(new Section(this, newSection.getUpStation(), section.getUpStation(), newSection.getDistance()));
                        this.sections.add(new Section(this, section.getUpStation(), section.getDownStation(), section.getDistance()));
                    }
                    // 하행 종점에 추가된 경우
                    if (getFinalDownStation().equals(newSection.getUpStation())) {
                        this.sections.add(new Section(this, section.getUpStation(), section.getDownStation(), section.getDistance()));
                        this.sections.add(new Section(this, section.getDownStation(), newSection.getDownStation(), newSection.getDistance()));
                    }
                    // 상행에 추가한 경우
                    if (section.getUpStation().equals(newSection.getUpStation())) {
                        if (section.getDistance() <= newSection.getDistance()) {
                            throw new IllegalArgumentException("기존 구간의 길이보다 긴 구간은 추가할 수 없습니다.");
                        }
                        this.sections.add(new Section(this, section.getUpStation(), newSection.getDownStation(), newSection.getDistance()));
                        this.sections.add(new Section(this, newSection.getDownStation(), section.getDownStation(), section.getDistance() - newSection.getDistance()));
                    }
                    // 하행에 추가한 경우
                    if (section.getDownStation().equals(newSection.getDownStation())) {
                        if (section.getDistance() <= newSection.getDistance()) {
                            throw new IllegalArgumentException("기존 구간의 길이보다 긴 구간은 추가할 수 없습니다.");
                        }
                        this.sections.add(new Section(this, section.getUpStation(), newSection.getUpStation(), section.getDistance() - newSection.getDistance()));
                        this.sections.add(new Section(this, newSection.getUpStation(), section.getDownStation(), newSection.getDistance()));
                    }
                    this.sections.remove(section);
                }, () -> {
                    // 상,하행 둘다 존재하지 않는 역인 경우
                    throw new IllegalArgumentException("노선에 존재하지 않는 구간은 추가할 수 없습니다.");
                });
    }

    public List<Station> getStations() {
        if (this.sections.isEmpty()) {
            return Collections.emptyList();
        }
        Stations stations = Stations.of(getFinalUpStation());
        while (!stations.isFinalDownStationEqualTo(getFinalDownStation())) {
            // 상행역 -> 하행역
            this.sections.stream()
                    .filter(section -> stations.isFinalDownStationEqualTo(section.getUpStation()))
                    .findFirst()
                    .map(Section::getDownStation)
                    .ifPresent(stations::add);
        }
        return stations.get();
    }

    private Station getFinalUpStation() {
        return sections.stream()
                .map(Section::getUpStation)
                .filter(upStation -> !getDownStations().contains(upStation))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("상행 종점역이 존재하지 않습니다."));
    }

    private Station getFinalDownStation() {
        return sections.stream()
                .map(Section::getDownStation)
                .filter(downStation -> !getUpStations().contains(downStation))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("하행 종점역이 존재하지 않습니다."));
    }

    public List<Station> getDownStations() {
        return this.sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
    }

    public List<Station> getUpStations() {
        return this.sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
    }

    public List<Integer> getSectionDistances() {
        return sections.stream()
                .map(Section::getDistance)
                .collect(Collectors.toList());
    }

    public void removeSection(Station station) {
        if (!sections.get(sections.size() - 1).getDownStation().equals(station)) {
            throw new IllegalArgumentException();
        }
        this.sections.remove(this.sections.size() - 1);
    }

    public void removeSection(String stationName) {
        sections.stream()
                .filter(section -> section.getDownStation().getName().equals(stationName)
                        || section.getUpStation().getName().equals(stationName))
                .findFirst()
                .ifPresent(section -> sections.remove(section));
    }

    public void updateLine(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }
}
