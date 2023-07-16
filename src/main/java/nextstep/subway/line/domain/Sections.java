package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.util.CollectionUtils;

import nextstep.subway.section.domain.Section;
import nextstep.subway.section.exception.InvalidSectionDeleteException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.support.ErrorCode;
import nextstep.subway.support.SubwayException;


@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Sections {

    @OneToMany(mappedBy = "line", cascade = { CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public boolean hasOneSection() {
        if (CollectionUtils.isEmpty(this.sections)) {
            return false;
        }

        return this.sections.size() == 1;
    }

    public void appendSection(Section section) {
        if (requireSectionDivide(section)) {
            Section divideTargetSection = findDivideTargetSection(section);

            if (divideTargetSection.equalsUpstation(section.getUpStation().getId())) {
                divideTargetSection.changeUpStation(section.getDownStation());
            }

            if (divideTargetSection.equalsDownStation(section.getDownStation().getId())) {
                divideTargetSection.changeDownStation(section.getUpStation());
            }

            divideTargetSection.decreaseDistance(section.getDistance());
        }

        this.sections.add(section);
    }

    private Section findDivideTargetSection(Section section) {
        Optional<Section> maybeTarget = findSectionByStationId(section.getUpStation().getId());

        if (maybeTarget.isEmpty()) {
            maybeTarget = findSectionByStationId(section.getDownStation().getId());
        }

        return maybeTarget.get();
    }

    private boolean requireSectionDivide(Section section) {
        return hasSameUpStationInSection(section.getUpStation()) || hasSameDownstationInSection(section.getDownStation());
    }

    public boolean isEmpty() {
        return this.sections.isEmpty();
    }

    public boolean possibleToAddSection(Section section) {
        if (isEmpty()) {
            return true;
        }

        /**
         * 역의 양 끝에 추가하는 경우
         */
        if (hasSameUpStationInSection(section.getDownStation())) {
            return true;
        }

        if (hasSameDownstationInSection(section.getUpStation())) {
            return true;
        }

        if (hasSameUpStationInSection(section.getUpStation()) && hasSameDownstationInSection(section.getDownStation())) {
            return false;
        }

        if (hasSameUpStationInSection(section.getUpStation())) {
            if (findSameUpStationSection(section.getUpStation()).get().distanceIsLessThanEquals(section.getDistance())) {
                return false;
            }
        }

        if (hasSameDownstationInSection(section.getDownStation())) {
            if (findSameDownStation(section.getDownStation()).get().getDistance() <= section.getDistance()) {
                return false;
            }
        }

        if (findSectionByStationId(section.getUpStation().getId()).isEmpty() && findSectionByStationId(section.getDownStation().getId()).isEmpty()) {
            return false;
        }

        return true;
    }

    public boolean requireUpStationChange(Section section) {
        return hasSameUpStationInSection(section.getDownStation());
    }

    public boolean requireDownStationChange(Section section) {
        return hasSameDownstationInSection(section.getUpStation());
    }

    private Optional<Section> findSameUpStationSection(Station station) {
        return sections.stream()
                       .filter(section -> section.equalsUpstation(station))
                       .findFirst();
    }

    private Optional<Section> findSameDownStation(Station station) {
        return sections.stream()
                       .filter(section -> section.equalsDownStation(station))
                       .findFirst();
    }

    private boolean hasSameDownstationInSection(Station station) {
        return findSameDownStation(station).isPresent();
    }

    private boolean hasSameUpStationInSection(Station station) {
        return findSameUpStationSection(station).isPresent();
    }

    public void possibleToDeleteSection(Station station) {
        if (hasOneSection()) {
            throw new InvalidSectionDeleteException(ErrorCode.SECTION_DELETE_FAIL_BY_LAST_SECTION_CANNOT_DELETED);
        }

        if (!isStationOnSections(station)) {
            throw new SubwayException(ErrorCode.STATION_NOT_ON_SECTIONS);
        }
    }

    private boolean isStationOnSections(Station station) {
        return sections.stream().anyMatch(section -> section.containStation(station.getId()));
    }

    public void deleteSectionByStationId(Long stationId) {
        List<Section> relatedSections = findSectionsByStationId(stationId);

        if (relatedSections.size() == 2) {
            Section newSection = relatedSections.get(0).mergeSection(relatedSections.get(1));

            this.sections.removeAll(relatedSections);
            this.sections.add(newSection);
        } else if (relatedSections.size() == 1) {
            this.sections.remove(relatedSections.get(0));
        }
    }

    private Optional<Section> findSectionByStationId(Long stationId) {
        return sections.stream()
                       .filter(section -> section.containStation(stationId))
                       .findAny();
    }

    private List<Section> findSectionsByStationId(Long stationId) {
        return sections.stream()
                       .filter(section -> section.containStation(stationId))
                       .collect(Collectors.toList());
    }

    public List<Section> toOrderedList(Station lastUpStation) {
        List<Section> orderedSections = new ArrayList<>();

        Section iterateSection = findSameUpStationSection(lastUpStation).get();
        orderedSections.add(iterateSection);

        while(findSameUpStationSection(iterateSection.getDownStation()).isPresent()) {
            iterateSection = findSameUpStationSection(iterateSection.getDownStation()).get();
            orderedSections.add(iterateSection);
        }

        return List.copyOf(orderedSections);
    }

    public int allDistance() {
        return sections.stream()
                       .map(Section::getDistance)
                       .reduce(0, Integer::sum);
    }

    public Optional<Section> findSectionByUpStation(Long stationId) {
        return sections.stream()
                       .filter(section -> section.equalsUpstation(stationId))
                       .findFirst();
    }

    public Optional<Section> findSectionByDownStation(Long stationId) {
        return sections.stream()
                       .filter(section -> section.equalsDownStation(stationId))
                       .findFirst();
    }

}
