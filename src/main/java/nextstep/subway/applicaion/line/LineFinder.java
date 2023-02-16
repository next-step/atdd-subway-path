package nextstep.subway.applicaion.line;

import nextstep.subway.applicaion.dto.line.LineResponse;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.LineRepository;
import nextstep.subway.domain.station.StationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineFinder extends LineService{
    public LineFinder(LineRepository lineRepository, StationRepository stationRepository) {
        super(lineRepository, stationRepository);
    }

    public List<LineResponse> showLines() {
        return lineRepository.findAll().stream()
                .map(a -> LineResponse.of(a))
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        return LineResponse.of(line);
    }
}
