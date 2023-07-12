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
        this.sections.add(section);
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
        if (isSameUpStation(section.getDownStation().getId())) {
            return true;
        }

        if (isSameDownstation(section.getUpStation().getId())) {
            return true;
        }

        if (isSameUpStation(section.getUpStation().getId()) && isSameDownstation(section.getDownStation().getId())) {
            return false;
        }

        if (isSameUpStation(section.getUpStation().getId())) {
            if (findSameUpStation(section.getUpStation().getId()).get().getDistance() <= section.getDistance()) {
                return false;
            }
        }

        if (isSameDownstation(section.getDownStation().getId())) {
            if (findSameDownStation(section.getDownStation().getId()).get().getDistance() <= section.getDistance()) {
                return false;
            }
        }

        if (findSectionByStationId(section.getUpStation().getId()).isEmpty() && findSectionByStationId(section.getDownStation().getId()).isEmpty()) {
            return false;
        }

//        Section lastSection = getLastSection();
//
////        if (!section.equalsUpstation(lastSection.getDownStation().getId())) {
////            throw new InvalidSectionCreateException(ErrorCode.SECTION_CREATE_FAIL_BY_UPSTATION);
////        }
//
//
//
//        if (alreadyRegistered(section.getDownStation().getId())) {
//            throw new InvalidSectionCreateException(ErrorCode.SECTION_CREATE_FAIL_BY_DOWNSTATION);
//        }

        return true;
    }

    public boolean requireUpStationChange(Section section) {
        return isSameUpStation(section.getDownStation().getId());
    }

    public boolean requireDownStationChange(Section section) {
        return isSameDownstation(section.getUpStation().getId());
    }

    private Optional<Section> findSameUpStation(Long stationId) {
        return sections.stream()
                       .filter(section -> section.equalsUpstation(stationId))
                       .findFirst();
    }

    private Optional<Section> findSameDownStation(Long stationId) {
        return sections.stream()
                       .filter(section -> section.equalsDownStation(stationId))
                       .findFirst();
    }

    private boolean isSameDownstation(Long stationId) {
        return findSameDownStation(stationId).isPresent();
    }

    private boolean isSameUpStation(Long stationId) {
        return findSameUpStation(stationId).isPresent();
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

    private boolean alreadyRegistered(Long stationId) {
        return sections.stream()
                       .anyMatch(section -> section.containStation(stationId));
    }

    private Section getLastSection() {
        return sections.get(sections.size()-1);
    }
}
