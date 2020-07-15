package nextstep.subway.map.application;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.map.dto.MapResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MapService {

    private final LineService lineService;

    public MapService(LineService lineService) {
        this.lineService = lineService;
    }

    @Transactional(readOnly = true)
    public MapResponse loadMap() {
        List<LineResponse> lineResponses = lineService.findAllLinesWithStations();
        return new MapResponse(lineResponses);
    }
}
