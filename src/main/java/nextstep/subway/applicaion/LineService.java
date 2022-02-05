package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.common.exception.DuplicateAttributeException;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;

    private final StationRepository stationRepository;

    public LineService(
            final LineRepository lineRepository,
            final StationRepository stationRepository
    ) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse createLine(LineRequest request) {
        var requestName = request.getName();
        var requestColor = request.getColor();
        var requestUpStationId = request.getUpStationId();
        var requestDownStationId = request.getDownStationId();
        var distance = request.getDistance();

        if (isLineNamePresent(requestName)) {
            throw new DuplicateAttributeException("이미 존재하는 노선 명: " + requestName);
        }

        var upStation = stationRepository.findById(requestUpStationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지하철 역 id: " + requestUpStationId));
        var downStation = stationRepository.findById(requestDownStationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지하철 역 id: " + requestDownStationId));

        var savedLine = lineRepository.save(new Line(requestName, requestColor, upStation, downStation, distance));

        return LineResponse.from(savedLine);
    }

    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public LineResponse findLineById(Long id) {
        var line = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지하철 노선 id: " + id));

        return LineResponse.from(line);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        var lines = lineRepository.findAll();

        return lines.stream()
                .map(LineResponse::from)
                .collect(Collectors.toList());
    }

    public LineResponse updateLine(Long id, LineRequest lineRequest) {
        var line = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지하철 노선 id: " + id));
        line.update(lineRequest.getName(), lineRequest.getColor());

        return LineResponse.from(line);
    }

    public void addSection(Long lineId, SectionRequest sectionRequest) {
        var line = lineRepository.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지하철 노선 id: " + lineId));

        var upStationId = sectionRequest.getUpStationId();
        var downStationId = sectionRequest.getDownStationId();
        var distance = sectionRequest.getDistance();

        var upStation = stationRepository.findById(upStationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지하철 역 id: " + upStationId));
        var downStation = stationRepository.findById(downStationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지하철 역 id: " + downStationId));

        line.addSection(upStation, downStation, distance);
    }

    public void deleteSection(Long lineId, Long stationId) {
        var line = lineRepository.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지하철 노선 id: " + lineId));

        var station = stationRepository.findById(stationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지하철 역 id: " + stationId));

        line.removeSection(station);
    }

    private boolean isLineNamePresent(String lineName) {
        return lineRepository.findByName(lineName)
                .isPresent();
    }
}
