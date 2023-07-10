package subway.domain;

import lombok.*;
import subway.exception.StationLineCreateException;
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
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "lineId")
public class StationLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lineId;

    @Column
    private String name;

    @Column
    private String color;

    @OrderColumn(name = "section_order_index")
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<StationLineSection> sections = new ArrayList<>();

    @Builder
    public StationLine(String name, String color, Station upStation, Station downStation, BigDecimal distance) {
        if (upStation.equals(downStation)) {
            throw new StationLineCreateException("upStation and downStation can't be equal");
        }

        this.name = name;
        this.color = color;

        final StationLineSection section = StationLineSection.builder()
                .upStation(upStation)
                .downStation(downStation)
                .distance(distance)
                .build();

        getSections().add(section);
        section.apply(this);
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public StationLineSection createSection(Station sectionUpStation, Station sectionDownStation, BigDecimal distance) {
        checkSectionStationExistOnlyOneToLine(sectionUpStation, sectionDownStation);

        final StationLineSection newSection = StationLineSection.builder()
                .upStation(sectionUpStation)
                .downStation(sectionDownStation)
                .distance(distance)
                .build();

        final boolean sectionUpStationExistingToLine = isStationExistingToLine(sectionUpStation);
        final Station newStation = sectionUpStationExistingToLine ? sectionDownStation : sectionUpStation;
        final Station standardStation = newStation.equals(sectionUpStation) ? sectionDownStation : sectionUpStation;

        if (newStation.equals(sectionUpStation) && standardStation.equals(getLineFirstUpStation())) {
            appendNewSectionToFirst(newSection);
        } else if (newStation.equals(sectionDownStation) && standardStation.equals(getLineLastDownStation())) {
            appendNewSectionToLast(newSection);
        } else {
            appendNewSectionToBetween(newStation, standardStation, newSection);
        }

        newSection.apply(this);

        return newSection;
    }

    private void appendNewSectionToFirst(StationLineSection newSection) {
        getSections().add(0, newSection);
    }

    private void appendNewSectionToLast(StationLineSection newSection) {
        final StationLineSection neighborSection = getLastSection();
        final int indexOfNeighborSection = getSections().indexOf(neighborSection);

        getSections().add(indexOfNeighborSection + 1, newSection);
    }


    private void appendNewSectionToBetween(Station newStation, Station standardStation, StationLineSection newSection) {
        final boolean isStandardStationUpSide = standardStation.equals(newSection.getUpStation());

        final StationLineSection neighborSection = getSections()
                .stream()
                .filter(lineSection -> isStandardStationUpSide ? standardStation.equals(lineSection.getUpStation()) : standardStation.equals(lineSection.getDownStation()))
                .findFirst()
                .orElseThrow(() -> new StationLineSectionCreateException("can't find standardStation included section"));

        neighborSection.splitSection(standardStation, newStation, newSection.getDistance());

        final int indexOfNeighborSection = getSections().indexOf(neighborSection);
        final int indexOfNewSection = isStandardStationUpSide ? indexOfNeighborSection : indexOfNeighborSection + 1;

        getSections().add(indexOfNewSection, newSection);
    }

    private void checkSectionStationExistOnlyOneToLine(Station sectionUpStation, Station sectionDownStation) {
        if (isStationExistingToLine(sectionUpStation) == isStationExistingToLine(sectionDownStation)) {
            throw new StationLineCreateException("one of section up station and down station exactly exist only one to line");
        }
    }

    private boolean isStationExistingToLine(Station station) {
        return getAllStations().stream()
                .anyMatch(station::equals);
    }

    public void deleteSection(Station targetStation) {
        checkSectionCanDeleted(targetStation);

        final StationLineSection targetSection = getSections().stream()
                .filter(section -> targetStation.equals(section.getDownStation()))
                .findFirst()
                .orElseThrow(() -> new StationLineSectionDeleteException("not found deleted target section"));

        getSections().remove(targetSection);
    }

    private void checkSectionCanDeleted(Station targetStation) {
        if (!targetStation.equals(getLineLastDownStation())) {
            throw new StationLineSectionDeleteException("target section must be last station of line");
        }

        if (getSections().size() < 2) {
            throw new StationLineSectionDeleteException("section must be greater or equals than 2");
        }
    }

    public List<Station> getAllStations() {
        final List<Station> allUpStation = getSections().stream()
                .map(StationLineSection::getUpStation)
                .collect(Collectors.toList());

        Optional.ofNullable(getLineLastDownStation())
                .ifPresent(allUpStation::add);

        return allUpStation;
    }

    public Station getLineFirstUpStation() {
        return getSections().stream()
                .map(StationLineSection::getUpStation)
                .findFirst()
                .orElse(null);
    }

    public Station getLineLastDownStation() {
        return Optional.ofNullable(getLastSection())
                .map(StationLineSection::getDownStation)
                .orElse(null);
    }

    public StationLineSection getFirstSection() {
        if (getSections().isEmpty()) {
            return null;
        }

        return getSections().get(0);
    }

    public StationLineSection getLastSection() {
        if (getSections().isEmpty()) {
            return null;
        }

        final int lastIndexOfSections = getSections().size() - 1;

        return getSections().get(lastIndexOfSections);
    }
}
