package nextstep.subway.domain.policy;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;

import java.util.List;
import java.util.NoSuchElementException;

public class AddBetweenSectionPolicy extends AddSectionPolicy {

    private Sections sections;
    private List<Section> sectionList;
    private Section newSection;

    public AddBetweenSectionPolicy(Sections sections, List<Section> sectionList, Section newSection) {
        this.sections = sections;
        this.sectionList = sectionList;
        this.newSection = newSection;
    }

    @Override
    public void execute() {
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
