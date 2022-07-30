package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PathService {

    private final StationService stationService;

    private final LineRepository lineRepository;

    public PathService(StationService stationService, LineRepository lineRepository) {
        this.stationService = stationService;
        this.lineRepository = lineRepository;
    }

    public PathResponse findPaths(Long source, Long target) {
        if (Objects.isNull(source) || Objects.isNull(target)) {
            throw new BusinessException("source 나 target이 Null 일 수 없습니다", HttpStatus.BAD_REQUEST);
        }

        Station sourceStation = stationService.findById(source);
        Station targetStation = stationService.findById(target);

        PathFinder pathFinder = new PathFinder(getAllSections());
        List<Station> stationList = pathFinder.getShortestPath(sourceStation, targetStation);
        long distance = pathFinder.getSumOfDistance(sourceStation, targetStation);

        return new PathResponse(distance, stationList);
    }

    private List<Section> getAllSections() {
        return lineRepository.findAll()
                .stream()
                .flatMap(line -> line.getSectionList()
                        .stream())
                .collect(Collectors.toList());
    }
}
