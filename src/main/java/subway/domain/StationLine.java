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
        /*
            기준역과 신규 역을 찾는다.
            신규역이 사이에 존재할 경우 왼쪽 거리와 오른쪽 거리를 계산한뒤
            기준역이 존재하는 기존 구간을 찾는다.
            기존 구간의 거리를 왼쪽 거리로 update하고 해당 구간의 downStation을 신규역으로 수정한다
            신규역과 기존역의 다음 역을 다시 새로운 구간으로 만들어서 해당 위치에 오른쪽 거리로 추가한다
         */

        checkSectionStationExistOnlyOneToLine(sectionUpStation, sectionDownStation);

        final StationLineSection section = StationLineSection.builder()
                .upStation(sectionUpStation)
                .downStation(sectionDownStation)
                .distance(distance)
                .build();

        if (!isStationFirstOrLastOfLine(sectionDownStation)) {
            final StationLineSection previousSectionOfNewSection = getSections()
                    .stream()
                    .filter(lineSection -> lineSection.getUpStation().equals(sectionUpStation))
                    .findFirst()
                    .orElseThrow(() -> new StationLineCreateException("can't find section up station"));

            if (distance.compareTo(previousSectionOfNewSection.getDistance()) >= 0) {
                throw new StationLineCreateException("section distance must be less then existing section distance");
            }

            final BigDecimal newPreviousSectionDistance = previousSectionOfNewSection.getDistance().subtract(distance);
            previousSectionOfNewSection.changeDistance(newPreviousSectionDistance);


            final int newSectionIndex = getSections().indexOf(previousSectionOfNewSection);

            getSections().add(newSectionIndex, section);
        }

//        checkSectionCanAdded(sectionUpStation, sectionDownStation);

        section.apply(this);
        return section;
    }

    private void checkSectionStationExistOnlyOneToLine(Station sectionUpStation, Station sectionDownStation) {
        final boolean isSectionUpStationExisting = getAllStations().stream()
                .anyMatch(sectionUpStation::equals);
        final boolean isSectionDownStationExisting = getAllStations().stream()
                .anyMatch(sectionDownStation::equals);

        if (isSectionUpStationExisting == isSectionDownStationExisting) {
            throw new StationLineCreateException("one of section up station and down station exactly exist only one to line");
        }
    }

    private boolean isStationFirstOrLastOfLine(Station station) {
        return station.equals(getLineFirstUpStation()) || station.equals(getLineLastDownStation());
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

    public StationLineSection getLastSection() {
        if (getSections().isEmpty()) {
            return null;
        }

        final int lastIndexOfSections = getSections().size() - 1;

        return getSections().get(lastIndexOfSections);
    }
}
