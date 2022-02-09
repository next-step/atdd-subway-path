package nextstep.subway.domain;

import java.util.Set;

public class SectionsIncludingStation {
    private Section upSection;
    private Section downSection;

    public SectionsIncludingStation(Set<Section> sections, Station station) {
        this.upSection = sections.stream()
                .filter(section -> section.hasDownStation(station))
                .findAny()
                .orElse(Section.EMPTY);
        this.downSection = sections.stream()
                .filter(section -> section.hasUpStation(station))
                .findAny()
                .orElse(Section.EMPTY);
    }

    public boolean hasNotAnySection() {
        return !hasUpSection() && !hasDownSection();
    }

    public boolean hasOnlyOneSection() {
        return (hasUpSection() && !hasDownSection()) ||
                (!hasUpSection() && hasDownSection());
    }

    public Section getAnyOneSection() {
        if (hasUpSection()) {
            return upSection;
        }

        return downSection;
    }

    private boolean hasUpSection() {
        return !upSection.isEmpty();
    }

    private boolean hasDownSection() {
        return !downSection.isEmpty();
    }

    public Section getUpSection() {
        return upSection;
    }

    public Section getDownSection() {
        return downSection;
    }
}
