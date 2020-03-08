package atdd.path.service;

import atdd.path.application.dto.EdgeResponseView;
import atdd.path.application.dto.LineListResponseView;
import atdd.path.application.dto.LineRequestView;
import atdd.path.application.dto.LineResponseView;
import atdd.path.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class LineService {
    private LineRepository lineRepository;
    private EdgeRepository edgeRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository,
                       EdgeRepository edgeRepository,
                       StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.edgeRepository = edgeRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponseView create(LineRequestView requestView) {
        if (requestView.getStartTime().isAfter(requestView.getEndTime())) {
            throw new IllegalArgumentException("노선의 시작시간은 종료시간보다 빨라야 합니다.");
        }

        if (requestView.getInterval() < 0) {
            throw new IllegalArgumentException("노선의 배차간격은 0보다 커야 합니다.");
        }
        Line line = Line.builder()
                .name(requestView.getName())
                .startTime(requestView.getStartTime())
                .endTime(requestView.getEndTime())
                .interval(requestView.getInterval())
                .build();
        Line savedLine = lineRepository.save(line);
        return LineResponseView.of(savedLine);
    }

    public void delete(Long id) {
        Optional<Line> line = lineRepository.findById(id);
        if (line.isPresent()) {
            lineRepository.deleteById(id);
        }
    }

    public LineResponseView retrieve(Long id) {
        Optional<Line> line = lineRepository.findById(id);
        line.orElseThrow(NoSuchElementException::new);
        return LineResponseView.of(line.get());
    }

    public LineListResponseView showAll() {
        List<Line> lines = lineRepository.findAll();
        return LineListResponseView.builder()
                .lines(lines)
                .build();
    }
}
