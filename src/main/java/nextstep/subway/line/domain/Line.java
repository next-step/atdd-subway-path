package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.*;

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

    public void addSection(Station upStation, Station downStation, int distance) {
        if (getStations().size() == 0) {
            sections.add(new Section(this, upStation, downStation, distance));
            return;
        }
        checkSectionAddValidity(upStation, downStation);
        if (newUpDownStationIsLastStation(upStation, downStation)) {
            sections.add(new Section(this, upStation, downStation, distance));
            return;
        }
        addNewSectionsFromOldSection(upStation, downStation, distance);
    }

    private void addNewSectionsFromOldSection(Station upStation, Station downStation, int distance) {
        Section oldSection = findSection(upStation, downStation);
        int oldDistance = oldSection.getDistance();
        checkSectionDistanceValidty(oldDistance, distance);
        removeSection(oldSection.getDownStation().getId());
        if (oldSection.getUpStation() == upStation) {
            sections.add(new Section(this, upStation, downStation, distance));
            sections.add(new Section(this, downStation, oldSection.getDownStation(), oldDistance - distance));
        }
        sections.add(new Section(this, oldSection.getUpStation(), upStation, oldDistance - distance));
        sections.add(new Section(this, upStation, downStation, distance));
    }

    private void checkSectionDistanceValidty(int oldDistance, int distance) {
        if (oldDistance <= distance) {
            throw new RuntimeException("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음");
        }
    }

    private Section findSection(Station upStation, Station downStation) {
        Section sectionWithUpStation = getSections().stream().filter(it -> it.getUpStation().getId() == upStation.getId()).findFirst().orElse(null);
        Section sectionWithDownStation = getSections().stream().filter(it -> it.getDownStation().getId() == downStation.getId()).findFirst().orElse(null);
        if (sectionWithUpStation == null) {
            return sectionWithDownStation;
        }
        return sectionWithUpStation;
    }

    private boolean newUpDownStationIsLastStation(Station upStation, Station downStation) {
        if (getStations().get(0).getId() == downStation.getId()) {
            return true;
        }
        if (getStations().get(getStations().size()-1).getId() == upStation.getId()) {
            return true;
        }
        return false;
    }

    private void checkSectionAddValidity(Station upStation, Station downStation) {
        if (getStations().stream().noneMatch(it -> it.getId() == upStation.getId()) &&
                getStations().stream().noneMatch(it -> it.getId() == downStation.getId())) {
            throw new RuntimeException("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없습니다.");
        }
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        if (getSections().isEmpty()) {
            return stations;
        }

        Station downStation = findUpStation();
        stations.add(downStation);

        while (downStation != null) {
            Station finalDownStation = downStation;
            Optional<Section> nextLineStation = getSections().stream()
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
        Station downStation = getSections().get(0).getUpStation();
        while (downStation != null) {
            Station finalDownStation = downStation;
            Optional<Section> nextLineStation = getSections().stream()
                    .filter(it -> it.getDownStation() == finalDownStation)
                    .findFirst();
            if (!nextLineStation.isPresent()) {
                break;
            }
            downStation = nextLineStation.get().getUpStation();
        }
        return downStation;
    }

    public void removeSection(Long stationId) {
        checkSectionRemoveValidity(stationId);
        getSections().stream()
                .filter(it -> it.getDownStation().getId() == stationId)
                .findFirst()
                .ifPresent(it -> getSections().remove(it));
    }

    private void checkSectionRemoveValidity(Long stationId) {
        if (getSections().size() <= 1) {
            throw new RuntimeException();
        }
        if (getStations().get(getStations().size() - 1).getId() != stationId) {
            throw new RuntimeException("하행 종점역만 삭제가 가능합니다.");
        }
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
}
