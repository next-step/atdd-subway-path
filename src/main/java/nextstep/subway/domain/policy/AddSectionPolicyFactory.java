package nextstep.subway.domain.policy;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;

import java.util.List;

public class AddSectionPolicyFactory {

    public static AddSectionPolicy of(Sections sections, List<Section> sectionList, Section newSection, Station firstStation, Station lastStation) {
        if (sections.isEmpty()) {
            return new AddEmptySectionPolicy(sections, sectionList, newSection);
        }

        if (newSection.getDownStation().equals(firstStation)) {
            return new AddFirstSectionPolicy(sections, sectionList, newSection);
        }

        if (newSection.getUpStation().equals(lastStation)) {
            return new AddLastSectionPolicy(sections, sectionList, newSection);
        }

        return new AddBetweenSectionPolicy(sections, sectionList, newSection);
    }


}
