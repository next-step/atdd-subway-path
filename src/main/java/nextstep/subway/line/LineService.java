package nextstep.subway.line;


import nextstep.subway.Exception.ErrorCode;
import nextstep.subway.Exception.SubwayException;
import nextstep.subway.line.section.Section;
import nextstep.subway.line.section.SectionRequest;
import nextstep.subway.line.section.SectionResponse;
import nextstep.subway.path.PathFinder;
import nextstep.subway.path.PathResponse;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
import nextstep.subway.station.StationResponse;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    private StationResponse createStationResponse(Station station) {
        return new StationResponse(station.getId(), station.getName());
    }

    private LineResponse createLineResponse(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), line.getSections().allStations());
    }

    public LineResponse createLine(LineRequest lineRequest) {
        Station upStation = stationRepository.findById(lineRequest.getUpStationId()).orElseThrow(() -> new SubwayException(ErrorCode.STATION_NOT_FOUND, ""));
        Station downStation = stationRepository.findById(lineRequest.getDownStationId()).orElseThrow(() -> new SubwayException(ErrorCode.STATION_NOT_FOUND, ""));

        Line line = new Line(lineRequest.getName(), lineRequest.getColor(), upStation, downStation, lineRequest.getDistance());

        lineRepository.save(line);
        return createLineResponse(line);
    }

    public List<LineResponse> showLines() {
        return lineRepository.findAll()
                .stream()
                .map(this::createLineResponse)
                .collect(Collectors.toList());
    }

    public LineResponse showLine(Long id) {
        return createLineResponse(lineRepository.findById(id).orElseThrow(() -> new SubwayException(ErrorCode.LINE_NOT_FOUND, "")));
    }

    public void updateLine(Long id, UpdateLineRequest updateLineRequest) {
        Line line = lineRepository.findById(id).orElseThrow(() -> new SubwayException(ErrorCode.LINE_NOT_FOUND, ""));
        line.setName(updateLineRequest.getName());
        line.setColor(updateLineRequest.getColor());
    }

    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    private LineSectionResponse createLineSectionResponse(Line line) {
        List<SectionResponse> sectionResponses = line.getSections().get().stream()
                .map(this::createSectionResponse)
                .collect(Collectors.toList());
        return new LineSectionResponse(line.getId(), line.getName(), sectionResponses);
    }

    private SectionResponse createSectionResponse(Section section) {
        return new SectionResponse(section.getId(), createStationResponse(section.getUpStation()), createStationResponse(section.getDownStation()), section.getDistance());
    }

    public LineSectionResponse showLineSections(Long id) {
        return createLineSectionResponse(lineRepository.findById(id).orElseThrow(() -> new SubwayException(ErrorCode.LINE_NOT_FOUND, "")));
    }

    public SectionResponse addSection(Long id, SectionRequest sectionRequest) {
        Line line = lineRepository.findById(id).get();

        Station upStation = stationRepository.findById(sectionRequest.getUpStationId()).get();
        Station downStation = stationRepository.findById(sectionRequest.getDownStationId()).get();

        Section section = new Section(line, upStation, downStation, sectionRequest.getDistance());
        line.addSection(section);
        return createSectionResponse(section);
    }

    public void deleteSection(Long id, Long stationId) {
        Line line = lineRepository.findById(id).get();
        line.deleteSection(stationId);
    }

    public PathResponse getShortestPath(Long source, Long target) {
        Station sourceStation = stationRepository.findById(source).orElseThrow(() -> new SubwayException(ErrorCode.STATION_NOT_FOUND, ""));
        Station targetStation = stationRepository.findById(target).orElseThrow(() -> new SubwayException(ErrorCode.STATION_NOT_FOUND, ""));
        List<Line> lines = lineRepository.findAll();

        return new PathFinder(lines).shortestPath(sourceStation, targetStation);
    }
}
