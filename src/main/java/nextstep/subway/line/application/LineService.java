package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineCreateRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineUpdateRequest;
import nextstep.subway.line.exception.LineNotFoundException;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.exception.StationNotFoundException;

@Transactional(readOnly = true)
@Service
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineCreateRequest lineCreateRequest) {
        Station upStation = getStation(lineCreateRequest.getUpStationId());
        Station downStation = getStation(lineCreateRequest.getDownStationId());

        Section section = new Section(upStation, downStation, lineCreateRequest.getDistance());
        Line line = new Line(lineCreateRequest.getName(), lineCreateRequest.getColor(), section);
        lineRepository.save(line);

        return LineResponse.of(line);
    }

    private Station getStation(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(StationNotFoundException::new);
    }

    public List<LineResponse> findAllStation() {
        return lineRepository.findAll().stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLine(Long id) {
        Line line = getLine(id);
        return LineResponse.of(line);
    }

    private Line getLine(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(LineNotFoundException::new);
    }

    @Transactional
    public void updateLine(Long id, LineUpdateRequest lineUpdateRequest) {
        Line line = getLine(id);
        line.update(lineUpdateRequest.getName(), lineUpdateRequest.getColor());
    }

    @Transactional
    public void deleteLine(Long id) {
        Line line = getLine(id);
        lineRepository.delete(line);
    }

    @Transactional
    public SectionResponse registerSection(Long lineId, SectionRequest sectionRequest) {
        Station upStation = getStation(sectionRequest.getUpStationId());
        Station downStation = getStation(sectionRequest.getDownStationId());

        Section section = new Section(upStation, downStation, sectionRequest.getDistance());

        Line line = getLine(lineId);
        line.registerSection(section);

        return SectionResponse.of(section);
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Line line = getLine(lineId);
        Station station = getStation(stationId);

        line.deleteSection(station);
    }
}
