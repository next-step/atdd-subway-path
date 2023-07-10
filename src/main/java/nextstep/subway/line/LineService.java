package nextstep.subway.line;

import nextstep.subway.station.Station;
import nextstep.subway.station.StationNotFoundException;
import nextstep.subway.station.StationRepository;
import nextstep.subway.station.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final LineStationRepository lineStationRepository;

    public LineService(LineRepository lineRepository, final StationRepository stationRepository, LineStationRepository lineStationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.lineStationRepository = lineStationRepository;
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAllLines() {
        return lineRepository.findAll()
                .stream()
                .map(this::createLineResponse)
                .collect(Collectors.toList());
    }

    private LineResponse createLineResponse(Line line) {
      return new LineResponse(
          line.getId(),
          line.getName(),
          line.getColor(),
          mapToStations(line)
      );
    }

  private static List<StationResponse> mapToStations(final Line line) {
      return line.getStations()
              .stream()
              .map(StationResponse::new)
              .collect(Collectors.toList());
  }

  public LineResponse saveLine(LineRequest lineRequest) {
      final Line line = lineRepository.save(new Line(lineRequest.getName(),
          lineRequest.getColor(),
          lineRequest.getDistance()));

      if (lineRequest.getUpStationId() != null && lineRequest.getDownStationId() != null) {
          final Station upStation = getStationById(lineRequest.getUpStationId());
          final Station downStation = getStationById(lineRequest.getDownStationId());
          lineStationRepository.save(line.addSection(upStation, lineRequest.getDistance()));
          lineStationRepository.save(line.addSection(downStation, lineRequest.getDistance()));
      }

      return createLineResponse(line);
    }

    @Transactional(readOnly = true)
    public LineResponse findLineById(Long id) {
        return createLineResponse(lineRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new));
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        line.update(lineRequest.getName(), lineRequest.getColor());
    }

    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    private Station getStationById(final Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new StationNotFoundException(stationId));
    }
}
