package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.exception.DuplicateException;
import nextstep.subway.applicaion.exception.NotExistLineException;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;
    private StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse saveLine(LineRequest request) {
        validateDuplicatedLine(request);

        Station upStation = stationService.findById(request.getUpStationId());
        Station downStation = stationService.findById(request.getDownStationId());

        Line line = lineRepository.save(Line.of(request.getName(), request.getColor(), upStation, downStation, request.getDistance()));

        return LineResponse.from(line);
    }

    private void validateDuplicatedLine(LineRequest lineRequest) {
        boolean existsLine = lineRepository.existsLineByName(lineRequest.getName());

        if (existsLine) {
            throw new DuplicateException(lineRequest.getName());
        }
    }

    @Transactional(readOnly = true)
    public List<LineResponse> showLines() {
        return lineRepository.findAll().stream()
                .map(line -> LineResponse.from(line))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findById(Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new NotExistLineException(id));

        return LineResponse.from(line);
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id).orElseThrow(IllegalArgumentException::new);

        if (lineRequest.getName() != null) {
            line.setName(lineRequest.getName());
        }
        if (lineRequest.getColor() != null) {
            line.setColor(lineRequest.getColor());
        }
    }

    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    public LineResponse addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new NotExistLineException(lineId));

        Station upStation = stationService.findById(sectionRequest.getUpStationId());

        Station downStation = stationService.findById(sectionRequest.getDownStationId());

        if (line.isNotEqualDownStation(upStation)) {
            throw new IllegalArgumentException("노선의 하행선이 구간의 상행선과 다릅니다.");
        }

        if (line.existStation(downStation)) {
            throw new IllegalArgumentException("해당 역은 이미 노선에 등록되어 있습니다.");
        }

        line.addSection(upStation, downStation, sectionRequest.getDistance());

        return LineResponse.from(line);
    }

    public void deleteSection(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new NotExistLineException(lineId));

        Station station = stationService.findById(stationId);

        line.deleteSection(station);
    }
}
