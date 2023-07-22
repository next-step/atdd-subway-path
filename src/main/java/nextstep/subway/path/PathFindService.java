package nextstep.subway.path;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Sections;
import nextstep.subway.section.service.SectionReadService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.support.ErrorCode;
import nextstep.subway.support.SubwayException;

@Service
@Slf4j
@RequiredArgsConstructor
public class PathFindService {
    private final SectionReadService sectionReadService;
    private final ShortestPathFinder shortestPathFinder;

    @Transactional(readOnly = true)
    public ShortestPath getPath(Station sourceStation, Station targetStation) {
        if (sourceStation.equals(targetStation)) {
            throw new SubwayException(ErrorCode.PATH_SOURCE_TARGET_SHOULD_DIFFERENT);
        }

        Sections allSections = new Sections(sectionReadService.getAll());

        return shortestPathFinder.find(allSections, sourceStation, targetStation);
    }
}
