package nextstep.subway.line;

import nextstep.exception.BadRequestException;
import nextstep.subway.section.SectionRequest;
import nextstep.subway.section.SectionService;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final SectionService sectionService;

    public LineService(LineRepository lineRepository, StationRepository stationRepository, SectionService sectionService) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionService = sectionService;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Line line = lineRequest.createLine();
        line = lineRepository.save(line);

        return new LineResponse(line);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(Long id) {
        Line line = lineRepository.findById(id).orElseThrow();
        LineResponse lineResponse = new LineResponse(line);
        return lineResponse;
    }

    @Transactional
    public void modifyLine(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id).orElseThrow();
        line.changeLineInfo(lineRequest.getName(), lineRequest.getColor());
    }

    @Transactional
    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = lineRepository.findById(lineId).orElseThrow();
        sectionService.addSection(line, sectionRequest);
    }

    @Transactional
    public void deleteSection(Long lineId, Long deleteStationId) {
        Line line = lineRepository.findById(lineId).orElseThrow();
        Station station = stationRepository.findById(deleteStationId).orElseThrow(
                () -> new BadRequestException("존재하지 않는 역 입니다.")
        );

        line.deleteSection(station);
    }
}
