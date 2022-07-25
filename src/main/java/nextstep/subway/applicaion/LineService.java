package nextstep.subway.applicaion;

import static java.util.stream.Collectors.toList;

import java.util.List;
import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;
    private final StationService stationService;

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        Line line = lineRepository.save(new Line(request.getName(), request.getColor()));
        if (request.isStationNullCheck() && request.isDistanceZeroCheck()) {
            Station upStation = findStation(request.getUpStationId());
            Station downStation = findStation(request.getDownStationId());
            line.getSections().addSection(new Section(line, upStation, downStation, request.getDistance()));
        }
        return createLineResponse(line);
    }

    private Station findStation(Long stationId) {
        return stationService.findById(stationId);
    }

    public List<LineResponse> showLines() {
        return lineRepository.findAll().stream()
            .map(this::createLineResponse)
            .collect(toList());
    }

    public LineResponse findById(Long id) {
        return createLineResponse(getLineById(id));
    }

    private LineResponse createLineResponse(Line line) {
        return LineResponse.createLineResponse(line, createStationResponses(line));
    }

    private List<StationResponse> createStationResponses(Line line) {
        List<Station> stations = line.getSections().getAllStation();
        return stations.stream()
            .map(stationService::createStationResponse)
            .collect(toList());
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = getLineById(id);

        if (lineRequest.getName() != null) {
            line.changeName(lineRequest.getName());
        }
        if (lineRequest.getColor() != null) {
            line.changeColor(lineRequest.getColor());
        }
    }

    @Transactional
    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Station upStation = stationService.findById(sectionRequest.getUpStationId());
        Station downStation = stationService.findById(sectionRequest.getDownStationId());
        Line line = getLineById(lineId);

        line.getSections().addSection(new Section(line, upStation, downStation, sectionRequest.getDistance()));
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Sections sections = getLineById(lineId).getSections();

        Station station = stationService.findById(stationId);
        sections.isDeleteStationCheck(station);

        sections.removeLastSection();
    }

    private Line getLineById(Long lineId) {
        return lineRepository.findById(lineId).orElseThrow(IllegalArgumentException::new);
    }
}