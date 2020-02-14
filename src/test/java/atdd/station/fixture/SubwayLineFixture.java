package atdd.station.fixture;

import atdd.station.domain.SubwayLine;

import java.util.ArrayList;
import java.util.List;

import static atdd.station.fixture.SubwaysFixture.SECOND_SUBWAYS;
import static atdd.station.fixture.SubwaysFixture.SUBWAYS;


public class SubwayLineFixture {
    public static final String SECOND_SUBWAY_LINE_NAME = "2호선";
    public static final String FIRST_SUBWAY_LINE = "1호선";
    public static final String NEW_BUNDANG_LINE_NAME = "신분당선";
    private static final String DEFAULT_START_TIME = "05:00";
    private static final String DEFAULT_END_TIME = "23:50";
    private static final String DEFAULT_INTERVAL = "10";

    public static final SubwayLine SECOND_SUBWAY_LINE = new SubwayLine(SECOND_SUBWAY_LINE_NAME);
    public static final SubwayLine NEW_BUNDANG_SUBWAY_LINE = new SubwayLine(NEW_BUNDANG_LINE_NAME);

    public static SubwayLine getSubwayLine(String line) {
        return SubwayLine.builder()
                .name(line)
                .startTime(DEFAULT_START_TIME)
                .endTime(DEFAULT_END_TIME)
                .intervalTime(DEFAULT_INTERVAL)
                .subways(SUBWAYS)
                .build();
    }

    public static SubwayLine getSecondSubwayLineName() {
        return SubwayLine.builder()
                .name(SECOND_SUBWAY_LINE_NAME)
                .startTime(DEFAULT_START_TIME)
                .endTime(DEFAULT_END_TIME)
                .intervalTime(DEFAULT_INTERVAL)
                .subways(SECOND_SUBWAYS)
                .build();
    }

    public static SubwayLine getFirstSubwayLine() {
        return SubwayLine.builder()
                .name(FIRST_SUBWAY_LINE)
                .startTime(DEFAULT_START_TIME)
                .endTime(DEFAULT_END_TIME)
                .intervalTime(DEFAULT_INTERVAL)
                .subways(SUBWAYS)
                .build();
    }

    public static List<SubwayLine> getSubwayLines() {
        List<SubwayLine> subwayLines = new ArrayList<>();

        subwayLines.add(getFirstSubwayLine());
        subwayLines.add(getSecondSubwayLineName());

        return subwayLines;
    }
}
