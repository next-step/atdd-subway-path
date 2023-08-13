package nextstep.subway.linesection.remove;

import nextstep.subway.linesection.LineSections;
import nextstep.subway.station.Station;

public interface LineSectionRemover {
    /**
     *
     * @param sections
     * @param deleteStation
     * @return removed : boolean
     */
    void remove(LineSections sections, Station deleteStation);

    boolean support(LineSections sections, Station deleteStation);
}
