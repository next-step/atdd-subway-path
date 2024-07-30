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
    private static final String ADD_ERROR_INVALID_UPSTATION = "구간 추가 실패 - 구간의 상행역이 마지막 구간의 하행종점역이어야 합니다";
    private static final String ADD_ERROR_INVALID_DOWNSTATION = "구간 추가 실패 - 구간의 하행역 이미 등록되어있습니다";
    private static final String REMOVE_ERROR_COUNT = "구간 삭제 실패 - 구간이 %d 개 이하입니다.";
    private static final String REMOVE_ERROR_LAST = "구간 삭제 실패 - 마지막 구간만 제거할 수 있습니다.";
    @OneToMany(mappedBy = "subwayLine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void addSection(Section section) {
        var lastSection = getLastSection();
        if (lastSection.isEmpty()) {
            this.sections.add(section);
            return;
        }

        if (!lastSection.get().isDownStationId(section.getUpStationId())) {
            throw new UnsupportedOperationException(ADD_ERROR_INVALID_UPSTATION);
        }
        if (hasStation(section.getDownStationId())) {
            throw new UnsupportedOperationException(ADD_ERROR_INVALID_DOWNSTATION);
        }

        this.sections.add(section);
    }

    private boolean hasStation(Long id) {
        return getStations().stream()
                .anyMatch(s -> s.getId().equals(id));
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

    private Optional<Section> getLastSection() {
        return this.sections.stream()
                .filter(s -> isLastSection(s.getDownStationId()))
                .findFirst();
    }

    private boolean isLastSection(Long downStationId) {
        return this.sections.stream()
                .noneMatch(s -> s.isUpStationId(downStationId));
    }

    public List<Station> getStations() {
        return this.sections.stream()
                .flatMap(s -> Stream.of(s.getUpStation(), s.getDownStation()))
                .distinct()
                .collect(Collectors.toList());
    }
}
