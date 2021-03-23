package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Lines;
import nextstep.subway.path.domain.exception.CannotFindPathException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.Stations;

public class PathFinder {

    private PathStrategy pathStrategy;

    public PathFinder(PathStrategy pathStrategy) {
        this.pathStrategy = pathStrategy;
    }

    public Stations findShortestPath(Station source, Station target) {
        if(source.equals(target)) {
            throw new CannotFindPathException("출발역과 도착역이 같을 순 없습니다");
        }
        try{
            Stations stations = pathStrategy.getPath(source, target);
            return stations;
        }catch (CannotFindPathException e){
            throw new CannotFindPathException("연결되지 않았습니다.");
        }
    }

}
