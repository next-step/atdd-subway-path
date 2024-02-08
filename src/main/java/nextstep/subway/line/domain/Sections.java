package nextstep.subway.line.domain;

import nextstep.subway.line.exception.SectionAddFailureException;
import nextstep.subway.line.exception.SectionDeleteFailureException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(
        mappedBy = "line",
        cascade = {CascadeType.PERSIST, CascadeType.MERGE},
        orphanRemoval = true
    )
    private List<Section> sections = new ArrayList<>();

    /** 구간 추가 */
    public void add(Section section) {
        verifyAddableSection(section);

        // 기존에 존재하는 구간이 없으면 바로 추가 가능
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }

        // 상행역이 이미 존재하지 않으면, 맨 앞에 추가
        if (shouldBeAddFirst(section)) {
            verifyAddableSectionToFirst(section);
            sections.add(section);
            return;
        }

        // 상행역이 기존 구간의 마지막이었으면, 맨 뒤에 추가
        if (shouldBeAddedLast(section)) {
            verifyAddableSectionToLast(section);
            sections.add(section);
            return;
        }

        addToMiddle(section);
    }

    /** 구간 제거 */
    public void remove(Section section) {
        verifyDeletableStation(section);
        this.sections.remove(section);
    }

    /** 구간 반환 */
    public List<Section> getSections() {
        return sections.stream()
            .sorted((s1, s2) -> {
                if (s1.getDownStation().equals(s2.getUpStation())) {
                    return -1;
                }
                return 0;
            })
            .collect(Collectors.toList());
    }

    /** 모든 역 반환 */
    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return List.of();
        }

        List<Station> stations = getSections()
            .stream()
            .map(Section::getUpStation)
            .collect(Collectors.toList());

        stations.add(getLastSection().getDownStation());

        return stations;
    }

    /** 마지막 구간 반환 */
    public Section getLastSection() {
        if (sections.isEmpty()) {
            return null;
        }
        return getSections().get(sections.size() - 1);
    }

    /** 첫 번째 구간 반환 */
    public Section getFistSection() {
        if (sections.isEmpty()) {
            return null;
        }
        return getSections().get(0);
    }

    private boolean shouldBeAddFirst(Section section) {
        return !isAlreadyExistStation(section.getUpStation());
    }

    private boolean shouldBeAddedLast(Section section) {
        return getLastSection().getDownStation().equals(section.getUpStation());
    }

    private void addToMiddle(Section section) {
        // 상행역이 이미 존재하면, 중간에 추가
        Section nextSection = findNextSection(section);
        // 기존의 구간을 update
        nextSection.changeUpStation(section.getDownStation());
        nextSection.subtractDistance(section.getDistance());

        sections.add(section);
    }

    /** 구간을 맨 앞에 추가할 수 있는지 검증 */
    private void verifyAddableSectionToFirst(Section section) {
        if (!section.getDownStation().equals(getFistSection().getUpStation())) {
            throw new SectionAddFailureException("새로운 구간의 하행역과 이어지는 구간이 없습니다.");
        }
    }

    /** 구간을 맨 뒤에 추가할 수 있는지 검증 */
    private void verifyAddableSectionToLast(Section section) {
        if (isAlreadyExistStation(section.getDownStation())) {
            throw new SectionAddFailureException("새로운 구간의 하행역이 기존 노선에 이미 존재합니다.");
        }
    }

    /** 구간을 추가할 수 있는지 검증 */
    private void verifyAddableSection(Section section) {
        if (sections.isEmpty()) {
            return;
        }

        if (section.getUpStation().equals(section.getDownStation())) {
            throw new SectionAddFailureException("상행역과 하행역이 동일할 수 없습니다.");
        }

        if (isAlreadyExistSection(section)) {
            throw new SectionAddFailureException("이미 존재하는 구간입니다.");
        }
    }

    /** 구간을 삭제할 수 있는지 검증 */
    private void verifyDeletableStation(Section section) {
        if (hasOnlyOneSection()) {
            throw new SectionDeleteFailureException("노선의 구간은 최소 한 개 이상 존재해야 합니다.");
        }

        Section lastSection = getLastSection();
        if (lastSection == null || !lastSection.equals(section)) {
            throw new SectionDeleteFailureException("노선의 하행종점역만 제거할 수 있습니다.");
        }
    }

    /** 이미 존재하는 구간이면 true, 존재하지 않으면 false 반환 */
    private boolean isAlreadyExistSection(Section section) {
        return sections.stream()
            .anyMatch(s ->
                s.getUpStation().equals(section.getUpStation()) && s.getDownStation().equals(section.getDownStation())
            );
    }

    /** 이미 존재하는 역이면 true, 존재하지 않으면 false 반환 */
    private boolean isAlreadyExistStation(Station station) {
        return sections.stream().anyMatch(section ->
            station.equals(section.getUpStation()) || station.equals(section.getDownStation())
        );
    }

    /** 추가하려는 구간의 다음 구간을 반환 */
    private Section findNextSection(Section newSection) {
        return sections.stream()
            .filter(section -> section.getUpStation().equals(newSection.getUpStation()))
            .findAny()
            .orElseThrow(() -> new SectionAddFailureException("구간 추가에 실패했습니다."));
    }

    /** 하나의 구간만 가지고 있으면 true, 아니면 false 반환 */
    private boolean hasOnlyOneSection() {
        return sections.size() == 1;
    }
}
