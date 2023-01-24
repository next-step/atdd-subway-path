package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private LineRepository lineRepository;
    private StationService stationService;

    public LineService(final LineRepository lineRepository, final StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse saveLine(final LineRequest request) {
        final Line line = lineRepository.save(new Line(new LineInfo(request.getName(), request.getColor())));
        if (request.getUpStationId() != null && request.getDownStationId() != null && request.getDistance() != 0) {
            Station upStation = stationService.findById(request.getUpStationId());
            Station downStation = stationService.findById(request.getDownStationId());
            line.addSection(new Section(line, upStation, downStation, request.getDistance()));
        }
        return createLineResponse(line);
    }

    public List<LineResponse> showLines() {
        return lineRepository.findAll().stream()
                .map(this::createLineResponse)
                .collect(Collectors.toList());
    }

    public LineResponse findById(final Long id) {
        return createLineResponse(lineRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new));
    }

    @Transactional
    public void updateLine(final Long id, final LineRequest lineRequest) {
        final Line line = lineRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);

        LineInfo lineInfo = line.getLineInfo();
        if (lineRequest.getName() != null) {
            lineInfo.setName(lineRequest.getName());
        }
        if (lineRequest.getColor() != null) {
            lineInfo.setColor(lineRequest.getColor());
        }
        line.updateLine(lineInfo);
    }

    @Transactional
    public void deleteLine(final Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public void addSection(final Long lineId, final SectionRequest sectionRequest) {
        final Station upStation = stationService.findById(sectionRequest.getUpStationId());
        final Station downStation = stationService.findById(sectionRequest.getDownStationId());
        final Line line = lineRepository.findById(lineId)
                .orElseThrow(IllegalArgumentException::new);

        line.addSection(new Section(line, upStation, downStation, sectionRequest.getDistance()));
    }

    private LineResponse createLineResponse(final Line line) {
        return new LineResponse(
                line.getId(),
                line.getLineInfo().getName(),
                line.getLineInfo().getColor(),
                createStationResponses(line)
        );
    }

    private List<StationResponse> createStationResponses(Line line) {
        if (line.getSections().isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = line.getSections().stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());

        stations.add(0, line.getSections().get(0).getUpStation());

        return stations.stream()
                .map(it -> stationService.createStationResponse(it))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteSection(final Long lineId, final Long stationId) {
        final Line line = lineRepository.findById(lineId)
                .orElseThrow(IllegalArgumentException::new);
        final Station station = stationService.findById(stationId);

        line.removeSection(station);
    }
}
