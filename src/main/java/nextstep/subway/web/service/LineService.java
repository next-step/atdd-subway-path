package nextstep.subway.web.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import nextstep.subway.domain.entity.Line;
import nextstep.subway.domain.entity.Section;
import nextstep.subway.domain.entity.Station;
import nextstep.subway.domain.repository.LineRepository;
import nextstep.subway.web.dto.request.AddSectionRequest;
import nextstep.subway.web.dto.request.LineCreateRequest;
import nextstep.subway.web.dto.request.LineUpdateRequest;
import nextstep.subway.web.dto.response.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LineService {

    private final StationService stationService;
    private final LineRepository lineRepository;


    @Transactional
    public LineResponse saveLine(LineCreateRequest lineCreateRequest) {
        Station upStation = stationService.getStationById(lineCreateRequest.getUpStationId());
        Station downStation = stationService.getStationById(lineCreateRequest.getDownStationId());
        Line line = lineRepository.save(Line.of(
            lineCreateRequest.getName(),
            lineCreateRequest.getColor()
        ));
        line.addSection(Section.of(line, upStation, downStation, lineCreateRequest.getDistance()));
        return new LineResponse(line);
    }

    public List<LineResponse> findAllLines() {
        return getLines().stream()
            .map(LineResponse::new)
            .collect(Collectors.toList());
    }


    public LineResponse findLine(Long id) {
        Line line = getLineById(id);
        return new LineResponse(line);
    }

    @Transactional
    public void updateLine(Long id, LineUpdateRequest lineUpdateRequest) {
        Line line = getLineById(id);
        line.updateName(lineUpdateRequest.getName());
        line.updateColor(lineUpdateRequest.getColor());
    }

    @Transactional
    public void removeLine(Long id) {
        Line line = getLineById(id);
        lineRepository.delete(line);
    }

    @Transactional
    public LineResponse addSection(Long lineId, AddSectionRequest addSectionRequest) {
        Line line = getLineById(lineId);
        Station upStation = stationService.getStationById(addSectionRequest.getUpStationId());
        Station downStation = stationService.getStationById(addSectionRequest.getDownStationId());
        line.addSection(
            Section.of(line, upStation, downStation, addSectionRequest.getDistance())
        );
        return new LineResponse(line);
    }

    @Transactional
    public LineResponse removeSection(Long lineId, Long downStationId) {
        Line line = getLineById(lineId);
        Station station = stationService.getStationById(downStationId);
        line.removeSection(station);
        return new LineResponse(line);
    }


    // TODO: custom exception & exception handler
    public Line getLineById(Long id) {
        return lineRepository.findById(id).orElseThrow(() -> new RuntimeException("not found line"));
    }

    public List<Line> getLines() {
        return lineRepository.findAll();
    }
}
