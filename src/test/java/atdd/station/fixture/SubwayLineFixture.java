package atdd.station.fixture;

import atdd.station.domain.Edge;
import atdd.station.domain.Edges;
import atdd.station.domain.SubwayLine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static atdd.station.fixture.StationFixture.*;
import static atdd.station.fixture.SubwaysFixture.SECOND_SUBWAYS;
import static atdd.station.fixture.SubwaysFixture.SUBWAYS;


public class SubwayLineFixture {
    public static final String SECOND_SUBWAY_LINE_NAME = "2호선";
    public static final String FIRST_SUBWAY_LINE_NAME = "1호선";
    public static final String NEW_BUNDANG_SUBWAY_LINE_NAME = "신분당선";
    public static final String BUNDANG_SUBWAY_LINE_NAME = "분당선";
    private static final String DEFAULT_START_TIME = "05:00";
    private static final String DEFAULT_END_TIME = "23:50";
    private static final String DEFAULT_INTERVAL = "10";
    public static final SubwayLine SECOND_SUBWAY_LINE = new SubwayLine(SECOND_SUBWAY_LINE_NAME);
    public static final SubwayLine NEW_BUNDANG_SUBWAY_LINE = new SubwayLine(NEW_BUNDANG_SUBWAY_LINE_NAME);
    public static final SubwayLine BUNDANG_SUBWAY_LINE = new SubwayLine(BUNDANG_SUBWAY_LINE_NAME);
    public static final SubwayLine FIRST_SUBWAY_LINE = new SubwayLine(FIRST_SUBWAY_LINE_NAME);

    public static final Edge FIRST_EDGE = new Edge(KANGNAM_STATION, YUCKSAM_STATON, FIRST_SUBWAY_LINE, 5);
    public static final Edge SECOND_EDGE = new Edge(PANGYO_STATION, SUNLENG_STATON, SECOND_SUBWAY_LINE, 10);
    public static final Edge THIRD_EDGE = new Edge(YUCKSAM_STATON, SUNLENG_STATON, SECOND_SUBWAY_LINE, 10);
    public static final Edge FORTH_EDGE = new Edge(SUNLENG_STATON, KANGNAM_STATION, SECOND_SUBWAY_LINE, 10);

    public static final Edges FIRST_EDGES = new Edges(Arrays.asList(FIRST_EDGE, SECOND_EDGE));
    public static final Edges SECOND_EDGES = new Edges(Arrays.asList(THIRD_EDGE, FORTH_EDGE));


    public static SubwayLine getSubwayLine(String line) {
        return SubwayLine.builder()
                .name(line)
                .startTime(DEFAULT_START_TIME)
                .endTime(DEFAULT_END_TIME)
                .intervalTime(DEFAULT_INTERVAL)
                .subways(SUBWAYS)
                .edges(FIRST_EDGES)
                .build();
    }

    public static SubwayLine getSecondSubwayLine() {
        return SubwayLine.builder()
                .id(0L)
                .name(SECOND_SUBWAY_LINE_NAME)
                .startTime(DEFAULT_START_TIME)
                .endTime(DEFAULT_END_TIME)
                .intervalTime(DEFAULT_INTERVAL)
                .subways(SECOND_SUBWAYS)
                .edges(SECOND_EDGES)
                .build();
    }

    public static SubwayLine getFirstSubwayLine() {
        return SubwayLine.builder()
                .id(1L)
                .name(FIRST_SUBWAY_LINE_NAME)
                .startTime(DEFAULT_START_TIME)
                .endTime(DEFAULT_END_TIME)
                .intervalTime(DEFAULT_INTERVAL)
                .subways(SUBWAYS)
                .edges(FIRST_EDGES)
                .build();
    }

    public static List<SubwayLine> getSubwayLines() {
        List<SubwayLine> subwayLines = new ArrayList<>();

        subwayLines.add(getFirstSubwayLine());
        subwayLines.add(getSecondSubwayLine());

        return subwayLines;
    }
}
