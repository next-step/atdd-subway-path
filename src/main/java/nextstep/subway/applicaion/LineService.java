package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;
    private final StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    @Transactional
    public Line saveLine(LineRequest request) {
        Line line = lineRepository.save(new Line(request.getName(), request.getColor()));
        if (request.getUpStationId() != null && request.getDownStationId() != null && request.getDistance() != 0) {
            Station upStation = stationService.findById(request.getUpStationId());
            Station downStation = stationService.findById(request.getDownStationId());
            Section section = new Section.SectionBuilder(line)
                    .setDownStation(downStation)
                    .setUpStation(upStation)
                    .setDistance(request.getDistance())
                    .build();
            line.addSection(section);
        }
        return line;
    }

    public List<Line> showLines() {
        return Collections.unmodifiableList(lineRepository.findAll());
    }

    public Line findById(Long id) {
        return findLineById(id);
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = findLineById(id);

        if (lineRequest.getName() != null) {
            line.setName(lineRequest.getName());
        }
        if (lineRequest.getColor() != null) {
            line.setColor(lineRequest.getColor());
        }
    }

    private Line findLineById(Long id) {
        return lineRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Station upStation = stationService.findById(sectionRequest.getUpStationId());
        Station downStation = stationService.findById(sectionRequest.getDownStationId());
        Line line = findLineById(lineId);
        Section section = new Section.SectionBuilder(line)
                .setUpStation(upStation)
                .setDownStation(downStation)
                .setDistance(sectionRequest.getDistance())
                .build();
        line.addSection(section);
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Line line = findLineById(lineId);
        Station station = stationService.findById(stationId);
        line.remove(station);
    }

}
