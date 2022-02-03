package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import nextstep.subway.common.domain.exception.ErrorMessage;
import nextstep.subway.line.infrastructure.PathFinder;
import nextstep.subway.line.infrastructure.dto.StationPath;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {
    @OneToMany(
        mappedBy = "line",
        fetch = FetchType.LAZY,
        cascade = { CascadeType.PERSIST, CascadeType.MERGE },
        orphanRemoval = true
    )
    private List<Section> values = new ArrayList<>();

    protected Sections() {
    }

    public Sections(List<Section> values) {
        this.values = values;
    }

    public void add(Section newSection) {
        if (!values.isEmpty()) {
            verifyAddable(newSection);

            findInsertableSection(newSection)
                .ifPresent(insertableSection -> insertableSection.changeUpStation(newSection));
        }
        values.add(newSection);
    }

    private void verifyAddable(Section section) {
        Set<Station> stations = new HashSet<>(toStations());
        boolean existsUpStation = stations.contains(section.getUpStation());
        boolean existsDownStation = stations.contains(section.getDownStation());

        if (existsUpStation && existsDownStation) {
            throw new IllegalArgumentException(ErrorMessage.EXISTS_STATIONS.getMessage());
        }
        if (!existsUpStation && !existsDownStation) {
            throw new IllegalArgumentException(ErrorMessage.NOT_EXISTS_STATIONS.getMessage());
        }
    }

    private Optional<Section> findInsertableSection(Section section) {
        return values.stream()
                     .filter(eachSection -> eachSection.matchUpStation(section))
                     .findFirst();
    }

    public void remove(long stationId) {
        verifyRemovable(stationId);

        values.removeIf(
            eachSection -> eachSection.matchDownStation(stationId)
        );
    }

    private void verifyRemovable(long downStationId) {
        if (isNotLastStation(downStationId)) {
            throw new IllegalArgumentException(ErrorMessage.NOT_LAST_STATION_DELETED.getMessage());
        }
    }

    private boolean isNotLastStation(long stationId) {
        List<Station> stations = toStations();
        Station lastStation = stations.get(stations.size() - 1);
        return !lastStation.matchId(stationId);
    }

    public List<Station> toStations() {
        if (values.isEmpty()) {
            return Collections.emptyList();
        }
        return followSectionsLink(firstSection());
    }

    private List<Station> followSectionsLink(Section startSection) {
        Map<Station, Section> regularizedByUpStation = regularizedByStation(Section::getUpStation);

        List<Station> stations = new ArrayList<>();
        stations.add(startSection.getUpStation());
        stations.add(startSection.getDownStation());

        Section nextSection = regularizedByUpStation.get(startSection.getDownStation());
        while (Objects.nonNull(nextSection)) {
            stations.add(nextSection.getDownStation());
            nextSection = regularizedByUpStation.get(nextSection.getDownStation());
        }
        return stations;
    }

    private Map<Station, Section> regularizedByStation(Function<Section, Station> getStationFunc) {
        return values.stream()
                     .collect(Collectors.toMap(
                         getStationFunc, eachSection -> eachSection
                     ));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private Section firstSection() {
        Map<Station, Section> regularizedByUpStation = regularizedByStation(Section::getUpStation);
        Map<Station, Section> regularizedByDownStation = regularizedByStation(Section::getDownStation);

        return regularizedByUpStation.entrySet().stream()
                                 .filter(eachEntry -> !regularizedByDownStation.containsKey(eachEntry.getKey()))
                                 .map(Map.Entry::getValue)
                                 .findFirst().get();
    }

    public int totalDistance() {
        return values.stream()
                     .map(Section::getDistance)
                     .reduce(Distance::addition)
                     .map(Distance::getValue)
                     .orElse(0);
    }

    public Sections union(Sections thatSections) {
        List<Section> newSections = new ArrayList<>(values);
        newSections.addAll(thatSections.values);
        return new Sections(newSections);
    }

    public StationPath shortestPaths(PathFinder pathFinder, Station source, Station target) {
        return pathFinder.findShortestPaths(values, source, target);
    }

    public int size() {
        return this.values.size();
    }
}
