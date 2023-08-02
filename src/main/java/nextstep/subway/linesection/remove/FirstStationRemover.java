package nextstep.subway.linesection.remove;

import nextstep.subway.linesection.LineSections;
import nextstep.subway.station.Station;

public class FirstStationRemover implements LineSectionRemover {
    @Override
    public boolean remove(LineSections sections, Station deleteStation) {
        if (deleteStation.equals(sections.getFirstStation())) {
            sections.removeByIndex(0);
            return true;
        }
        return false;
    }
}
