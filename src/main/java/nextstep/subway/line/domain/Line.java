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

        if (sections.isEmpty()) {
            return Arrays.asList();
        }

        List<Station> stations = new ArrayList<>();
        Station downStation = findUpStation();
        stations.add(downStation);

        while (downStation != null) {
            Station finalDownStation = downStation;
            Optional<Section> nextLineStation = sections.stream()
                    .filter(it -> it.getUpStation() == finalDownStation)
                    .findFirst();
            if (!nextLineStation.isPresent()) {
                break;
            }
            downStation = nextLineStation.get().getDownStation();
            stations.add(downStation);
        }

        return stations;
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

        Section section = new Section(this, upStation, downStation, distance);

        if (sections.size() == 0) {
            sections.add(section);
            return;
        }

        boolean existUpStation = getStations().stream().anyMatch(it -> it == upStation);
        boolean existDownStation = getStations().stream().anyMatch(it -> it == downStation);

        if (!existUpStation && !existDownStation) {
            throw new RuntimeException("노선에 상행역 또는 하행역이 등록되어 있어야 등록할 수 있습니다.");
        }

        boolean sameUpDownStation = sections.stream().anyMatch(it -> it.getUpStation() == upStation && it.getDownStation() == downStation);
        if (sameUpDownStation) {
            throw new RuntimeException("기존 구간과 같은 상행역 / 하행역을 가지는 노선은 등록할 수 없습니다.");
        }

        Optional<Section> sameUpStationSection = sections.stream()
                .filter(it -> it.getUpStation() == upStation)
                .findFirst();
        if (sameUpStationSection.isPresent()) {
            addSectionBetweenExist(sameUpStationSection.get(), section);
            return;
        }

        sections.add(section);
    }

    private void addSectionBetweenExist(Section existSection, Section newSection) {
        int existSectionDistance = existSection.getDistance();
        int newSectionDistance = newSection.getDistance();
        if (newSectionDistance >= existSectionDistance) {
            throw new RuntimeException("새로운 구간의 길이는 기존 구간의 길이보다 클 수 없습니다.");
        }

        Section newFirstSection = new Section(this, existSection.getUpStation(), newSection.getDownStation(), newSectionDistance);
        Section newSecondSection = new Section(this
                , newSection.getDownStation()
                , existSection.getDownStation()
                , existSectionDistance - newSectionDistance);

        sections.remove(existSection);
        sections.add(newFirstSection);
        sections.add(newSecondSection);
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
