package nextstep.subway.linesection.remove;

import nextstep.subway.linesection.LineSections;
import nextstep.subway.station.Station;

public class EndStationRemover implements LineSectionRemover {
    @Override
    public boolean remove(LineSections sections, Station deleteStation) {
        if (deleteStation.equals(sections.getLastStation())) {
            sections.removeSection(sections.getLastSection());
            return true;
        }
        return false;
    }
}
