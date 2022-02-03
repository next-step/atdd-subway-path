package nextstep.subway.domain;

import static nextstep.subway.exception.CommonExceptionMessages.NOT_EXIST_UP_DOWN_END_STATION;

import java.util.List;
import java.util.Objects;

public class SectionsIncludingRemoveStation {
    private Section sameUpStationSection;
    private Section sameDownStationSection;

    private SectionsIncludingRemoveStation(Section sameUpStationSection,
        Section sameDownStationSection) {
        this.sameUpStationSection = sameUpStationSection;
        this.sameDownStationSection = sameDownStationSection;
    }

    public static SectionsIncludingRemoveStation of(Section sameUpStationSection, Section sameDownStationSection) {
        return new SectionsIncludingRemoveStation(sameUpStationSection, sameDownStationSection);
    }

    public boolean isEmpty() {
        return Objects.isNull(sameUpStationSection) && Objects.isNull(sameDownStationSection);
    }

    public boolean hasEndSideSection() {
        return Objects.isNull(sameUpStationSection) || Objects.isNull(sameDownStationSection);
    }

    public Section getEndSection() {
        if (!hasEndSideSection()) {
            throw new IllegalStateException(NOT_EXIST_UP_DOWN_END_STATION);
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

        sameUpStationSection.handleRemoveMidCase(sameDownStationSection);
        sections.remove(sameDownStationSection);
    }
}
