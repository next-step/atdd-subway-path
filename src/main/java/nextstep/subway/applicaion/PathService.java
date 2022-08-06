package nextstep.subway.applicaion;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Paths;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PathService {

    private final LineQueryService lineQueryService;

    public PathResponse getShortestPath(Long source, Long target) {
        Paths paths = Paths.from(lineQueryService.findLines());
        return PathResponse.from(paths.getShortestPath(source, target));
    }

}
