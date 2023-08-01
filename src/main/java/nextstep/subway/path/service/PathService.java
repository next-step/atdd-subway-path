package nextstep.subway.path.service;

import nextstep.subway.exception.ErrorCode;
import nextstep.subway.exception.SubwayException;
import nextstep.subway.line.entity.Line;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.path.dto.GeneratedPathFinder;
import nextstep.subway.path.dto.PathDto;
import nextstep.subway.station.entity.Station;
import nextstep.subway.station.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PathService {

    private final StationRepository stationRepository;
    private final LineRepository lineRepository;
    public PathService(StationRepository stationRepository, LineRepository lineRepository) {
        this.stationRepository = stationRepository;
        this.lineRepository = lineRepository;
    }

    public PathDto findPath(Long source, Long target) {
        Station startStation = stationRepository.findById(source)
                .orElseThrow(() -> new SubwayException(ErrorCode.NOT_FOUND_STATION));
        Station endStation = stationRepository.findById(target)
                .orElseThrow(() -> new SubwayException(ErrorCode.NOT_FOUND_STATION));

        List<Line> lines = lineRepository.findAll();
        PathFinder pathFinder = new GeneratedPathFinder(lines);

        return PathDto.from(pathFinder.findPath(startStation, endStation));
    }
}
