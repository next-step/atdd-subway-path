package nextstep.subway.domain;

import nextstep.subway.domain.exception.NotFoundException;
import nextstep.subway.domain.exception.SubwayException;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class PathFinder {
    private final ShortestPathStrategy<Station, Section> shortestPathStrategy;

    public PathFinder(ShortestPathStrategy<Station, Section> shortestPathStrategy, List<Line> lines) {
        List<Station> allStations = allStations(lines);
        List<Section> sections = getSections(lines);

        this.shortestPathStrategy = shortestPathStrategy;
        shortestPathStrategy.init(allStations, sections);
    }

    private List<Station> allStations(List<Line> lines) {
        return lines.stream().map(Line::getStations)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<Section> getSections(List<Line> lines) {
        return lines.stream()
                .map(Line::getSections)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public Path shortestPath(Long source, Long target) {
        Station sourceStation = findStation(source);
        Station targetStation = findStation(target);

        return shortestPathStrategy.shortestPath(sourceStation, targetStation)
                .orElseThrow(() -> new SubwayException(String.format("%s과 %s이 연결되어있지 않습니다.", sourceStation.getName(), targetStation.getName())));
    }

    private Station findStation(Long stationId) {
        return shortestPathStrategy.allVertices()
                .stream()
                .filter(station -> station.getId().equals(stationId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(stationId + "번 역을 찾을 수 없습니다."));
    }
}
