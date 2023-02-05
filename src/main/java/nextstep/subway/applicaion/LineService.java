package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.common.exception.NoExistLineException;
import nextstep.subway.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.common.error.SubwayError.NO_FIND_LINE;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(final LineRepository lineRepository, final StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse saveLine(final LineRequest request) {
        if (!request.canAddSection()) {
            return LineResponse.createResponse(lineRepository.save(request.toEntity()));
        }

        Station upStation = stationService.findById(request.getUpStationId());
        Station downStation = stationService.findById(request.getDownStationId());
        final Line saveLine = save(request.toEntityAddStation(upStation, downStation));

        return LineResponse.createResponse(saveLine);
    }

    public List<LineResponse> showLines() {
        return lineRepository.findAll().stream()
                .map(LineResponse::createResponse)
                .collect(Collectors.toList());
    }

    public LineResponse findById(final Long id) {
        return LineResponse.createResponse(findLine(id));
    }

    @Transactional
    public void updateLine(final Long id, final LineRequest lineRequest) {
        final Line line = findLine(id);

        if (lineRequest.getName() != null) {
            line.setName(lineRequest.getName());
        }
        if (lineRequest.getColor() != null) {
            line.setColor(lineRequest.getColor());
        }
    }

    @Transactional
    public void deleteLine(final Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public void addSection(final Long lineId, final SectionRequest sectionRequest) {
        final Station upStation = stationService.findById(sectionRequest.getUpStationId());
        final Station downStation = stationService.findById(sectionRequest.getDownStationId());
        final Line line = findLine(lineId);

        line.addSection(upStation, downStation, sectionRequest.getDistance());
    }

    @Transactional
    public void deleteSection(final Long lineId, final Long stationId) {
        final Line line = findLine(lineId);
        final Station station = stationService.findById(stationId);

        line.removeSection(station);
    }

    public Line findLine(final Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new NoExistLineException(NO_FIND_LINE.getMessage()));
    }

    private Line save(final Line line) {
        final Sections sections = line.getSections();
        sections.addLine(line);
        return lineRepository.save(line);
    }
}
