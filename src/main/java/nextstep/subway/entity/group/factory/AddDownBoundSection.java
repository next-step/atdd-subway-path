package nextstep.subway.entity.group.factory;

import nextstep.subway.entity.Section;

public class AddDownBoundSection implements SectionAddAction {

    private final Section newSection;
    private final Section originSection;

    private AddDownBoundSection(Section newSection, Section originSection) {
        this.newSection = newSection;
        this.originSection = originSection;
    }

    public static SectionAddAction of(Section newSection, Section originSection) {
        return new AddDownBoundSection(newSection, originSection);
    }

    @Override
    public void validate() {
        originSection.validationAddDistance(newSection.getDistance());
    }

    @Override
    public void addAction() {

        originSection.changeDownStation(newSection.getUpStation());
        originSection.minusDistance(newSection.getDistance());


    }

}
