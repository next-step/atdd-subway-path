package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.DuplicateCreationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
  private final LineRepository lineRepository;
  private final StationService stationService;

  public LineService(LineRepository lineRepository, StationService stationService) {
    this.lineRepository = lineRepository;
    this.stationService = stationService;
  }

  public LineResponse saveLine(LineRequest request) {
    lineRepository
      .findByName(request.getName())
      .ifPresent(
        line -> {
          throw new DuplicateCreationException();
        });
    Line line = lineRepository.save(new Line(request.getName(), request.getColor()));

    if (request.getUpStationId() != null && request.getDownStationId() != null && request.getDistance() != 0) {
      Station upStation = stationService.findById(request.getUpStationId());
      Station downStation = stationService.findById(request.getDownStationId());
      line.addSection(upStation, downStation, request.getDistance());
    }
    return createLineResponse(line);
  }

  @Transactional(readOnly = true)
  public List<Line> getLines() {
    return lineRepository.findAll();
  }

  @Transactional(readOnly = true)
  public List<Line> getLinesWithSections() {
    return lineRepository.findAllWithSections();
  }

  @Transactional(readOnly = true)
  public List<LineResponse> showLines() {
    return lineRepository.findAll().stream()
      .map(this::createLineResponse)
      .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public LineResponse findById(Long id) {
    return createLineResponse(lineRepository.findById(id).orElseThrow(IllegalArgumentException::new));
  }

  public void updateLine(Long id, LineRequest lineRequest) {
    Line line = lineRepository.findById(id).orElseThrow(IllegalArgumentException::new);

    if (lineRequest.getName() != null) {
      line.updateName(lineRequest.getName());
    }
    if (lineRequest.getColor() != null) {
      line.updateColor(lineRequest.getColor());
    }
  }

  public void deleteLine(Long id) {
    lineRepository.deleteById(id);
  }

  public void addSection(Long lineId, SectionRequest sectionRequest) {
    Station upStation = stationService.findById(sectionRequest.getUpStationId());
    Station downStation = stationService.findById(sectionRequest.getDownStationId());
    Line line = lineRepository.findById(lineId).orElseThrow(IllegalArgumentException::new);

    line.addSection(upStation, downStation, sectionRequest.getDistance());
  }

  private LineResponse createLineResponse(Line line) {
    return new LineResponse(
      line.getId(),
      line.getName(),
      line.getColor(),
      createStationResponses(line),
      line.getCreatedDate(),
      line.getModifiedDate()
    );
  }

  private List<StationResponse> createStationResponses(Line line) {
    List<Station> stations = line.getSections().getSectionStations();

    return stations.stream()
      .map(stationService::createStationResponse)
      .collect(Collectors.toList());
  }

  public void deleteSection(Long lineId, Long stationId) {
    Line line = lineRepository.findById(lineId).orElseThrow(IllegalArgumentException::new);
    Station station = stationService.findById(stationId);

    line.removeSection(station);
  }
}
