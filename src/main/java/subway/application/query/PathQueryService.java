package subway.application.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.application.mapper.PathResponseMapper;
import subway.application.query.in.PathQuery;
import subway.application.query.out.PathStationsLoadPort;
import subway.application.query.out.PathSearcherLoadPort;
import subway.application.response.PathResponse;
import subway.domain.PathSearcher;
import subway.domain.PathStation;
import subway.domain.PathSubway;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class PathQueryService implements PathQuery {

    private final PathStationsLoadPort pathStationMapLoadByInPort;
    private final PathSearcherLoadPort pathSearcherLoadPort;
    private final PathResponseMapper pathResponseMapper;

    @Override
    public PathResponse findOne(PathQuery.Command command) {
        PathStationsLoadPort.Response response = pathStationMapLoadByInPort.findAllBy(List.of(command.getStartStationId(), command.getEndStationId()));
        PathStation startStation = getStation(command.getStartStationId(), response);
        PathStation endStation = getStation(command.getEndStationId(), response);
        PathSearcher searcher = pathSearcherLoadPort.findOne()
                .orElseThrow(() -> new NoSuchElementException("최단 거리를 찾을 수 없습니다."));

        PathSubway search = searcher.search(startStation, endStation);
        return pathResponseMapper.from(search);
    }

    private static PathStation getStation(PathStation.Id id, PathStationsLoadPort.Response response) {
        return response.getStationBy(id)
                .orElseThrow(() -> new NoSuchElementException(String.format("id가 %d인 역은 존재하지 않습니다.", id.getValue())));
    }
}
