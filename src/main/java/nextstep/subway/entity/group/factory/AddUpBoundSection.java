package nextstep.subway.entity.group.factory;

import nextstep.subway.entity.Line;
import nextstep.subway.entity.Section;
import nextstep.subway.entity.Station;
import nextstep.subway.entity.group.SectionGroup;

public class AddUpBoundSection implements SectionAddAction {

    @Override
    public void validate(Station upStation, Station downStation, int distance, Section section,
        SectionGroup sectionGroup) {

        section.validationAddDistance(distance);

        sectionGroup.validateExistStation(downStation.getId());

    }

    @Override
    public Section addAction(Line line, Station upStation, Station downStation, int distance,
        Section nowSection, SectionGroup sectionGroup) {

        nowSection.changeUpStation(downStation);
        nowSection.minusDistance(distance);

        if (nowSection.isUpEndPointSection()) {
            nowSection.cancelOfUpEndPoint();
            return new Section(line, upStation, downStation, distance, true, false);
        }

        return new Section(line, upStation, downStation, distance, false, false);
    }

    @Override
    public boolean isAdd() {
        return true;
    }
}
