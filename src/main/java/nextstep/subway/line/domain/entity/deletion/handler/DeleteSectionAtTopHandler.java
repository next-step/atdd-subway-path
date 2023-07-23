package nextstep.subway.line.domain.entity.deletion.handler;

import nextstep.subway.line.domain.vo.Sections;
import nextstep.subway.station.entity.Station;

public class DeleteSectionAtTopHandler extends SectionDeletionHandler{
    @Override
    public boolean checkApplicable(Sections sections, Station station) {
        return sections.getFirstStation().equals(station);
    }

    @Override
    public void apply(Sections sections, Station station) {
        sections.forceSectionRemove(sections.getSectionByUpStation(station));
    }
}
