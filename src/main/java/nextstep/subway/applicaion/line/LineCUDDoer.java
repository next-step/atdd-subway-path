package nextstep.subway.applicaion.line;

import nextstep.subway.applicaion.dto.line.LineRequest;
import nextstep.subway.applicaion.dto.line.LineResponse;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.LineRepository;
import nextstep.subway.domain.section.Distance;
import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LineCUDDoer extends LineService{
    public LineCUDDoer(LineRepository lineRepository, StationRepository stationRepository) {
        super(lineRepository, stationRepository);
    }

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        Line line = lineRepository.save(new Line(request.getName(), request.getColor()));

        if (request.getUpStationId() != null && request.getDownStationId() != null && request.getDistance() != 0) {
            Station upStation = stationRepository.findById(request.getUpStationId()).orElseThrow(IllegalArgumentException::new);
            Station downStation = stationRepository.findById(request.getDownStationId()).orElseThrow(IllegalArgumentException::new);

            line.addSection(Section.builder()
                    .line(line)
                    .upStation(upStation)
                    .downStation(downStation)
                    .distance(new Distance(request.getDistance()))
                    .build());
        }
        return LineResponse.of(line);
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id).orElseThrow(IllegalArgumentException::new);

        line.change(lineRequest.getName(), lineRequest.getColor());
    }

    @Transactional
    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }
}
