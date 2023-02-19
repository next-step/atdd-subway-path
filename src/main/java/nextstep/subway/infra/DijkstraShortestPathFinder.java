package nextstep.subway.infra;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.dto.PathResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static nextstep.subway.common.constants.ErrorConstant.NOT_FOUND_STATION;
import static nextstep.subway.common.constants.ErrorConstant.SAME_STATION;

@Component
public class DijkstraShortestPathFinder implements PathFinder {

    private List<Line> lines = new ArrayList<>();

    @Override
    public void init(List<Line> lines) {
        this.lines.addAll(lines);
    }

    @Override
    public PathResponse find(Station source, Station target) {
        validationFindStation(source, target);

        return null;
    }

    private void validationFindStation(Station source, Station target) {
        checkSameStation(source, target);
        checkExistStationsInLines(source, target);
    }

    private void checkSameStation(Station source, Station target) {
        if (source.equals(target)) {
            throw new IllegalArgumentException(SAME_STATION);
        }
    }

    private void checkExistStationsInLines(Station source, Station target) {
        boolean isContainSource = false;
        boolean isContainTarget = false;

        for (Line line : lines) {
            isContainSource |= line.isContainStation(source);
            isContainTarget |= line.isContainStation(target);

            if (isContainSource && isContainTarget) {
                return;
            }
        }

        throw new IllegalArgumentException(NOT_FOUND_STATION);
    }
}
