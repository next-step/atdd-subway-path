package subway.line;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.section.Section;
import subway.section.SectionRepository;
import subway.section.SectionRequest;
import subway.station.Station;
import subway.station.StationRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository, SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = stationRepository.findById(lineRequest.getUpStationId())
                .orElseThrow(() -> new EntityNotFoundException("해당 역을 상행종점역으로 등록할 수 없습니다."));
        Station downStation = stationRepository.findById(lineRequest.getDownStationId())
                .orElseThrow(() -> new EntityNotFoundException("해당 역을 하행종점역으로 등록할 수 없습니다."));
        Line line = lineRepository.save(lineRequest.toEntity(upStation, downStation));
        return new LineResponse(line, List.of(upStation, downStation));
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
                .map(line -> new LineResponse(line, line.getSections().transferToStations()))
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line line = lineRepository.getReferenceById(id);
        List<Station> stations = line.getSections().transferToStations();
        return new LineResponse(line, stations);
    }

    @Transactional
    public void updateLine(Long id, UpdateLineRequest updateLineRequest) {
        Line line = lineRepository.getReferenceById(id);
        line.update(updateLineRequest.getName(), updateLineRequest.getColor());
    }

    @Transactional
    public void deleteStationById(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public Long saveSection(Long lineId, SectionRequest sectionRequest) {
        Station upStation = stationRepository.findById(sectionRequest.getUpStationId())
                .orElseThrow(() -> new EntityNotFoundException("해당 역을 상행종점역으로 등록할 수 없습니다."));
        Station downStation = stationRepository.findById(sectionRequest.getDownStationId())
                .orElseThrow(() -> new EntityNotFoundException("해당 역을 하행종점역으로 등록할 수 없습니다."));
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new EntityNotFoundException("해당 라인을 찾을 수 없습니다."));

        if (!line.getSections().isLastSection(upStation)) {
            throw new IllegalArgumentException("상행역이 하행종점역과 같지 않습니다.");
        }

        Section section = sectionRepository.save(sectionRequest.toEntity(upStation, downStation, line));
        return section.getId();
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new EntityNotFoundException("해당 라인을 찾을 수 없습니다."));
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new EntityNotFoundException("해당 역을 찾을 수 없습니다."));

        if (line.getSections().hasUnderOneSection()) {
            throw new IllegalArgumentException("노선에는 하나 이상의 구간이 존재해야 합니다.");
        }

        if (!line.getSections().isLastSection(station)) {
            throw new IllegalArgumentException("노선의 마지막 구간이 아닙니다.");
        }

        line.getSections().removeSection(line.getSections().lastSection());
    }
}
