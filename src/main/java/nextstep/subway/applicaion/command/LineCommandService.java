package nextstep.subway.applicaion.command;

import nextstep.subway.applicaion.dto.*;
import nextstep.subway.applicaion.query.LineQueryService;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.exception.line.DuplicateLineException;
import nextstep.subway.exception.station.StationNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineCommandService {

    private final LineRepository lineRepository;
    private final LineQueryService lineQueryService;
    private final StationRepository stationRepository;

    public LineCommandService(LineRepository lineRepository,
                              LineQueryService lineQueryService,
                              StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.lineQueryService = lineQueryService;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        if (lineRepository.existsByName(request.getName())) {
            throw new DuplicateLineException(request.getName());
        }

        Station upStation = findStationsById(request.getUpStationId());
        Station downStation = findStationsById(request.getDownStationId());

        Line line = lineRepository.save(
                Line.of(request.getName(), request.getColor(), upStation, downStation, request.getDistance()));

        return new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                line.getCreatedDate(),
                line.getModifiedDate()
        );
    }

    public LineAndSectionResponse addSection(long lineId, SectionRequest request) {
        Line line = lineQueryService.findLineById(lineId);
        Station upStation = findStationsById(request.getUpStationId());
        Station downStation = findStationsById(request.getDownStationId());

        line.addSection(upStation, downStation, request.getDistance());

        return lineQueryService.createShowLineResponse(line);
    }

    public void deleteSection(long lineId, long stationId) {
        Line line = lineQueryService.findLineById(lineId);
        Station deleteStation = findStationsById(stationId);
        line.deleteStation(deleteStation);
    }

    public void updateLine(long id, UpdateLineRequest request) {
        Line line = lineQueryService.findLineById(id);
        line.update(request.getName(), request.getColor());
    }

    public void deleteLine(long id) {
        lineRepository.deleteById(id);
    }

    private Station findStationsById(long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new StationNotFoundException(id));
    }

}
