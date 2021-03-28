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

    @Embedded
    private Sections sections = new Sections();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        sections.addSection(new Section(this, upStation, downStation, distance));
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

    public Sections getSections() {
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
            Optional<Section> nextLineStation = sections.findSectionByUpStation(finalDownStation);
            if (!nextLineStation.isPresent()) {
                break;
            }
            downStation = nextLineStation.get().getDownStation();
            stations.add(downStation);
        }

        return stations;
    }

    private Station findUpStation() {
        Station downStation = sections.findFirstSection().getUpStation();
        while (downStation != null) {
            Station finalDownStation = downStation;
            Optional<Section> nextLineStation = sections.findSectionByDownStation(finalDownStation);
            if (!nextLineStation.isPresent()) {
                break;
            }
            downStation = nextLineStation.get().getUpStation();
        }

        return downStation;
    }

    private Station findDownStation() {
        Station upStation = sections.findFirstSection().getDownStation();
        while (upStation != null) {
            Station finalUpStation = upStation;
            Optional<Section> nextLineStation = sections.findSectionByUpStation(finalUpStation);
            if (!nextLineStation.isPresent()) {
                break;
            }
            upStation = nextLineStation.get().getDownStation();
        }

        return upStation;
    }

    public void addSection(Station upStation, Station downStation, int distance) {

        Section section = new Section(this, upStation, downStation, distance);

        if (sections.size() == 0) {
            sections.addSection(section);
            return;
        }

        boolean existUpStation = getStations().stream().anyMatch(it -> it.equals(upStation));
        boolean existDownStation = getStations().stream().anyMatch(it -> it.equals(downStation));

        if (!existUpStation && !existDownStation) {
            throw new RuntimeException("노선에 상행역 또는 하행역이 등록되어 있어야 등록할 수 있습니다.");
        }

        if (existUpStation && existDownStation) {
            throw new RuntimeException("기존 구간과 같은 상행역 / 하행역을 가지는 노선은 등록할 수 없습니다.");
        }

        Optional<Section> sameUpStationSection = sections.findSectionByUpStation(upStation);
        if (sameUpStationSection.isPresent()) {
            addSectionBetweenExist(sameUpStationSection.get(), section);
            return;
        }

        sections.addSection(section);
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

        sections.removeSection(existSection);
        sections.addSection(newFirstSection);
        sections.addSection(newSecondSection);
    }

    public void removeSection(Station station) {
        if (sections.size() <= 1) {
            throw new RuntimeException("구간이 1개 이하일 경우 삭제할 수 없습니다.");
        }

        boolean existStation = getStations().stream().anyMatch(it -> it == station);
        if (!existStation) {
            throw new RuntimeException("존재하지 않는 역은 삭제할 수 없습니다.");
        }

        // 상행 종점역 제거
        Optional.of(findUpStation())
                .filter(station::equals)
                .ifPresent(sections::removeSectionByUpStation);

        // 하행 종점역 제거
        Optional.of(findDownStation())
                .filter(station::equals)
                .ifPresent(sections::removeSectionByDownStation);


        // 종점이 아닌역 제거
        removeSectionInMiddle(station);
    }

    private void removeSectionInMiddle(Station station) {

        Optional<Section> frontSectionOpt = sections.findSectionByDownStation(station);
        Optional<Section> backSectionOpt = sections.findSectionByUpStation(station);

        if (frontSectionOpt.isPresent() && backSectionOpt.isPresent()) {
            Section frontSection = frontSectionOpt.get();
            Section backSection = backSectionOpt.get();

            Section newSection = new Section(this
                    , frontSection.getUpStation()
                    , backSection.getDownStation()
                    , frontSection.getDistance() + backSection.getDistance());

            sections.removeSection(frontSection);
            sections.removeSection(backSection);
            sections.addSection(newSection);
        }
    }
}

