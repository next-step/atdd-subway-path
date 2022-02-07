package nextstep.subway.handler.validator;

import nextstep.subway.handler.exception.ExploreException;
import org.jgrapht.GraphPath;

import static nextstep.subway.handler.exception.ErrorCode.TWO_STATIONS_IS_SAME;
import static nextstep.subway.handler.exception.ErrorCode.TWO_STATIONS_NOT_LINKED;

public class ExploreValidator {
    private final static GraphPath NOT_FOUND = null;

    public static void validateNotFound(GraphPath path) {
        if (path == NOT_FOUND) {
            throw new ExploreException(TWO_STATIONS_NOT_LINKED);
        }
    }

    public static void validateStationsIsSame(String source, String target) {
        if (source.equals(target)) {
            throw new ExploreException(TWO_STATIONS_IS_SAME);
        }
    }
}
