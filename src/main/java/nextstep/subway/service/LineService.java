package nextstep.subway.service;

import nextstep.subway.common.NotFoundLineException;
import nextstep.subway.controller.resonse.LineResponse;
import nextstep.subway.controller.resonse.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.StationRepository;
import nextstep.subway.domain.command.LineCreateCommand;
import nextstep.subway.domain.command.LineModifyCommand;
import nextstep.subway.domain.command.SectionAddCommand;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {

    private final StationRepository stationRepository;
    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineCreateCommand createCommand) {
        Station upStation = stationRepository.getReferenceById(createCommand.getUpStationId());
        Station downStation = stationRepository.getReferenceById(createCommand.getDownStationId());

        Line line = Line.builder()
                .upStation(upStation)
                .downStation(downStation)
                .distance(createCommand.getDistance())
                .name(createCommand.getName())
                .color(createCommand.getColor())
                .build();

        Line satedLine = lineRepository.save(line);

        return createLineResponse(satedLine);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
                .map(this::createLineResponse)
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(Long id) {
        Line line = requireGetById(id);
        return createLineResponse(line);
    }

    @Transactional
    public void modifyLine(Long id, LineModifyCommand modifyCommand) {
        Line line = requireGetById(id);
        line.modify(modifyCommand.getName(), modifyCommand.getColor());
    }

    @Transactional
    public void deleteLineById(Long id) {
        Line line = requireGetById(id);
        lineRepository.delete(line);
    }

    private Line requireGetById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new NotFoundLineException(id));
    }

    private LineResponse createLineResponse(Line line) {
        List<StationResponse> stationResponses = line.getStations().stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
        return new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                stationResponses,
                line.getDistance());
    }

    @Transactional
    public void addSection(Long lLineId, SectionAddCommand sectionAddCommand) {
        Line line = requireGetById(lLineId);

        Station upStation = stationRepository.getReferenceById(sectionAddCommand.getUpStationId());
        Station downStation = stationRepository.getReferenceById(sectionAddCommand.getDownStationId());

        Section savedSection = Section.of(upStation, downStation, sectionAddCommand.getDistance());
        line.add(savedSection);
    }

    @Transactional
    public void deleteStationAtLine(Long lineId, Long stationId) {
        Line line = requireGetById(lineId);

        Station targetStation = stationRepository.getReferenceById(stationId);

        line.remove(targetStation);
    }
}
