package nextstep.subway.line.entity.handler;

import nextstep.subway.common.exception.CreationValidationException;
import nextstep.subway.line.entity.Section;
import nextstep.subway.line.entity.Sections;

public class AddSectionAtLastHandler implements SectionAdditionHandler{
    @Override
    public boolean checkApplicable(Sections sections, Section section) {
        return sections.equalsLastStation(section.getUpStation());
    }

    @Override
    public void validate(Sections sections, Section section) {
        validateNewSectionDownStationIsNewcomer(sections, section);
    }

    @Override
    public void apply(Sections sections, Section newSection) {
        sections.forceSectionAddition(newSection);
    }

    private void validateNewSectionDownStationIsNewcomer(Sections sections, Section section) {
        if (sections.hasStation(section.getDownStation())) {
            throw new CreationValidationException("새로운 구간의 하행역이 해당 노선에 등록되어있는 역임.");
        }
    }
}
