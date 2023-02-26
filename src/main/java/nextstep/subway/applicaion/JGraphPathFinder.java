package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.ShortPathFinder;
import nextstep.subway.domain.Station;
import nextstep.subway.global.error.exception.ErrorCode;
import nextstep.subway.global.error.exception.InvalidValueException;
import org.springframework.stereotype.Component;

import java.util.List;

import static nextstep.subway.domain.graphMaker.makeSubwayGraph;

@Component
public class JGraphPathFinder {

    public PathResponse find(List<Line> lines, Station source, Station target) {
        checkSameStations(source, target);
        checkExistStationsInLines(lines, source, target);
        return ShortPathFinder.findShortPath(makeSubwayGraph(lines), source, target);
    }


    private void checkSameStations(Station source, Station target) {
        if (source.equals(target)) {
            throw new InvalidValueException(ErrorCode.START_STATION_MUST_NOT_SAME_END_STATION);
        }
    }

    private void checkExistStationsInLines(List<Line> lines, Station source, Station target) {
        boolean isContainSource = false;
        boolean isContainTarget = false;
        for (Line line : lines) {
            isContainSource |= line.isContainStation(source);
            isContainTarget |= line.isContainStation(target);

            if (isContainSource && isContainTarget) {
                return;
            }
        }
        throw new InvalidValueException(ErrorCode.NOT_EXISTS_STATIONS);
    }
}
