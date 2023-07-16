package nextstep.subway.unit;

import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.Section;
import nextstep.subway.domain.station.Station;

public class LineFixture {

    public static final int DEFAULT_LINE_LENGTH = 10;

    public static Line makeLine(final Station upStation, final Station firstDownStation, Station... downStations) {
        final var line = new Line("신분당선", "bg-red-600", upStation, firstDownStation, 10);

        if (downStations.length == 0) {
            return line;
        }

        line.appendSection(new Section(firstDownStation, downStations[0], DEFAULT_LINE_LENGTH));

        if (downStations.length > 1) {
            for (int i = 0; i < downStations.length - 1; i++) {
                line.appendSection(new Section(downStations[i], downStations[i + 1], DEFAULT_LINE_LENGTH));
            }
        }

        return line;
    }

    public static Line makeLine(final Station upStation, final Station downStation, final int distance) {
        return new Line("신분당선", "bg-red-600", upStation, downStation, distance);
    }

    public static void appendSection(final Line line, final Station upStation, final Station downStation,
                                     final int distance) {
        line.appendSection(new Section(upStation, downStation, distance));
    }
}
