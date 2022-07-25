package nextstep.subway.line.domain;

import lombok.Getter;
import nextstep.subway.line.domain.exception.CannotAddSectionException;
import nextstep.subway.line.domain.exception.CannotDeleteSectionException;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static javax.persistence.CascadeType.ALL;

@Getter
@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void add(Section addedSection) {
        if (sections.isEmpty()) {
            sections.add(addedSection);
            return;
        }

        validateAddedSection(addedSection);

        if (isFirstOrLastSection(addedSection)) {
            sections.add(addedSection);
            return;
        }

        Section includingSection = sections.stream()
                .filter(s -> s.startsOrEndsTogether(addedSection))
                .findAny()
                .orElseThrow(() -> new CannotAddSectionException("추가할 수 없는 구간입니다."));

        sections.remove(includingSection);
        sections.add(addedSection);
        sections.add(includingSection.subtract(addedSection));
    }

    private boolean isFirstOrLastSection(Section addedSection) {
        return firstSection().isAfter(addedSection) || lastSection().isBefore(addedSection);
    }

    private void validateAddedSection(Section addedSection) {
        List<Long> stationIds = stationIds();

        boolean containsUpStation = stationIds.contains(addedSection.getUpStationId());
        boolean containsDownStation = stationIds.contains(addedSection.getDownStationId());

        if (containsUpStation && containsDownStation) {
            throw new CannotAddSectionException("상행역과 하행역이 이미 노선에 모두 등록되어 있으면 구간을 추가할 수 없습니다.");
        }

        if (!containsUpStation && !containsDownStation) {
            throw new CannotAddSectionException("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 구간을 추가할 수 없습니다.");
        }

        sections.stream()
                .filter(s -> s.startsOrEndsTogether(addedSection) && s.isShorterThanOrEqualWith(addedSection))
                .findAny()
                .ifPresent(s -> {
                    throw new CannotAddSectionException("기존 구간 사이에 추가할 구간의 길이가 기존 구간의 길이보다 크거나 같을 수 없습니다.");
                });
    }

    public void removeSection(Long stationId) {
        if (sections.isEmpty()) {
            throw new CannotDeleteSectionException("현재 존재하는 구간이 없습니다.");
        }

        Section lastSection = lastSection();
        if (!lastSection.matchDownStation(stationId)) {
            throw new CannotDeleteSectionException("노선의 종점만 삭제할 수 있습니다.");
        }
        sections.remove(lastSection);
    }

    public List<Long> stationIds() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> stationIds = new ArrayList<>();
        Section iter = firstSection();

        while (!iter.equals(lastSection())) {
            stationIds.add(iter.getUpStationId());
            iter = findByUpStationId(iter.getDownStationId());
        }

        stationIds.add(iter.getUpStationId());
        stationIds.add(iter.getDownStationId());
        return stationIds;
    }

    private Section findByUpStationId(Long stationId) {
        return sections.stream()
                .filter(s -> s.matchUpStation(stationId))
                .findAny()
                .orElseThrow();
    }

    private Section firstSection() {
        return sections.stream()
                .filter(this::isFirstSection)
                .findAny()
                .orElseThrow();
    }

    private Section lastSection() {
        return sections.stream()
                .filter(this::isLastSection)
                .findAny()
                .orElseThrow();
    }

    private boolean isFirstSection(Section section) {
        return sections.stream()
                .map(Section::getDownStationId)
                .noneMatch(id -> id.equals(section.getUpStationId()));
    }

    private boolean isLastSection(Section section) {
        return sections.stream()
                .map(Section::getUpStationId)
                .noneMatch(id -> id.equals(section.getDownStationId()));
    }
}
