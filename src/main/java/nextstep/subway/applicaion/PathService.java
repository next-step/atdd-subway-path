package nextstep.subway.applicaion;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.line.LineRepository;
import nextstep.subway.domain.section.SectionRepository;
import nextstep.subway.domain.station.StationRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PathService {
    private final LineRepository lineRepository;
    private final SectionRepository sectionRepository;
    private final StationRepository stationRepository;

    public PathResponse findPath(Long sourceStationId, Long targetStationId) {
        return null;
    }
}
