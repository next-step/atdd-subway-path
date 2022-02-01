package nextstep.subway.domain;

import java.util.List;
import java.util.Objects;

public class SectionsIncludingRemoveStation {
    private Section sameUpStationSection;
    private Section sameDownStationSection;

    public SectionsIncludingRemoveStation() {
    }

    public void find(List<Section> sections, Station station) {
        sameUpStationSection = sections.stream()
            .filter(section -> section.getUpStation().equals(station))
            .findAny()
            .orElse(null);

        sameDownStationSection = sections.stream()
            .filter(section -> section.getDownStation().equals(station))
            .findAny()
            .orElse(null);
    }

    public boolean isEmpty() {
        return Objects.isNull(sameUpStationSection) && Objects.isNull(sameDownStationSection);
    }

    public boolean hasEndSideSection() {
        return Objects.isNull(sameUpStationSection) || Objects.isNull(sameDownStationSection);
    }

    public Section getEndSection() {
        if (!hasEndSideSection()) {
            throw new IllegalStateException("최상행 혹은 최하행 역이 없음.");
        }

        if (sameUpStationSection != null) {
            return sameUpStationSection;
        }
        return sameDownStationSection;
    }

    public boolean hasMidSection() {
        return Objects.nonNull(sameUpStationSection) && Objects.nonNull(sameDownStationSection);
    }

    public void handleRemoveMidCaseSection(Sections sections) {
        if (!this.hasMidSection()) {
            return;
        }

        if (Objects.nonNull(sameUpStationSection)) {
            sameUpStationSection.handleRemoveMidCase(sameDownStationSection);
            sections.remove(sameDownStationSection);
            return;
        }

        if (Objects.nonNull(sameDownStationSection)) {
            sameDownStationSection.handleRemoveMidCase(sameUpStationSection);
            sections.remove(sameUpStationSection);
            return;
        }
    }
}
