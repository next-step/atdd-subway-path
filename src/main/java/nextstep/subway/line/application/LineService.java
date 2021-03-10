package nextstep.subway.line.application;

import nextstep.subway.exceptions.AlreadyExistsEntityException;
import nextstep.subway.exceptions.NotFoundLineException;
import nextstep.subway.exceptions.NotFoundStationException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    public static final String LINE_EXCEPTION = "%s 노선";

    private LineRepository lineRepository;
    private StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse saveLine(LineRequest request) {
        String requestName = request.getName();
        if (lineRepository.existsByName(requestName)) {
            throw new AlreadyExistsEntityException(String.format(LINE_EXCEPTION, requestName));
        }

        Station upStation = findStationById(request.getUpStationId());
        Station downStation = findStationById(request.getDownStationId());

        Section section = request.toSection(upStation, downStation);
        Line persistLine = lineRepository.save(request.toLine(section));

        return LineResponse.of(persistLine);
    }

    public List<Line> findLines() {
        return lineRepository.findAll();
    }

    public List<LineResponse> findLineResponses() {
        List<Line> persistLines = lineRepository.findAll();
        return persistLines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLineResponseById(Long id) {
        Line persistLine = findLineById(id);
        return LineResponse.of(persistLine);
    }

    @Modifying
    public void updateLine(Long id, LineRequest lineUpdateRequest) {
        Line line = findLineById(id);
        line.update(lineUpdateRequest.toLine());
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public void addSection(Long lineId, SectionRequest request) {
        Line line = findLineById(lineId);
        Station upStation = stationService.findStationById(request.getUpStationId());
        Station downStation = stationService.findStationById(request.getDownStationId());

        Section section = request.toSection(upStation, downStation);

        line.addSection(section);
    }

    public void removeSection(Long lineId, Long stationId) {
        Line line = findLineById(lineId);
        line.deleteLastSection(stationId);
    }

    private Line findLineById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new NotFoundLineException(id));
    }

    private Station findStationById(Long id) {
        return stationService.findById(id);
    }

    
}
