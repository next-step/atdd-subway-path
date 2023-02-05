package nextstep.subway.domain.sections.strategy;

import java.util.List;

import nextstep.subway.domain.Section;

public class ChangeableSections {
    private final List<Section> additionalSections;
    private final List<Section> deprecatedSections;

    public ChangeableSections(List<Section> additionalSections, List<Section> deprecatedSections) {
        this.additionalSections = additionalSections;
        this.deprecatedSections = deprecatedSections;
    }

    public List<Section> getAdditionalSections() {
        return additionalSections;
    }

    public List<Section> getDeprecatedSections() {
        return deprecatedSections;
    }
}
