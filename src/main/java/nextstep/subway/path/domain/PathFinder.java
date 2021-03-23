package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Lines;
import nextstep.subway.path.domain.exception.CannotFindPathException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.Stations;

import java.util.List;

public class PathFinder {

    private PathStrategy pathStrategy;
    private Lines lines;

    public PathFinder(List<Line> lines, PathStrategy pathStrategy) {
        this.lines = new Lines(lines);
        this.pathStrategy = pathStrategy;
    }

    public Stations findShortestPath(Station source, Station target) {
        if(source.equals(target)) {
            throw new CannotFindPathException("출발역과 도착역이 같을 순 없습니다");
        }
        try{
            Stations stations = pathStrategy.getPath(source, target, lines);
            return stations;
        }catch (CannotFindPathException e){
            throw new CannotFindPathException("연결되지 않았습니다.");
        }
    }

}
