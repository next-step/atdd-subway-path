package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        Line saveLine = lineRepository.save(new Line(request.getName(), request.getColor()));
        addSectionInLine(request, saveLine);

        return LineResponse.from(saveLine);
    }

    private void addSectionInLine(LineRequest request, Line line) {
        if (request.getUpStationId() != null && request.getDownStationId() != null && request.getDistance() != 0) {
            Station upStation = stationService.findById(request.getUpStationId());
            Station downStation = stationService.findById(request.getDownStationId());
            line.addSection(upStation, downStation, request.getDistance());
        }
    }

    @Transactional(readOnly = true)
    public List<LineResponse> showLines() {
        return LineResponse.from(lineRepository.findAll());
    }

    @Transactional(readOnly = true)
    public LineResponse findById(Long id) {
        Line findLine = findLine(id);
        return LineResponse.from(findLine);
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = findLine(id);

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

    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Station upStation = stationService.findById(sectionRequest.getUpStationId());
        Station downStation = stationService.findById(sectionRequest.getDownStationId());
        Line findLine = findLine(lineId);

        findLine.addSection(upStation, downStation, sectionRequest.getDistance());
    }

    public void deleteSection(Long lineId, Long stationId) {
        Line line = findLine(lineId);
        Station deleteStation = stationService.findById(stationId);
        Station findLastDownStation = line.getLastDownStation();

        if (!findLastDownStation.equals(deleteStation)) {
            throw new IllegalArgumentException();
        }

        int lastIndexOfSection = line.sizeOfSection() - 1;
        line.removeSection(lastIndexOfSection);
    }

    private Line findLine(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);
    }
}
