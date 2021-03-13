package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.exception.DownStationExistedException;
import nextstep.subway.line.exception.HasNoneOrOneSectionException;
import nextstep.subway.line.exception.NotLastStationException;
import nextstep.subway.line.exception.NotValidUpStationException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
        if (getSections().isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = new ArrayList<>();
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

    public void addSection(Station upStation, Station downStation, int distance) {
        if (getStations().isEmpty()) {
            getSections().add(new Section(this, upStation, downStation, distance));
            return;
        }

        boolean isNotValidUpStation = getStations().get(getStations().size() - 1) != upStation;
        if (isNotValidUpStation) {
            throw new NotValidUpStationException();
        }

        boolean isDownStationExisted = getStations().stream().anyMatch(it -> it == downStation);
        if (isDownStationExisted) {
            throw new DownStationExistedException();
        }

        getSections().add(new Section(this, upStation, downStation, distance));
    }

    public void removeSection(Long stationId) {
        if (getSections().size() <= 1) {
            throw new HasNoneOrOneSectionException();
        }

        boolean isNotValidUpStation = !getStations().get(getStations().size() - 1).getId().equals(stationId);
        if (isNotValidUpStation) {
            throw new NotLastStationException();
        }

        getSections().stream()
                .filter(it -> it.getDownStation().getId().equals(stationId))
                .findFirst()
                .ifPresent(it -> getSections().remove(it));
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

}
