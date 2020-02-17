package atdd.station.service;

import atdd.station.domain.SubwayLine;
import atdd.station.domain.SubwayLineRepository;
import atdd.station.dto.path.PathFindResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PathService {
    private SubwayLineRepository subwayLineRepository;

    public PathService(SubwayLineRepository subwayLineRepository) {
        this.subwayLineRepository = subwayLineRepository;
    }

    public PathFindResponseDto findPath(long startId, long endId) {
        List<SubwayLine> subwayLines = subwayLineRepository.findAll();
        return PathFindResponseDto.toDtoEntity(startId, endId, subwayLines);
    }
}
