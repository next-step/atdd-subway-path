package nextstep.subway.linesection.remove;

import nextstep.subway.linesection.LineSections;
import nextstep.subway.station.Station;

public class EndStationRemover implements LineSectionRemover {
    @Override
    public void remove(LineSections sections, Station deleteStation) {
        sections.removeSection(sections.getLastSection());
    }

    @Override
    public boolean support(LineSections sections, Station deleteStation) {
        return deleteStation.equals(sections.getLastStation());
    }
}
