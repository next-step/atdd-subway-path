package nextstep.subway.domain.policy;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;

import java.util.List;
import java.util.NoSuchElementException;

public class AddBetweenSectionPolicy extends AddSectionPolicyChain {

    @Override
    protected boolean supported(Sections sections, Section newSection) {
        return sections.isBetweenSection(newSection);
    }

    @Override
    protected void addSection(List<Section> sectionList, Section newSection) {
        Section originSection =  sectionList.stream()
                .filter(s -> isBetweenSection(s, newSection))
                .findFirst().orElseThrow(NoSuchElementException::new);

        sectionList.addAll(originSection.divide(newSection));

        sectionList.remove(originSection);
    }

    private boolean isBetweenSection(Section origin, Section target) {
        return origin.getUpStation().equals(target.getUpStation())
                || origin.getDownStation().equals(target.getDownStation());
    }

}
