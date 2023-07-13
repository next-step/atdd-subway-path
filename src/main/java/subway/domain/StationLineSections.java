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

    public StationLineSection appendStationLineSection(Station upStation, Station downStation, BigDecimal distance) {
        final StationLineSection section = StationLineSection.builder()
                .upStation(upStation)
                .downStation(downStation)
                .distance(distance)
                .build();

        appendNewSection(section);

        return section;
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
                .orElse(null);
    }

    public void deleteSection(Station station) {
        checkSectionCanDeleted(station);

        sections.remove(sections.size() - 1);
    }

    private void checkSectionCanDeleted(Station station) {
        if (!station.equals(getLineLastStation())) {
            throw new StationLineSectionDeleteException("target section must be last station of line");
        }

        if (getCountOfAllStation() <= 2) {
            throw new StationLineSectionDeleteException("section must be greater or equals than 2");
        }
    }

    public int getCountOfAllStation() {
        return sections.size() + 1;
    }

    private Station getLineFirstStation() {
        return getSections().stream()
                .map(StationLineSection::getUpStation)
                .findFirst()
                .orElse(null);
    }
}
