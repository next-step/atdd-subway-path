package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.exception.AlreadyExistDownStationException;
import nextstep.subway.line.exception.NotLastStationException;
import nextstep.subway.line.exception.NotMatchedUpStationException;
import nextstep.subway.line.exception.TooLowLengthSectionsException;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.*;

@Entity
public class Line extends BaseEntity {
    public static final int SECTIONS_MIN_SIZE = 1;
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
        sections.add(Section.of(this, upStation, downStation, distance));
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }

    public void addSection(Section section) {
        if (getStations().isEmpty()) {
            sections.add(section);
            return;
        }

        validateAddSection(section);
        sections.add(section);
    }

    public void removeSection(Station station) {
        validateDeleteSection(station);

        final Section target = sections.stream()
            .filter(section -> section.getDownStation().equals(station))
            .findAny()
            .orElseThrow(RuntimeException::new);

        sections.remove(target);
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

    private void validateAddSection(Section section) {
        boolean isNotValidUpStation = findLastStation() != section.getUpStation();
        if (isNotValidUpStation) {
            throw new NotMatchedUpStationException();
        }

        boolean isDownStationExisted = getStations().contains(section.getDownStation());
        if (isDownStationExisted) {
            throw new AlreadyExistDownStationException();
        }
    }

    private void validateDeleteSection(Station station) {
        if (sections.size() <= SECTIONS_MIN_SIZE) {
            throw new TooLowLengthSectionsException(SECTIONS_MIN_SIZE);
        }

        if (isNotLastStation(station)) {
            throw new NotLastStationException(station.getName());
        }
    }

    private boolean isNotLastStation(Station station) {
        return !findLastStation().equals(station);
    }

    private Station findLastStation() {
        return getStations().get(getStations().size() - 1);
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
}
