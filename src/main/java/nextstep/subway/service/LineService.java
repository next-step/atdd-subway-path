package nextstep.subway.service;

import nextstep.subway.dto.line.LineRequest;
import nextstep.subway.dto.line.LineResponse;
import nextstep.subway.dto.line.LineUpdateRequest;
import nextstep.subway.dto.section.SectionRequest;
import nextstep.subway.entity.Line;
import nextstep.subway.entity.Section;
import nextstep.subway.entity.Station;
import nextstep.subway.repository.LineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(
        LineRepository lineRepository,
        StationService stationService
    ) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    /** 지하철 노선을 생성한다. */
    @Transactional
    public LineResponse createLine(LineRequest request) {
        Station upStation = stationService.findStation(request.getUpStationId());
        Station downStation = stationService.findStation(request.getDownStationId());

        Line line = lineRepository.save(new Line(request.getName(), request.getColor()));
        Section section = new Section(line, upStation, downStation, request.getDistance());
        line.addSection(section);

        return new LineResponse(line);
    }

    /** 지하철 노선 목록을 조회한다. */
    public List<LineResponse> getLines() {
        return lineRepository.findAll().stream()
                    .map(LineResponse::new)
                    .collect(Collectors.toList());
    }

    /** 지하철 노선을 조회한다. */
    public LineResponse getLine(Long id) {
        Line line = findLine(id);
        return new LineResponse(line);
    }

    /** 지하철 노선을 수정한다. */
    @Transactional
    public void modifyLine(Long id, LineUpdateRequest request) {
        Line line = lineRepository.findById(id).orElseThrow();
        line.updateLine(request.getName(), request.getColor());
    }

    /** 지하철 노선을 삭제한다. */
    @Transactional
    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    public Line findLine(Long id) {
        return lineRepository.findById(id)
            .orElseThrow(IllegalArgumentException::new);
    }

    /** 구간을 생성한다. */
    @Transactional
    public Long createSection(Long lineId, SectionRequest request) {
        Line line = findLine(lineId);
        Station downStation = stationService.findStation(request.getDownStationId());
        Station upStation = stationService.findStation(request.getUpStationId());

        Section section = new Section(line, upStation, downStation, request.getDistance());
        line.addSection(section);

        return section.getId();
    }

    /** 구간을 삭제한다. */
    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Line line = findLine(lineId);
        Station station = stationService.findStation(stationId);

        line.removeSection(station);
    }

}
