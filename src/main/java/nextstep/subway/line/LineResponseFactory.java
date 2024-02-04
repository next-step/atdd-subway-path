package nextstep.subway.line;

import nextstep.subway.station.StationResponseFactory;

public class LineResponseFactory {

    private LineResponseFactory() {
    }

    public static LineResponse createLineResponse(Line line) {
        return new LineResponse(line.getId(),
                line.getName(),
                line.getColor(),
                StationResponseFactory.createStationResponses(line));
    }

}
