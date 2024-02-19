package nextstep.subway.service;

import nextstep.subway.domain.entity.Path;
import nextstep.subway.domain.entity.Section;
import nextstep.subway.domain.entity.Station;
import nextstep.subway.domain.response.PathResponse;
import nextstep.subway.exception.ApplicationException;
import nextstep.subway.repository.SectionRepository;
import nextstep.subway.strategy.DijkstraStrategy;
import nextstep.subway.strategy.PathStrategy;
import org.springframework.stereotype.Service;

import java.util.List;

import static nextstep.subway.exception.ExceptionMessage.SAME_SOURCE_TARGET_EXCEPTION;

@Service
public class PathService {
    private final SectionRepository sectionRepository;
    private final StationService stationService;

    public PathService(SectionRepository sectionRepository, StationService stationService) {
        this.sectionRepository = sectionRepository;
        this.stationService = stationService;
    }

    public PathResponse findShortestPath(Long sourceId, Long targetId) {
        Station source = stationService.findById(sourceId);
        Station target = stationService.findById(targetId);
        // 출발역과 도착역이 같으면 예외처리
        if (source.equals(target)) {
            throw new ApplicationException(SAME_SOURCE_TARGET_EXCEPTION.getMessage());
        }

        List<Section> sections = sectionRepository.findAll();

        PathStrategy strategy = new DijkstraStrategy(sections);
        Path path = strategy.findShortestPath(source, target);

        // 최단 경로 리턴
        return path.createPathResponse();
    }
}
