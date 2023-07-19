package nextstep.subway.entity.group;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.entity.Line;
import nextstep.subway.entity.Section;
import nextstep.subway.entity.Station;
import nextstep.subway.entity.group.factory.AddNone;
import nextstep.subway.entity.group.factory.SectionAddAction;
import nextstep.subway.entity.group.factory.SectionAddActionFactory;

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

    public Section add(Line line, Station upStation, Station downStation, int distance) {

        final Section ascEndUpSection = getEndUpSection();

        SectionAddAction action = SectionAddActionFactory.make(upStation.getId(),
            downStation.getId(), ascEndUpSection);

        Optional<Section> currentSection = Optional.of(ascEndUpSection);

        while (existNext(currentSection.get()) && !action.isAdd()) {

            currentSection = findNext(currentSection.get());

            action = SectionAddActionFactory.make(upStation.getId(),
                downStation.getId(), currentSection.get());

        }

        if (action instanceof AddNone) {
            throw new IllegalArgumentException("상행역과 하행역 둘 중 하나도 전체구간에 포함되지 않습니다.");
        }

        return action.add(line, upStation, downStation, distance, currentSection.get(), this);
    }

    private Section getEndUpSection() {
        return sections.stream()
            .filter(Section::isUpEndPointSection)
            .findFirst()
            .orElseThrow(
                () -> new IllegalArgumentException("해당 노선의 상행 종점역이 존재하지 않습니다.")
            );
    }

    private Optional<Section> findNext(Section now) {

        return sections.stream()
            .filter(section -> section.getUpStationId().equals(now.getDownStationId()))
            .findFirst();
    }

    private boolean existNext(Section now) {
        return sections.stream()
            .anyMatch(section -> section.getUpStationId().equals(now.getDownStationId()));
    }

    public void delete(long deleteStationId) {

        validateDelete(deleteStationId);
        sections.remove(getEndDownStation());
    }

    public Section getEndDownStation() {
        return sections.stream()
            .filter(Section::isDownEndPointSection)
            .findFirst()
            .orElseThrow(
                () -> new IllegalArgumentException("해당 노선의 하행종점역이 존재하지 않습니다.")
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
        return sections;
    }

    public void validateExistStation(long stationId) {

        if (isExistStation(stationId)) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어있습니다.");
        }
    }

    private boolean isExistStation(long stationId) {

        return sections.stream()
            .anyMatch(
                section ->
                    section.isEqualsDownStation(stationId) || section.isEqualsUpStation(stationId)
            );
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

        return result;
    }
}
