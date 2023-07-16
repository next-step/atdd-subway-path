package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.SectionRepository;
import org.springframework.stereotype.Component;

@Component
public class PathFinder {

    private SectionRepository sectionRepository;

    public PathFinder(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    public PathResponse find(Long sourceId, Long targetId) {
        return null;
    }
}
