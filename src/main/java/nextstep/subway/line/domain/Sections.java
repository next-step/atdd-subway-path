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

    public void remove(Station stationForRemove) {
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
        Map<Station, Section> regularizedByUpStation = regularizedByStation(Section::getUpStation);
        Map<Station, Section> regularizedByDownStation = regularizedByStation(Section::getDownStation);

        Section sectionForRemove = regularizedByDownStation.get(stationForRemove);
        values.remove(sectionForRemove);
        if (isLastSection(regularizedByUpStation, sectionForRemove)) {
            return;
        }
        Section upSectionOfDocking = regularizedByDownStation.get(stationForRemove);
        Section downSectionOfDocking = regularizedByUpStation.get(stationForRemove);
        upSectionOfDocking.dockingInDownSection(sectionForRemove, downSectionOfDocking);
    }

    private boolean isLastSection(Map<Station, Section> regularizedByUpStation, Section section) {
        return !regularizedByUpStation.containsKey(section.getDownStation());
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

    private <T> Map<T, Section> regularizedByStation(Function<Section, T> getKeyFunc) {
        return values.stream()
                     .collect(Collectors.toMap(
                         getKeyFunc, eachSection -> eachSection
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

    public int size() {
        return this.values.size();
    }
}
