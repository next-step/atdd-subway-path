package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.*;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse saveLine(LineSaveRequest request) {
        Station upStation = stationService.findById(request.getUpStationId());
        Station downStation = stationService.findById(request.getDownStationId());
        Line line = lineRepository.save(Line.createLine(request.getName(), request.getColor(), upStation, downStation, request.getDistance()));

        return createLineResponse(line);
    }

    public List<LineResponse> showLines() {
        return lineRepository.findAll().stream().map(this::createLineResponse).collect(Collectors.toList());
    }

    public LineResponse findById(Long lineId) {
        return createLineResponse(findLineById(lineId));
    }

    @Transactional
    public void updateLine(Long lineId, LineUpdateRequest lineRequest) {
        Line line = findLineById(lineId);

        line.updateName(lineRequest.getName());
        line.updateColor(lineRequest.getColor());
    }

    @Transactional
    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Station upStation = stationService.findById(sectionRequest.getUpStationId());
        Station downStation = stationService.findById(sectionRequest.getDownStationId());
        Line line = findLineById(lineId);

        line.addSection(upStation, downStation, sectionRequest.getDistance());
    }

    private LineResponse createLineResponse(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), createStationResponses(line));
    }

    private List<StationResponse> createStationResponses(Line line) {
        return line.getStations().stream().map(stationService::createStationResponse).collect(Collectors.toList());
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Line line = findLineById(lineId);
        Station station = stationService.findById(stationId);

        if (!line.isDeletableStation(station)) {
            throw new IllegalArgumentException("해당 정거장을 라인에서 삭제할 수 없습니다.");
        }

        line.removeStation(station);
    }

    public SectionResponse findSection(Long lineId, Long upStationId, Long downStationId) {

        Line line = findLineById(lineId);
        Section section = line.getSection(upStationId, downStationId);

        return new SectionResponse(section.getUpStation().getId(), section.getDownStation()
                .getId(), section.getDistance());
    }
    
    private Line findLineById(Long lineId) {
        return lineRepository.findById(lineId).orElseThrow(() -> new IllegalArgumentException("lineId에 해당하는 라인이 존재하지 않습니다."));
    }
}
