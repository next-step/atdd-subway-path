package nextstep.subway.web.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import nextstep.subway.domians.domain.Line;
import nextstep.subway.domians.domain.Section;
import nextstep.subway.domians.domain.Station;
import nextstep.subway.domians.repository.LineRepository;
import nextstep.subway.domians.repository.StationRepository;
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

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;


    @Transactional
    public LineResponse saveLine(LineCreateRequest lineCreateRequest) {
        Station upStation = getStationById(lineCreateRequest.getUpStationId());
        Station downStation = getStationById(lineCreateRequest.getDownStationId());
        Line line = lineRepository.save(Line.of(
            lineCreateRequest.getName(),
            lineCreateRequest.getColor()
        ));
        line.addSection(Section.of(line, upStation, downStation, lineCreateRequest.getDistance()));
        return new LineResponse(line);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
            .map(LineResponse::new)
            .collect(Collectors.toList());
    }

    public LineResponse findLine(Long id) {
        Line line = getLine(id);
        return new LineResponse(line);
    }

    @Transactional
    public void updateLine(Long id, LineUpdateRequest lineUpdateRequest) {
        Line line = getLine(id);
        line.updateName(lineUpdateRequest.getName());
        line.updateColor(lineUpdateRequest.getColor());
    }

    @Transactional
    public void removeLine(Long id) {
        Line line = getLine(id);
        lineRepository.delete(line);
    }

    @Transactional
    public LineResponse addSection(Long lineId, AddSectionRequest addSectionRequest) {
        Line line = getLine(lineId);
        Station upStation = getStationById(addSectionRequest.getUpStationId());
        Station downStation = getStationById(addSectionRequest.getDownStationId());
        line.addSection(
            Section.of(line, upStation, downStation, addSectionRequest.getDistance())
        );
        return new LineResponse(line);
    }

    @Transactional
    public LineResponse removeSection(Long lineId, Long downStationId) {
        Line line = getLine(lineId);
        line.removeSection(downStationId);
        return new LineResponse(line);
    }


    // TODO: custom exception & exception handler
    private Line getLine(Long id) {
        return lineRepository.findById(id).orElseThrow(() -> new RuntimeException("not found line"));
    }

    private Station getStationById(Long stationId) {
        return stationRepository.findById(stationId).orElseThrow(() -> new RuntimeException("not found station"));
    }


}
