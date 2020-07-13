package nextstep.subway.line.application;

import com.google.common.collect.Lists;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.line.dto.LineStationCreateRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineStationService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineStationService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public void addLineStation(Long lineId, LineStationCreateRequest request) {
        checkAddLineStationValidation(request);

        Line line = findLineById(lineId);
        LineStation lineStation = new LineStation(request.getStationId(), request.getPreStationId(), request.getDistance(), request.getDuration());
        line.addLineStation(lineStation);
    }

    public void removeLineStation(Long lineId, Long stationId) {
        Line line = findLineById(lineId);
        line.removeLineStationById(stationId);
    }

    private void checkAddLineStationValidation(LineStationCreateRequest request) {
        List<Station> stations = stationRepository.findAllById(Lists.newArrayList(request.getPreStationId(), request.getStationId()));
        List<Long> stationIds = stations.stream().map(Station::getId).collect(Collectors.toList());
        if (!stationIds.contains(request.getStationId())) {
            throw new RuntimeException();
        }
        if (request.getPreStationId() != null && !stationIds.contains(request.getPreStationId())) {
            throw new RuntimeException();
        }
    }

    private Line findLineById(Long lineId) {
        return lineRepository.findById(lineId).orElseThrow(RuntimeException::new);
    }
}