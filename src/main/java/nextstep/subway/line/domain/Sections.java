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
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.path.domain.StationPaths;
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
            throw new IllegalArgumentException(ErrorMessage.STATIONS_EXISTS.getMessage());
        }
        if (!existsUpStation && !existsDownStation) {
            throw new IllegalArgumentException(ErrorMessage.STATIONS_NOT_EXISTS.getMessage());
        }
    }

    private Optional<Section> findInsertableSection(Section section) {
        return values.stream()
                     .filter(eachSection -> eachSection.matchUpStation(section))
                     .findFirst();
    }

    public void delete(Station stationForRemove) {
        verifyRemovable(stationForRemove);

        if (removeIfFirstSection(stationForRemove)) {
            return;
        }
        removeNotFirstSection(stationForRemove);
    }

    private void verifyRemovable(Station stationForRemove) {
        Set<Station> stations = new HashSet<>(toStations());
        boolean isNotExists = !stations.contains(stationForRemove);
        if (isNotExists) {
            throw new IllegalArgumentException(ErrorMessage.NOT_FOUND_SECTION.getMessage());
        }

        boolean isMinSize = values.size() <= 1;
        if (isMinSize) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_SECTION_SIZE.getMessage());
        }
    }

    private boolean removeIfFirstSection(Station stationForRemove) {
        Section firstSection = firstSection();
        if (!firstSection.matchUpStation(stationForRemove)) {
            return false;
        }
        values.remove(firstSection);
        return true;
    }

    private void removeNotFirstSection(Station stationForRemove) {
        Map<Station, Section> byUpStation = toBasedOnKey(Section::getUpStation);
        Map<Station, Section> byDownStation = toBasedOnKey(Section::getDownStation);

        Section sectionForRemove = byDownStation.get(stationForRemove);
        values.remove(sectionForRemove);
        if (isLastSection(byUpStation, sectionForRemove)) {
            return;
        }
        Section downSectionOfCombine = byUpStation.get(stationForRemove);
        downSectionOfCombine.combineOfUpSection(sectionForRemove);
    }

    private <T> Map<T, Section> toBasedOnKey(Function<Section, T> getKeyFunc) {
        return values.stream()
                     .collect(Collectors.toMap(
                         getKeyFunc, eachSection -> eachSection
                     ));
    }

    private boolean isLastSection(Map<Station, Section> byUpStation, Section section) {
        return !byUpStation.containsKey(section.getDownStation());
    }

    public List<Station> toStations() {
        if (values.isEmpty()) {
            return Collections.emptyList();
        }
        return followSectionsLink(firstSection());
    }

    private List<Station> followSectionsLink(Section startSection) {
        Map<Station, Section> byUpStation = toBasedOnKey(Section::getUpStation);

        List<Station> stations = new ArrayList<>();
        stations.add(startSection.getUpStation());
        stations.add(startSection.getDownStation());

        Section nextSection = byUpStation.get(startSection.getDownStation());
        while (Objects.nonNull(nextSection)) {
            stations.add(nextSection.getDownStation());
            nextSection = byUpStation.get(nextSection.getDownStation());
        }
        return stations;
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private Section firstSection() {
        Map<Station, Section> byUpStation = toBasedOnKey(Section::getUpStation);
        Map<Station, Section> byDownStation = toBasedOnKey(Section::getDownStation);

        return byUpStation.entrySet().stream()
                                 .filter(eachEntry -> !byDownStation.containsKey(eachEntry.getKey()))
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

    public StationPaths shortestPaths(PathFinder pathFinder, Station source, Station target) {
        return pathFinder.findShortestPaths(values, source, target);
    }

    public int size() {
        return this.values.size();
    }
}
