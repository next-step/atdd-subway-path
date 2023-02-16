package nextstep.subway.applicaion;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;
    private final StationService stationService;
    private final LineMapper lineMapper;

    public LineService(LineRepository lineRepository, StationService stationService, final LineMapper lineMapper) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
        this.lineMapper = lineMapper;
    }

    @CacheEvict(value = "graph", allEntries = true)
    @Transactional
    public LineResponse saveLine(LineRequest request) {
        Line line = lineRepository.save(new Line(request.getName(), request.getColor()));
        if (request.isCreateSection()) {
            Station upStation = stationService.findById(request.getUpStationId());
            Station downStation = stationService.findById(request.getDownStationId());
            line.addSection(new Section(line, upStation, downStation, request.getDistance()));
        }
        return lineMapper.toResponseFrom(line);
    }

    public List<LineResponse> showLines() {
        return lineRepository.findAll().stream()
                .map(lineMapper::toResponseFrom)
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        return lineMapper.toResponseFrom(findLineById(id));
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = findLineById(id);
        line.update(lineRequest.getName(), lineRequest.getColor());
    }

    @CacheEvict(value = "graph", allEntries = true)
    @Transactional
    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    @CacheEvict(value = "graph", allEntries = true)
    @Transactional
    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Station upStation = stationService.findById(sectionRequest.getUpStationId());
        Station downStation = stationService.findById(sectionRequest.getDownStationId());
        Line line = findLineById(lineId);

        line.addSection(new Section(line, upStation, downStation, sectionRequest.getDistance()));
    }

    @CacheEvict(value = "graph", allEntries = true)
    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Line line = findLineById(lineId);
        Station station = stationService.findById(stationId);

        line.removeSection(station);
    }

    public Line findLineById(final long lineId) {
        return lineRepository.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 노선입니다."));
    }
}
