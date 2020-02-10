package atdd.station.fixture;

import atdd.station.domain.Station;
import atdd.station.domain.SubwayLine;

import java.util.ArrayList;
import java.util.List;

import static atdd.station.fixture.StationFixture.*;

public class SubwayLineFixture {
    public static final String SECOND_SUBWAY_LINE = "2호선";
    public static final String FIRST_SUBWAY_LINE = "1호선";
    private static final String DEFAULT_START_TIME = "05:00";
    private static final String DEFAULT_END_TIME = "23:50";
    private static final String DEFAULT_INTERVAL = "10";

    public static SubwayLine getSubwayLine(String line) {
        List<Station> stations = new ArrayList<>();
        stations.add(KANNAM_STATON);
        stations.add(SINSA_STATION);

        return SubwayLine.builder()
                .name(line)
                .startTime(DEFAULT_START_TIME)
                .endTime(DEFAULT_END_TIME)
                .intervalTime(DEFAULT_INTERVAL)
                .stations(stations)
                .build();
    }

    public static SubwayLine getSecondSubwayLine() {
        return SubwayLine.builder()
                .name(SECOND_SUBWAY_LINE)
                .build();
    }

    public static SubwayLine getFirstSubwayLine() {
        return SubwayLine.builder()
                .name(FIRST_SUBWAY_LINE)
                .build();
    }

    public static List<SubwayLine> getSubwayLines() {
        List<SubwayLine> subwayLines = new ArrayList<>();

        subwayLines.add(getFirstSubwayLine());
        subwayLines.add(getSecondSubwayLine());

        return subwayLines;
    }
}
