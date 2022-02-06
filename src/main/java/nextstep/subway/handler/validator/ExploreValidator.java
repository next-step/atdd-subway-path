package nextstep.subway.handler.validator;

import nextstep.subway.domain.Station;
import nextstep.subway.handler.exception.ExploreException;
import org.jgrapht.GraphPath;

import static nextstep.subway.handler.exception.ErrorCode.TWO_STATIONS_IS_SAME;
import static nextstep.subway.handler.exception.ErrorCode.TWO_STATIONS_NOT_LINKED;

public class ExploreValidator {
    public static void validateNotFound(GraphPath path) {
        if (path == null) {
            throw new ExploreException(TWO_STATIONS_NOT_LINKED);
        }
    }

    public static void validateStationsIsSame(Station source, Station target) {
        if (source.equals(target)) {
            throw new ExploreException(TWO_STATIONS_IS_SAME);
        }
    }
}
