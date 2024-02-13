package nextstep.subway.line;

import nextstep.exception.BadRequestException;
import nextstep.subway.section.Section;
import nextstep.subway.section.SectionRequest;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
    public LineResponse saveLine(LineRequest lineRequest) {
        Line line = lineRequest.createLine();
        line = lineRepository.save(line);

        return new LineResponse(line);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    public LineResponse findLine(Long id) {
        Line line = lineRepository.findById(id).orElseThrow();
        return new LineResponse(line);
    }

    @Transactional
    public void modifyLine(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id).orElseThrow();
        line.changeLineInfo(lineRequest.getName(), lineRequest.getColor());
    }

    @Transactional
    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = lineRepository.findById(lineId).orElseThrow();
        Station upStation = stationRepository.findById(sectionRequest.getUpStationId()).orElseThrow(
                () -> new BadRequestException("존재하지 않는 역입니다.")
        );
        Station downStation = stationRepository.findById(sectionRequest.getDownStationId()).orElseThrow(
                () -> new BadRequestException("존재하지 않는 역입니다.")
        );

        Section newSection = new Section(upStation, downStation, sectionRequest.getDistance());

        line.addSection(newSection);
    }

    @Transactional
    public void deleteSection(Long lineId, Long deleteStationId) {
        Line line = lineRepository.findById(lineId).orElseThrow();
        Station station = stationRepository.findById(deleteStationId).orElseThrow(
                () -> new BadRequestException("존재하지 않는 역 입니다.")
        );

        line.deleteSection(station);
    }
}
