package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Sections {
    private static final Integer MIN_SECTION_COUNT = 1;
    private static final String REMOVE_ERROR_COUNT = "구간 삭제 실패 - 구간이 %d 개 이하입니다.";
    private static final String REMOVE_ERROR_LAST = "구간 삭제 실패 - 마지막 구간만 제거할 수 있습니다.";
    @OneToMany(mappedBy = "subwayLine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public boolean addSectionToLast(Section section) {
        var lastSection = getLastSection();
        if (lastSection.isEmpty()) {
            this.sections.add(section);
            return true;
        }

        var upStationId = section.getUpStationId().orElse(null);
        var canAddToLast = lastSection.get().isDownStationId(upStationId) && !hasStation(section.getDownStation());
        if (!canAddToLast) return false;

        this.sections.add(section);
        return true;
    }

    public boolean addSectionToFirst(Section section) {
        var firstSection = getFirstSection();
        if (firstSection.isEmpty()) {
            this.sections.add(section);
            return true;
        }

        var downStationId = section.getDownStationId().orElse(null);

        var canAddToFirst = firstSection.get().isUpStationId(downStationId) && !hasStation(section.getUpStation());
        if (!canAddToFirst) return false;

        this.sections.add(section);
        return true;
    }

    public boolean addSectionToMiddle(Section section) {
        if (hasStation(section.getDownStation()) && hasStation(section.getUpStation())) {
            return false;
        }

        var optionalMiddleSection = this.sections.stream()
                .filter(s -> s.getDownStationId().equals(section.getDownStationId()) || s.getUpStationId().equals(section.getUpStationId()))
                .findFirst();

        if (optionalMiddleSection.isEmpty()) {
            return false;
        }

        var middleSection = optionalMiddleSection.get();

        var newDistance = middleSection.getDistance() - section.getDistance();
        if (middleSection.getUpStationId().equals(section.getUpStationId())) {
            middleSection.updateUpStation(section.getDownStation(), newDistance);
        } else {
            middleSection.updateDownStation(section.getUpStation(), newDistance);
        }

        sections.add(section);
        return true;
    }

    private Optional<Section> getFirstSection() {
        return this.sections.stream()
                .filter(s -> isFirstSection(s.getUpStationId().orElseThrow(IllegalStateException::new)))
                .findFirst();
    }

    private boolean isFirstSection(Long upStationId) {
        return this.sections.stream()
                .noneMatch(s -> s.isDownStationId(upStationId));
    }

    private Optional<Section> getLastSection() {
        return this.sections.stream()
                .filter(s -> isLastSection(s.getDownStationId().orElseThrow(IllegalStateException::new)))
                .findFirst();
    }

    private boolean isLastSection(Long downStationId) {
        return this.sections.stream()
                .noneMatch(s -> s.isUpStationId(downStationId));
    }


    private boolean hasStation(Station station) {
        return this.sections.stream()
                .anyMatch(section ->
                        section.getUpStationId().map(id -> id.equals(station.getId())).orElse(false)
                                || section.getDownStationId().map(id -> id.equals(station.getId())).orElse(false)
                );

    }

    public Section removeSection(Long downStationId) {
        if (sections.size() <= MIN_SECTION_COUNT) {
            throw new UnsupportedOperationException(String.format(REMOVE_ERROR_COUNT, MIN_SECTION_COUNT));
        }

        var sectionToRemove = getLastSection().orElseThrow();
        if (!sectionToRemove.isDownStationId(downStationId)) {
            throw new UnsupportedOperationException(REMOVE_ERROR_LAST);
        }

        this.sections.remove(sectionToRemove);
        return sectionToRemove;
    }

    public List<Station> getStations() {
        return this.sections.stream()
                .flatMap(s -> Stream.of(s.getUpStation(), s.getDownStation()))
                .distinct()
                .collect(Collectors.toList());
    }
}
