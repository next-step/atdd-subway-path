package subway.application.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.application.query.out.SubwayLineListQueryPort;
import subway.application.response.SubwayLineResponse;
import subway.application.query.in.SubwayLineListQuery;

import java.util.List;

@Service
@Transactional(readOnly = true)
class SubwayLineListQueryService implements SubwayLineListQuery {

    private final SubwayLineListQueryPort subwayLineListQueryPort;

    public SubwayLineListQueryService(SubwayLineListQueryPort subwayLineListQueryPort) {
        this.subwayLineListQueryPort = subwayLineListQueryPort;
    }

    @Override
    public List<SubwayLineResponse> findAll() {
        return subwayLineListQueryPort.findAll();
    }
}
