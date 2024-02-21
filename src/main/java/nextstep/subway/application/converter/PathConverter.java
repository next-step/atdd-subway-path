package nextstep.subway.application.converter;

import nextstep.subway.application.dto.PathResult;
import nextstep.subway.dto.PathResponse;

public class PathConverter {

    public static PathResponse convertToPathResponse(PathResult pathResult) {
        return new PathResponse(pathResult.getStations(), pathResult.getDistance());
    }
}
