package nextstep.subway.entity.group.factory.add;

import nextstep.subway.entity.Section;

public class AddUpBoundSection implements SectionAddAction {

    private final Section newSection;
    private final Section originSection;

    private AddUpBoundSection(Section newSection, Section originSection) {
        this.newSection = newSection;
        this.originSection = originSection;
    }

    public static AddUpBoundSection of(Section newSection, Section originSection) {
        return new AddUpBoundSection(newSection, originSection);
    }

    @Override
    public void validate() {
        originSection.validationAddDistance(newSection.getDistance());
    }

    @Override
    public void addAction() {

        originSection.changeUpStation(newSection.getDownStation());
        originSection.minusDistance(newSection.getDistance());

    }

}
