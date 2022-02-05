package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.exception.NotFoundException;
import nextstep.subway.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PathService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public PathService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public PathResponse getPath(Long source, Long target) {
        List<Line> lineList = lineRepository.findAll();
        Station from = stationRepository.findById(source).orElseThrow(NotFoundException::new);
        Station to = stationRepository.findById(target).orElseThrow(NotFoundException::new);
        return PathFinder.findPath(lineList,from,to);
    }
}
