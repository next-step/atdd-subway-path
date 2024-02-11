package nextstep.subway.line.domain;

import nextstep.subway.line.exception.LineException;
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
    public void remove(Station station) {
        verifyDeletableStation(station);

        // 맨 앞 제거
        if(isFirstStation(station)) {
            remove(getFistSection());
            return;
        }

        // 맨 뒤 제거
        if(isLastStation(station)) {
            remove(getLastSection());
            return;
        }

        // 중간 제거
        removeAtMiddle(station);
    }

    private void remove(Section section) {
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
    private Section getLastSection() {
        if (sections.isEmpty()) {
            throw new LineException("구간 정보가 존재하지 않습니다.");
        }

        return getSections().get(sections.size() - 1);
    }

    /** 첫 번째 구간 반환 */
    private Section getFistSection() {
        if (sections.isEmpty()) {
            throw new LineException("구간 정보가 존재하지 않습니다.");
        }
        return getSections().get(0);
    }

    private boolean shouldBeAddFirst(Section section) {
        return !isAlreadyExistStation(section.getUpStation());
    }

    private boolean shouldBeAddedLast(Section section) {
        return isLastStation(section.getUpStation());
    }

    private void addToMiddle(Section section) {
        // 추가하려는 구간의 상행역과 이어지는 구간을 찾음
        Section nextSection = findSectionByUpStation(section.getUpStation());

        nextSection.changeUpStation(section.getDownStation());
        nextSection.subtractDistance(section.getDistance());

        sections.add(section);
    }

    private boolean isLastStation(Station station) {
        return getLastSection().getDownStation().equals(station);
    }

    private boolean isFirstStation(Station station) {
        return getFistSection().getUpStation().equals(station);
    }

    private void removeAtMiddle(Station targetStation) {
        Section targetSection = findSectionByDownStation(targetStation);
        // 삭제하려는 구간의 하행역과 이어지는 구간을 찾음
        Section nextSection = findSectionByUpStation(targetSection.getDownStation());

        nextSection.changeUpStation(targetSection.getUpStation());
        nextSection.addDistance(targetSection.getDistance());

        remove(targetSection);
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
    private void verifyDeletableStation(Station station) {
        if (sections.isEmpty()) {
            throw new SectionDeleteFailureException("노선의 구간 정보가 존재하지 않습니다.");
        }

        if (hasOnlyOneSection()) {
            throw new SectionDeleteFailureException("노선의 구간은 최소 한 개 이상 존재해야 합니다.");
        }

        if (!isAlreadyExistStation(station)) {
            throw new SectionDeleteFailureException("삭제하려는 역이 해당 노선에 존재하지 않습니다.");
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

    /** 주어진 역이 구간의 상행역과 동일한 구간을 반환 */
    private Section findSectionByUpStation(Station station) {
        return sections.stream()
            .filter(section -> section.getUpStation().equals(station))
            .findAny()
            .orElseThrow(() -> new SectionAddFailureException("추가하려는 구간의 다음 구간을 찾을 수 없습니다."));
    }

    /** 주어진 역이 구간의 하행역과 동일한 구간을 반환 */
    private Section findSectionByDownStation(Station station) {
        return sections.stream()
            .filter(section -> section.getDownStation().equals(station))
            .findAny()
            .orElseThrow(() -> new SectionAddFailureException("삭제하려는 구간을 찾을 수 없습니다."));
    }

    /** 하나의 구간만 가지고 있으면 true, 아니면 false 반환 */
    private boolean hasOnlyOneSection() {
        return sections.size() == 1;
    }
}
