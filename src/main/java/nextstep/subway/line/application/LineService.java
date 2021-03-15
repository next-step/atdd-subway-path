package nextstep.subway.line.application;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.common.exception.NoResourceException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineService {

  private LineRepository lineRepository;
  private StationService stationService;

  public LineService(LineRepository lineRepository, StationService stationService) {

    this.lineRepository = lineRepository;
    this.stationService = stationService;
  }

  public LineResponse saveLine(LineRequest request) {
    Station upStation = stationService.findStation(request.getUpStationId());
    Station downStation = stationService.findStation(request.getDownStationId());
    Line persistLine = lineRepository.save(request.toLine(upStation, downStation));
    return LineResponse.of(persistLine, toStationResponse(persistLine.getSections()));
  }

  public List<LineResponse> getLines() {
    List<Line> lineList = lineRepository.findAll();
    return lineList.stream()
        .map(line -> LineResponse.of(line, toStationResponse(line.getSections())))
        .collect(Collectors.toList());
  }

  public LineResponse findLine(long id) {
    Line line = findLineById(id);
    return LineResponse.of(line, toStationResponse(line.getSections()));
  }

  public LineResponse modifyLine(long id, LineRequest lineRequest) {
    Line line = findLineById(id);
    line.update(lineRequest.getName(), lineRequest.getColor());
    return LineResponse.of(line, toStationResponse(line.getSections()));
  }

  public void removeLine(long id) {
    lineRepository.deleteById(id);
  }

  public void addSection(long lineId, SectionRequest sectionRequest) {
    Station upStation = stationService.findStation(sectionRequest.getUpStationId());
    Station downStation = stationService.findStation(sectionRequest.getDownStationId());
    lineRepository.findById(lineId)
        .orElseThrow(() -> new NoResourceException("노선을 찾을수 없습니다."))
        .addSection(upStation, downStation, sectionRequest.getDistance());
  }

  public void removeSection(long lineId, long stationId) {
    Station station = stationService.findStation(stationId);
    findLineById(lineId).removeSection(station);
  }

  private Line findLineById(long lindId) {
    return lineRepository.findById(lindId)
        .orElseThrow(() -> new NoResourceException("노선을 찾을수 없습니다."));
  }

  private List<StationResponse> toStationResponse(Sections sections) {
    return sections.getSortedStations().stream()
        .map(StationResponse::of)
        .collect(Collectors.toList());
  }
}
