package nextstep.subway.entity.group.factory;

import nextstep.subway.entity.Line;
import nextstep.subway.entity.Section;
import nextstep.subway.entity.Station;
import nextstep.subway.entity.group.SectionGroup;

public class AddDescendingEndPoint implements SectionAddAction {

    @Override
    public void validate(Station upStation, Station downStation, int distance,  Section section,
        SectionGroup sectionGroup) {

        sectionGroup.validateExistStation(downStation.getId());
    }

    @Override
    public Section addAction(Line line, Station upStation, Station downStation, int distance,
        Section nowSection, SectionGroup sectionGroup) {

        nowSection.cancelOfDownEndPoint();
        return new Section(line, upStation, downStation, distance, false, true);    }

    @Override
    public boolean isAdd() {
        return true;
    }
}
