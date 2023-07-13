package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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


@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Sections {

    @OneToMany(mappedBy = "line", cascade = { CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public boolean isLastDownStation(Long stationId) {
        if (CollectionUtils.isEmpty(this.sections)) {
            return false;
        }

        return getLastSection().equalsDownStation(stationId);
    }

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
        return hasSameUpStationInSection(section.getUpStation().getId()) || hasSameDownstationInSection(section.getDownStation().getId());
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
        if (hasSameUpStationInSection(section.getDownStation().getId())) {
            return true;
        }

        if (hasSameDownstationInSection(section.getUpStation().getId())) {
            return true;
        }

        if (hasSameUpStationInSection(section.getUpStation().getId()) && hasSameDownstationInSection(section.getDownStation().getId())) {
            return false;
        }

        if (hasSameUpStationInSection(section.getUpStation().getId())) {
            if (findSameUpStationSection(section.getUpStation().getId()).get().distanceIsLessThanEquals(section.getDistance())) {
                return false;
            }
        }

        if (hasSameDownstationInSection(section.getDownStation().getId())) {
            if (findSameDownStation(section.getDownStation().getId()).get().getDistance() <= section.getDistance()) {
                return false;
            }
        }

        if (findSectionByStationId(section.getUpStation().getId()).isEmpty() && findSectionByStationId(section.getDownStation().getId()).isEmpty()) {
            return false;
        }

        return true;
    }

    public boolean requireUpStationChange(Section section) {
        return hasSameUpStationInSection(section.getDownStation().getId());
    }

    public boolean requireDownStationChange(Section section) {
        return hasSameDownstationInSection(section.getUpStation().getId());
    }

    private Optional<Section> findSameUpStationSection(Long stationId) {
        return sections.stream()
                       .filter(section -> section.equalsUpstation(stationId))
                       .findFirst();
    }

    private Optional<Section> findSameDownStation(Long stationId) {
        return sections.stream()
                       .filter(section -> section.equalsDownStation(stationId))
                       .findFirst();
    }

    private boolean hasSameDownstationInSection(Long stationId) {
        return findSameDownStation(stationId).isPresent();
    }

    private boolean hasSameUpStationInSection(Long stationId) {
        return findSameUpStationSection(stationId).isPresent();
    }

    public boolean possibleToDeleteSection(Long stationId) {
        if (hasOneSection()) {
            throw new InvalidSectionDeleteException(ErrorCode.SECTION_DELETE_FAIL_BY_LAST_SECTION_CANNOT_DELETED);
        }

        if (!isLastDownStation(stationId)) {
            throw new InvalidSectionDeleteException(ErrorCode.ONLY_LAST_DOWNSTATION_CAN_DELETED);
        }

        return true;
    }

    public void deleteSectionByStationId(Long stationId) {
        findSectionByStationId(stationId).ifPresent(section -> sections.remove(section));
    }

    private Optional<Section> findSectionByStationId(Long stationId) {
        return sections.stream()
                       .filter(section -> section.containStation(stationId))
                       .findAny();
    }

    private Section getLastSection() {
        return sections.get(sections.size()-1);
    }

    public List<Section> toOrderedList(Station lastUpStation) {
        List<Section> orderedSections = new ArrayList<>();

        Section iterateSection = findSameUpStationSection(lastUpStation.getId()).get();
        orderedSections.add(iterateSection);

        while(findSameUpStationSection(iterateSection.getDownStation().getId()).isPresent()) {
            iterateSection = findSameUpStationSection(iterateSection.getDownStation().getId()).get();
            orderedSections.add(iterateSection);
        }

        return List.copyOf(orderedSections);
    }
}
