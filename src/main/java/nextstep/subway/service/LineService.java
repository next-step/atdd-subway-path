package nextstep.subway.service;

import nextstep.subway.common.NotFoundLineException;
import nextstep.subway.controller.request.SectionAddRequest;
import nextstep.subway.controller.resonse.LineResponse;
import nextstep.subway.controller.resonse.SectionResponse;
import nextstep.subway.controller.resonse.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.SectionRepository;
import nextstep.subway.repository.StationRepository;
import nextstep.subway.service.command.LineCreateCommand;
import nextstep.subway.service.command.LineModifyCommand;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository, SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    @Transactional
    public LineResponse saveStationLine(LineCreateCommand createCommand) {
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

        return createSubwayLineResponse(satedLine);
    }

    public List<LineResponse> findAllSubwayLines() {
        return lineRepository.findAll().stream()
                .map(this::createSubwayLineResponse)
                .collect(Collectors.toList());
    }

    public LineResponse findSubwayLine(Long id) {
        Line line = requireGetById(id);
        return createSubwayLineResponse(line);
    }

    @Transactional
    public void modifySubwayLine(Long id, LineModifyCommand modifyCommand) {
        Line line = requireGetById(id);
        line.modify(modifyCommand.getName(), modifyCommand.getColor());
    }

    @Transactional
    public void deleteSubwayLineById(Long id) {
        Line line = requireGetById(id);
        lineRepository.delete(line);
    }

    private Line requireGetById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new NotFoundLineException(id));
    }

    private LineResponse createSubwayLineResponse(Line line) {
        return new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                List.of(new StationResponse(line.getUpStation()),
                        new StationResponse(line.getDownStation())),
                line.getDistance());
    }

    @Transactional
    public SectionResponse addStationSection(Long subwayLineId, SectionAddRequest subwayLineCreateRequest) {
        Line line = requireGetById(subwayLineId);

        Station upStation = stationRepository.getReferenceById(subwayLineCreateRequest.getUpStationId());
        Station downStation = stationRepository.getReferenceById(subwayLineCreateRequest.getDownStationId());

        Section savedSection = sectionRepository.save(Section.of(upStation, downStation, subwayLineCreateRequest.getDistance()));
        line.expandLine(savedSection);

        return SectionResponse.of(savedSection);
    }

    @Transactional
    public void deleteStationAtLineSection(Long subwayLineId, Long stationId) {
        Line line = requireGetById(subwayLineId);

        Station targetStation = stationRepository.getReferenceById(stationId);

        line.shorten(targetStation);
    }
}
