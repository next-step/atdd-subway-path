package nextstep.subway.entity.group;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.entity.Section;

@Embeddable
public class SectionGroup {

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private final List<Section> sections;

    public SectionGroup() {
        sections = new ArrayList<>();
    }

    public SectionGroup(List<Section> sections) {
        this.sections = sections;
    }


    public static SectionGroup of(final List<Section> sections) {
        return new SectionGroup(sections);
    }

    public List<Long> getStationsId() {

        return sections.stream()
            .map(Section::getStationIdList)
            .flatMap(Collection::stream)
            .distinct()
            .collect(Collectors.toList());
    }


    public Section getEndDownStation() {
        return sections.stream()
            .sorted(Comparator.comparing(Section::getId).reversed())
            .limit(1)
            .collect(Collectors.toList())
            .get(0);
    }

    public void validateAdd(long upStationId, long downStationId) {

        validAddedSectionUpStation(upStationId);
        validAddedSectionDownStation(downStationId);
    }

    private void validAddedSectionUpStation(long upStationId) {
        if (!isEqualDownEndStation(upStationId)) {
            throw new IllegalArgumentException("추가하고자 하는 구간의 상행역이, 노선의 하행종점역이 아닙니다.");
        }
    }

    private void validAddedSectionDownStation(long downStationId) {
        if (isExistDownEndStation(downStationId)) {
            throw new IllegalArgumentException("추가하고자 하는 구간의 하행역이 이미 구간에 존재합니다.");
        }
    }

    public boolean isEqualDownEndStation(long addUpStationId) {

        return sections.stream()
            .sorted(Comparator.comparing(Section::getId).reversed())
            .limit(1)
            .anyMatch(
                section -> section.getDownStationId().equals(addUpStationId)
            );
    }

    public boolean isExistDownEndStation(long downStationId) {
        return sections.stream()
            .anyMatch(
                section -> section.getDownStationId().equals(downStationId)
            );
    }

    public void delete(long deleteStationId) {

        validateDelete(deleteStationId);
        sections.remove(getEndDownStation());
    }

    private void validateDelete(long deleteStationId) {

        validateSizeCanBeDeleted();
        validateDeleteStationIsEndStation(deleteStationId);
    }

    private void validateSizeCanBeDeleted() {
        if (sections.size() <= 1) {
            throw new IllegalArgumentException("구간이 1개인 경우 삭제할 수 없습니다.");
        }
    }

    private void validateDeleteStationIsEndStation(long deleteStationId) {
        if (!getEndDownStation().isEqualsDownStation(deleteStationId)) {
            throw new IllegalArgumentException("하행 종점역이 아니면 삭제할 수 없습니다.");
        }
    }
}
