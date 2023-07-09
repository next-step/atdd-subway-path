package nextstep.subway.unit;

import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.Section;
import nextstep.subway.domain.station.Station;

public class LineFixture {

    public static Line makeLine(final Station upStation, final Station firstDownStation, Station... downStations) {
        final var line = new Line("신분당선", "bg-red-600", upStation, firstDownStation, 10);

        if (downStations.length == 0) {
            return line;
        }

        line.appendSection(new Section(line, firstDownStation, downStations[0], 10));

        if (downStations.length > 1) {
            for (int i = 0; i < downStations.length - 1; i++) {
                line.appendSection(new Section(line, downStations[i], downStations[i + 1], 10));
            }
        }

        return line;
    }
}
