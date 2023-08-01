package nextstep.subway.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.StationRepository;


@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository,
                       StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse findLineById(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(NoSuchElementException::new);
        return createLineResponse(line);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
                .map(this::createLineResponse)
                .collect(Collectors.toList());
    }
    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = stationRepository.findById(lineRequest.getUpStationId()).orElseThrow(NoSuchElementException::new);
        Station downStation = stationRepository.findById(lineRequest.getDownStationId()).orElseThrow(NoSuchElementException::new);
        Line line = lineRequest.toEntity(upStation, downStation);
        lineRepository.save(line);
        return createLineResponse(line);
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    private LineResponse createLineResponse(Line line) {
        return LineResponse.from(line);
    }

    @Transactional
    public void updateStationById(Long id, LineUpdateRequest lineUpdateRequest) {
        Line line = lineRepository.findById(id).orElseThrow(NoSuchElementException::new);
        line.updateNameAndColor(lineUpdateRequest.getName(), lineUpdateRequest.getColor());
    }

    @Transactional
    public LineResponse addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = lineRepository.findById(lineId).orElseThrow(() -> new NoSuchElementException("해당 line 값이 없습니다."));
        Station upStation = stationRepository.findById(sectionRequest.getUpStationId()).orElseThrow(() -> new NoSuchElementException("해당 station 값이 없습니다."));
        Station downStation = stationRepository.findById(sectionRequest.getDownStationId()).orElseThrow(() -> new NoSuchElementException("해당 station 값이 없습니다."));
        Long distance = sectionRequest.getDistance();

        Section section = Section.of(line, distance, upStation, downStation);
        line.addSection(section);

        return createLineResponse(line);
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId).orElseThrow(NoSuchElementException::new);
        Station downStation = stationRepository.findById(stationId).orElseThrow(NoSuchElementException::new);
        line.deleteSection(downStation);
    }
}
