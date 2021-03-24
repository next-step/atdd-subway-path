package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Section> sections = new ArrayList<>();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        sections.add(new Section(this, upStation, downStation, distance));
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id) && Objects.equals(name, line.name) && Objects.equals(color, line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }

    public List<Station> getStations() {
        List<Station> allDownStations = getAllDownStations();
        allDownStations.add(0, findUpStation());
        return allDownStations;
    }

    private Station findUpStation() {
        Station downStation = sections.get(0).getUpStation();
        while (downStation != null) {
            Station finalDownStation = downStation;
            Optional<Section> nextLineStation = sections.stream()
                    .filter(it -> it.getDownStation() == finalDownStation)
                    .findFirst();
            if (!nextLineStation.isPresent()) {
                break;
            }
            downStation = nextLineStation.get().getUpStation();
        }

        return downStation;
    }

    private Station findDownStation() {
        Station upStation = sections.get(0).getDownStation();
        while (upStation != null) {
            Station finalUpStation = upStation;
            Optional<Section> nextLineStation = sections.stream()
                    .filter(it -> it.getUpStation() == finalUpStation)
                    .findFirst();
            if (!nextLineStation.isPresent()) {
                break;
            }
            upStation = nextLineStation.get().getDownStation();
        }

        return upStation;
    }

    private List<Station> getAllUpStations() {
        return sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
    }

    private List<Station> getAllDownStations() {
        return sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        if (sections.size() == 0) {
            sections.add(new Section(this, upStation, downStation, distance));
            return;
        }

        boolean isNotValidUpStation = findDownStation() != upStation;
        if (isNotValidUpStation) {
            throw new RuntimeException("상행역은 하행 종점역이어야 합니다.");
        }

        boolean isDownStationExisted = getStations().stream().anyMatch(it -> it == downStation);
        if (isDownStationExisted) {
            throw new RuntimeException("하행역이 이미 등록되어 있습니다.");
        }

        sections.add(new Section(this, upStation, downStation, distance));
    }


    public void removeSection(Station station) {
        if (sections.size() <= 1) {
            throw new RuntimeException("구간이 1개 이하일 경우 삭제할 수 없습니다.");
        }

        Station downStation = findDownStation();
        if (downStation != station) {
            throw new RuntimeException("하행 종점역만 삭제가 가능합니다.");
        }

        sections.stream()
                .filter(section -> section.getDownStation() == downStation)
                .findFirst()
                .ifPresent(section -> sections.remove(section));
    }
}
