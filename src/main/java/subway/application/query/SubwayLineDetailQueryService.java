package subway.application.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.application.query.in.SubwayLineDetailQuery;
import subway.application.query.response.SubwayLineResponse;
import subway.application.query.out.SubwayLineDetailQueryPort;

@Service
@Transactional(readOnly = true)
class SubwayLineDetailQueryService implements SubwayLineDetailQuery {

    private final SubwayLineDetailQueryPort subwayLineDetailQueryPort;

    public SubwayLineDetailQueryService(SubwayLineDetailQueryPort subwayLineDetailQueryPort) {
        this.subwayLineDetailQueryPort = subwayLineDetailQueryPort;
    }

    @Override
    public SubwayLineResponse findOne(Command command) {
        return subwayLineDetailQueryPort.findOne(command.getId());
    }
}
