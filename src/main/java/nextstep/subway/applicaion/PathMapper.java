package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.dto.PathDto;
import org.springframework.stereotype.Component;

@Component
public class PathMapper {

    private final StationMapper stationMapper;

    public PathMapper(final StationMapper stationMapper) {
        this.stationMapper = stationMapper;
    }

    public PathResponse toResponseFrom(final PathDto path) {
        return new PathResponse(stationMapper.toResponseFrom(path.getNodes()), path.getWeight());
    }
}
