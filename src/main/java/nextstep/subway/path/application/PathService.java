package nextstep.subway.path.application;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PathService {

    private final LineService lineService;
    private final StationService stationService;

    public PathService(LineService lineService,
                       StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    public PathResponse findPaths(Long source, Long target) {
        List<Section> allSection = findAllSection();

        Station sourceStation = stationService.findByStation(source);
        Station targetStation = stationService.findByStation(target);

        PathFinder paths = new PathFinder(allSection);
        return paths.getPath(sourceStation, targetStation);
    }

    private List<Section> findAllSection() {
        List<Line> allLine = lineService.findAll();
         return allLine.stream()
                .map(line -> line.getSections().getSections())
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
