package nextstep.subway.linesection.remove;

import nextstep.subway.linesection.LineSections;
import nextstep.subway.station.Station;

public class FirstStationRemover implements LineSectionRemover {
    @Override
    public void remove(LineSections sections, Station deleteStation) {
        sections.removeByIndex(0);
    }

    @Override
    public boolean support(LineSections sections, Station deleteStation) {
        return deleteStation.equals(sections.getFirstStation());
    }
}
