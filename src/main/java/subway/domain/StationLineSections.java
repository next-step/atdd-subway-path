package subway.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import subway.exception.StationLineSectionCreateException;
import subway.exception.StationLineSectionDeleteException;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class StationLineSections {
    @OrderColumn(name = "section_order_index")
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<StationLineSection> sections = new ArrayList<>();

    @Builder
    public StationLineSections(StationLineSection section) {
        sections.add(section);
    }

    public void appendStationLineSection(Station upStation, Station downStation, BigDecimal distance) {
        final StationLineSection section = StationLineSection.builder()
                .upStation(upStation)
                .downStation(downStation)
                .distance(distance)
                .build();

        appendNewSection(section);
    }

    private void appendNewSection(StationLineSection section) {
        final Station newStation = section.getNewStation(getAllStations());
        final Station standardStation = section.getStandardStation(getAllStations());

        if (newStation.equals(section.getUpStation()) && standardStation.equals(getLineFirstStation())) {
            appendNewSectionToFirst(section);
        } else if (newStation.equals(section.getDownStation()) && standardStation.equals(getLineLastStation())) {
            appendNewSectionToLast(section);
        } else {
            appendNewSectionToBetween(section);
        }
    }

    private void appendNewSectionToFirst(StationLineSection section) {
        sections.add(0, section);
    }

    private void appendNewSectionToLast(StationLineSection section) {
        sections.add(section);
    }

    private void appendNewSectionToBetween(StationLineSection section) {
        final Station newStation = section.getNewStation(getAllStations());
        final Station standardStation = section.getStandardStation(getAllStations());
        final boolean isStandardStationUpSide = standardStation.equals(section.getUpStation());

        final StationLineSection neighborSection = getSections()
                .stream()
                .filter(lineSection -> isStandardStationUpSide ? standardStation.equals(lineSection.getUpStation()) : standardStation.equals(lineSection.getDownStation()))
                .findFirst()
                .orElseThrow(() -> new StationLineSectionCreateException("can't find standardStation included section"));

        neighborSection.splitSection(newStation, standardStation, section.getDistance());

        final int indexOfNeighborSection = getSections().indexOf(neighborSection);
        final int indexOfNewSection = isStandardStationUpSide ? indexOfNeighborSection : indexOfNeighborSection + 1;

        getSections().add(indexOfNewSection, section);
    }

    public List<Station> getAllStations() {
        final List<Station> allUpStations = sections.stream()
                .map(StationLineSection::getUpStation)
                .collect(Collectors.toList());

        final Station lastStation = getLineLastStation();

        if (Objects.nonNull(lastStation)) {
            allUpStations.add(lastStation);
        }

        return allUpStations;
    }

    public Station getLineLastStation() {
        final int indexOfLastSection = sections.size() - 1;

        return Optional.of(sections)
                .map(stations -> stations.get(indexOfLastSection))
                .map(StationLineSection::getDownStation)
                .orElseThrow(() -> new IllegalStateException("there is no last station of this line"));
    }

    public void deleteSection(Station station) {
        checkCanDeleteStation(station);

        if (station.equals(getLineFirstStation())) {
            deleteFirstStation();
        } else if (station.equals(getLineLastStation())) {
            deleteLastStation();
        } else {
            deleteMiddleStation(station);
        }
    }

    private void deleteFirstStation() {
        sections.remove(0);
    }

    private void deleteLastStation() {
        sections.remove(getSections().size() - 1);
    }

    private void deleteMiddleStation(Station station) {
        final StationLineSection upSection = sections.stream()
                .filter(section -> station.equals(section.getDownStation()))
                .findFirst()
                .orElseThrow(() -> new StationLineSectionDeleteException("can't find station included section"));

        final StationLineSection downSection = sections.stream()
                .filter(section -> station.equals(section.getUpStation()))
                .findFirst()
                .orElseThrow(() -> new StationLineSectionDeleteException("can't find station included section"));

        mergeStationLineSection(upSection, downSection);

        sections.remove(upSection);
        sections.remove(downSection);
    }

    public void mergeStationLineSection(StationLineSection upSection, StationLineSection downSection) {
        final BigDecimal totalDistance = upSection.getDistance().add(downSection.getDistance());

        final StationLineSection mergedSection = StationLineSection.builder()
                .upStation(upSection.getUpStation())
                .downStation(downSection.getDownStation())
                .distance(totalDistance)
                .build();

        final int indexOfDownSection = sections.indexOf(downSection);

        sections.add(indexOfDownSection, mergedSection);
    }

    private void checkCanDeleteStation(Station station) {
        if (getCountOfAllStation() <= 2) {
            throw new StationLineSectionDeleteException("section must be greater or equals than 2");
        }

        final boolean isStationNotExistsInLine = getAllStations().stream()
                .noneMatch(station::equals);

        if (isStationNotExistsInLine) {
            throw new StationLineSectionDeleteException("the station not exists to this line");
        }
    }

    public int getCountOfAllStation() {
        return sections.size() + 1;
    }

    public Station getLineFirstStation() {
        return getSections().stream()
                .map(StationLineSection::getUpStation)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("there is no station at this line"));
    }

    //associate util method
    public void apply(StationLine line) {
        sections.forEach(section -> section.apply(line));
    }
}
