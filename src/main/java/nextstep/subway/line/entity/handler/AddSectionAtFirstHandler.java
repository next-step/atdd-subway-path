package nextstep.subway.line.entity.handler;

import nextstep.subway.common.exception.CreationValidationException;
import nextstep.subway.line.entity.Section;
import nextstep.subway.line.entity.Sections;

public class AddSectionAtFirstHandler implements SectionAdditionHandler{
    @Override
    public boolean checkApplicable(Sections sections, Section section) {
        return sections.getFirstStation().equalsId(section.getDownStation());
    }

    @Override
    public void validate(Sections sections, Section section) {
        validateNewSectionUpStationIsNewcomer(sections, section);
    }

    @Override
    public void apply(Sections sections, Section newSection) {
        sections.forceSectionAddition(newSection);
    }

    private void validateNewSectionUpStationIsNewcomer(Sections sections, Section section) {
        if (sections.hasStation(section.getUpStation())) {
            throw new CreationValidationException("새로운 구간의 하행역이 해당 노선에 등록되어있는 역임.");
        }
    }
}
