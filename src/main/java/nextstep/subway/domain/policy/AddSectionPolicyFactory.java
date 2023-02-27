package nextstep.subway.domain.policy;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;

import java.util.List;

public class AddSectionPolicyFactory {

    public static AddSectionPolicy of(Sections sections, List<Section> sectionList, Section newSection) {
        if (sections.isEmpty()) {
            return new AddEmptySectionPolicy(sectionList, newSection);
        }

        Station firstStation = sections.getFirstStation();
        if (newSection.getDownStation().equals(firstStation)) {
            return new AddFirstSectionPolicy(sectionList, newSection);
        }

        Station lastStation = sections.getLastStation();
        if (newSection.getUpStation().equals(lastStation)) {
            return new AddLastSectionPolicy(sectionList, newSection);
        }

        return new AddBetweenSectionPolicy(sectionList, newSection);
    }


}
