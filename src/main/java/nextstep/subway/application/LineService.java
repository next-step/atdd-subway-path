package nextstep.subway.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineCreateRequest;
import nextstep.subway.dto.LineUpdateRequest;
import nextstep.subway.dto.SectionAddRequest;
import nextstep.subway.exception.LineDuplicationNameException;
import nextstep.subway.exception.LineNotFoundException;
import nextstep.subway.exception.StationNotFoundException;

@Service
@Transactional(readOnly = true)
public class LineService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public Line save(LineCreateRequest lineCreateRequest) {
        validateDuplicationLineName(lineCreateRequest.getName());
        Line line = lineRepository.save(lineCreateRequest.toLine());
        addSection(line, lineCreateRequest.getUpStationId(), lineCreateRequest.getDownStationId(),
            lineCreateRequest.getDistance());
        return line;
    }

    private void validateDuplicationLineName(String name) {
        if (lineRepository.existsByName(name)) {
            throw new LineDuplicationNameException();
        }
    }

    private void addSection(Line line, Long upStationId, Long DownStationId, Integer distance) {
        Station upStation = getStation(upStationId);
        Station downStation = getStation(DownStationId);
        line.addSection(new Section(line, upStation, downStation, distance));
    }

    private Station getStation(Long stationId) {
        return stationRepository.findById(stationId)
            .orElseThrow(StationNotFoundException::new);
    }

    public List<Line> findAll() {
        return lineRepository.findAll();
    }

    public Line findById(Long id) {
        return lineRepository.findById(id)
            .orElseThrow(LineNotFoundException::new);
    }

    @Transactional
    public void update(Long id, LineUpdateRequest lineUpdateRequest) {
        Line line = lineRepository.findById(id)
            .orElseThrow(LineNotFoundException::new);
        line.update(lineUpdateRequest.getName(), lineUpdateRequest.getColor());
    }

    @Transactional
    public void delete(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public Line addSection(Long id, SectionAddRequest sectionAddRequest) {
        Line line = lineRepository.findById(id)
            .orElseThrow(LineNotFoundException::new);
        addSection(line, sectionAddRequest.getUpStationId(), sectionAddRequest.getDownStationId(),
            sectionAddRequest.getDistance());
        return line;
    }

    @Transactional
    public void deleteSection(Long id, Long stationId) {
        Line line = lineRepository.findById(id)
            .orElseThrow(LineNotFoundException::new);
        line.removeSection(stationId);
    }
}
