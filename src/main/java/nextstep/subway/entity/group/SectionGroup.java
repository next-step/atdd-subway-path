package nextstep.subway.entity.group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.entity.Line;
import nextstep.subway.entity.Section;
import nextstep.subway.entity.Station;
import nextstep.subway.entity.group.factory.SectionAddActionFactory;

@Embeddable
public class SectionGroup {

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Section> sections;

    public SectionGroup() {
        sections = new ArrayList<>();
    }

    public SectionGroup(List<Section> sections) {
        this.sections = sections;
    }

    public static SectionGroup of(final List<Section> sections) {
        return new SectionGroup(sections);
    }

    public Section add(Line line, Station upStation, Station downStation, int distance) {

        Section newSection = new Section(line, upStation, downStation, distance);

        if (sections.isEmpty()) {
            sections.add(newSection);
            return newSection;
        }
        validateAdd(upStation.getId(), downStation.getId());

        SectionAddActionFactory.make(newSection, findAddSection(newSection)).action();

        sections.add(newSection);

        return newSection;
    }

    private Section findAddSection(final Section newSection) {

        final Section endUpSection = getEndUpSection();
        final Section endDownStation = getEndDownStation();

        if (endUpSection.isEqualsUpStation(newSection.getDownStationId())) {
            return endUpSection;
        }

        if (endDownStation.isEqualsDownStation(newSection.getUpStationId())) {
            return endDownStation;
        }

        return sections.stream()
            .filter(section ->
                section.isEqualsUpStation(newSection.getUpStationId()) ||
                    section.isEqualsDownStation(newSection.getDownStationId())
            )
            .findFirst()
            .orElseThrow(
                () -> new IllegalArgumentException("")
            );
    }

    private void validateAdd(Long upStationId, Long downStationId) {

        if (isExistStation(upStationId) && isExistStation(downStationId)) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어있습니다.");
        }

        if (!isExistStation(upStationId) && !isExistStation(downStationId)) {
            throw new IllegalArgumentException("상행역과 하행역 둘 중 하나도 전체구간에 포함되지 않습니다.");
        }
    }

    private boolean isExistStation(long stationId) {

        return sections.stream()
            .anyMatch(
                section ->
                    section.isEqualsDownStation(stationId) || section.isEqualsUpStation(stationId)
            );
    }

    private Section getEndUpSection() {

        Map<Long, Section> map = new HashMap<>();

        for (Section section : getSections()) {
            map.put(section.getUpStationId(), section);
        }

        for (Section section : getSections()) {
            map.remove(section.getDownStationId());
        }

        if (map.size() > 1) {
            throw new IllegalArgumentException("해당 노선의 상행 종점역이 1개 이상입니다.");
        }

        if (map.isEmpty()) {
            throw new IllegalArgumentException("해당 노선의 상행 종점역이 존재하지 않습니다.");
        }

        return map.values().stream()
            .findFirst()
            .orElseThrow(
                () -> new IllegalArgumentException("해당 노선의 상행 종점역이 존재하지 않습니다.")
            );
    }
    public void delete(long deleteStationId) {

        validateDelete(deleteStationId);
        sections.remove(getEndDownStation());
    }

    private Section getEndDownStation() {

        Map<Long, Section> map = new HashMap<>();

        for (Section section : getSections()) {
            map.put(section.getDownStationId(), section);
        }

        for (Section section : getSections()) {
            map.remove(section.getUpStationId());
        }

        if (map.size() > 1) {
            throw new IllegalArgumentException("해당 노선의 하행 종점역이 1개 이상입니다.");
        }

        if (map.isEmpty()) {
            throw new IllegalArgumentException("해당 노선의 하행 종점역이 존재하지 않습니다.");
        }

        return map.values().stream()
            .findFirst()
            .orElseThrow(
                () -> new IllegalArgumentException("해당 노선의 하행 종점역이 존재하지 않습니다.")
            );
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

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public List<Station> getStationsInOrder() {

        Section start = getEndUpSection();

        List<Station> result = new ArrayList<>();
        result.add(start.getUpStation());
        result.add(start.getDownStation());

        Optional<Section> next = findNext(start);

        while (next.isPresent()) {
            result.add(next.get().getDownStation());
            next = findNext(next.get());
        }

        return Collections.unmodifiableList(result);
    }

    private Optional<Section> findNext(Section now) {

        return sections.stream()
            .filter(section -> section.getUpStationId().equals(now.getDownStationId()))
            .findFirst();
    }

    public boolean isEndUpSection(final Section section) {

        final Section endUpSection = getEndUpSection();

        return endUpSection.isEqualsUpStation(section.getUpStationId()) &&
            endUpSection.isEqualsDownStation(section.getDownStationId());
    }

    public boolean isEndDownSection(final Section section) {

        final Section endDownSection = getEndDownStation();

        return endDownSection.isEqualsUpStation(section.getUpStationId()) &&
            endDownSection.isEqualsDownStation(section.getDownStationId());
    }
}
