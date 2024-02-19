package nextstep.subway.service.client;

import nextstep.subway.domain.Station;
import nextstep.subway.service.PathFinder;
import nextstep.subway.service.dto.LineDto;
import nextstep.subway.service.dto.PathDto;
import nextstep.subway.service.dto.StationDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JgraphtClient implements PathFinder {
    public JgraphtClient() {
    }

    public PathDto findShortestPath(List<LineDto> lines, Station sourceStation, Station targetStation) {
        // 아래는 타입 맞추느라고 임시로...
        return new PathDto(List.of(StationDto.from(sourceStation), StationDto.from(targetStation)), 1);
    }
}
