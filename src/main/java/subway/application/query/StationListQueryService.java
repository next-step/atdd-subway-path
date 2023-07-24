package subway.application.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.application.query.out.StationListQueryPort;
import subway.application.query.response.StationResponse;
import subway.application.query.in.StationListQuery;

import java.util.List;

@Service
@Transactional(readOnly = true)
class StationListQueryService implements StationListQuery {

    private final StationListQueryPort stationListQueryPort;

    StationListQueryService(StationListQueryPort stationListQueryPort) {
        this.stationListQueryPort = stationListQueryPort;
    }

    @Override
    public List<StationResponse> findAll() {
        return stationListQueryPort.findAll();
    }
}
