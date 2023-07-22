package nextstep.subway.path;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.section.service.SectionReadService;
import nextstep.subway.station.domain.Station;

@Service
@Slf4j
@RequiredArgsConstructor
public class PathFindService {
    private final SectionReadService sectionReadService;
    private final ShortestPathFinder shortestPathFinder;

    @Transactional(readOnly = true)
    public ShortestPath getPath(Station sourceStation, Station targetStation) {
        Line line = sectionReadService.getSection(sourceStation).getLine();

        Sections subSections = line.getSections()
                                   .getSubSections(sourceStation, targetStation);

        return shortestPathFinder.find(subSections, sourceStation, targetStation);
    }
}
