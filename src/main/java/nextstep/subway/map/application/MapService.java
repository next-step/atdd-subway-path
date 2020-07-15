package nextstep.subway.map.application;

import nextstep.subway.line.application.LineService;
import nextstep.subway.map.dto.MapResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MapService {

    private final LineService lineService;

    public MapService(LineService lineService) {
        this.lineService = lineService;
    }

    @Transactional(readOnly = true)
    public MapResponse loadMap() {
        return new MapResponse();
    }
}
