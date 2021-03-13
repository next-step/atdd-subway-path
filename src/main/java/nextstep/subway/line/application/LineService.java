package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;
    private StationService stationService;

    private final Long MAX_STATIONS_COUNT_IN_SECTION = 2L;
    private final Long MIN_STATIONS_COUNT_IN_SECTION = 0L;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse saveLine(LineRequest request) {
        Station upStation = stationService.findById(request.getUpStationId());
        Station downStation = stationService.findById(request.getDownStationId());
        Line persistLine = lineRepository.save(new Line(request.getName(), request.getColor(), upStation, downStation, request.getDistance()));
        return createLineResponse(persistLine);
    }

    public List<Line> findLines() {
        return lineRepository.findAll();
    }

    public List<LineResponse> findLineResponses() {
        List<Line> persistLines = lineRepository.findAll();
        return persistLines.stream()
                .map(line -> createLineResponse(line))
                .collect(Collectors.toList());
    }

    public Line findLineById(Long id) {
        return lineRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    public LineResponse findLineResponseById(Long id) {
        Line persistLine = findLineById(id);
        return createLineResponse(persistLine);
    }

    public void updateLine(Long id, LineRequest lineUpdateRequest) {
        Line persistLine = lineRepository.findById(id).orElseThrow(RuntimeException::new);
        persistLine.update(new Line(lineUpdateRequest.getName(), lineUpdateRequest.getColor()));
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public void addSection(Long lineId, SectionRequest request) {
        Line line = findLineById(lineId);
        Station upStation = stationService.findStationById(request.getUpStationId());
        Station downStation = stationService.findStationById(request.getDownStationId());
        addSection(line, upStation, downStation, request.getDistance());
    }

    public void removeSection(Long lineId, Long stationId) {
        Line line = findLineById(lineId);
        removeSection(line, stationId);
    }

    public void addSection(Line line, Station upStation, Station downStation, int distance) {
        if (getStations(line).size() == 0) {
            line.getSections().add(new Section(line, upStation, downStation, distance));
            return;
        }

        validateStations(line, upStation, downStation);

        registerSection(line, upStation, downStation, distance);
    }

    private void addUpfrontSectionBetweenSection(Line line, Section newSection) {
        final Section oldSection = getBelongingSectionInUpStation(line, newSection.getUpStation());
        if (oldSection.getDistance() <= newSection.getDistance()){
            throw new RuntimeException("새롭게 추가되는 구간의 길이가 잘못 되었습니다.");
        }
        line.getSections().remove(oldSection);
        line.getSections().add(newSection);
        line.getSections().add(new Section(line, newSection.getDownStation(), oldSection.getDownStation(), oldSection.getDistance() - newSection.getDistance()));
    }

    private void addDownBehindSectionBetweenSection(Line line, Section newSection) {
        final Section oldSection = getBelongingSectionInDownStation(line, newSection.getDownStation());
        if (oldSection.getDistance() <= newSection.getDistance()){
            throw new RuntimeException("새롭게 추가되는 구간의 길이가 잘못 되었습니다.");
        }
        line.getSections().remove(oldSection);
        line.getSections().add(newSection);
        line.getSections().add(new Section(line, oldSection.getDownStation(), newSection.getDownStation(), oldSection.getDistance() - newSection.getDistance()));
    }

    private void registerSection(Line line, Station upStation, Station downStation, int distance){
        final Section newSection = new Section(line, upStation, downStation, distance);
        if (isUpStationInLine(line, upStation)){
            addUpfrontSectionBetweenSection(line, newSection);
            return;
        }

        if (isDownStationInLine(line, downStation)){
            addDownBehindSectionBetweenSection(line, newSection);
            return;
        }

        line.getSections().add(newSection);
    }

    private boolean isUpStationInLine(Line line, Station station){
        return line.getSections().stream()
                .anyMatch(section -> section.getUpStation() == station);
    }

    private boolean isDownStationInLine(Line line, Station station){
        return line.getSections().stream()
                .anyMatch(section -> section.getDownStation() == station);
    }

    private Section getBelongingSectionInUpStation(Line line, Station station){
        return line.getSections().stream()
                .filter(section -> section.getUpStation() == station)
                .findFirst().orElseThrow(RuntimeException::new);
    }

    private Section getBelongingSectionInDownStation(Line line, Station station){
        return line.getSections().stream()
                .filter(section -> section.getDownStation() == station)
                .findFirst().orElseThrow(RuntimeException::new);
    }

    public void validateStations(Line line, Station upStation, Station downStation) {
        final Long matchedStationCount = countStationsAtLine(line, upStation, downStation);
        if (matchedStationCount >= MAX_STATIONS_COUNT_IN_SECTION) {
            throw new RuntimeException("이미 두 역은 등록되어 있습니다.");
        }
        if (matchedStationCount <= MIN_STATIONS_COUNT_IN_SECTION) {
            throw new RuntimeException("이미 두 역 중 한 역은 등록되어 있어야 합니다.");
        }
    }

    private Long countStationsAtLine(Line line, Station... stations){
        return getStations(line).stream()
                .filter(it -> (Arrays.stream(stations).anyMatch(station -> it == station)))
                .collect(Collectors.counting());
    }

    public void removeSection(Line line, Long stationId) {
        if (line.getSections().size() <= 1) {
            throw new RuntimeException();
        }

        boolean isNotValidUpStation = getStations(line).get(getStations(line).size() - 1).getId() != stationId;
        if (isNotValidUpStation) {
            throw new RuntimeException("하행 종점역만 삭제가 가능합니다.");
        }

        line.getSections().stream()
                .filter(it -> it.getDownStation().getId() == stationId)
                .findFirst()
                .ifPresent(it -> line.getSections().remove(it));
    }

    public List<Station> getStations(Line line) {
        if (line.getSections().isEmpty()) {
            return Arrays.asList();
        }

        List<Station> stations = new ArrayList<>();
        Station downStation = findUpStation(line);
        stations.add(downStation);

        while (downStation != null) {
            Station finalDownStation = downStation;
            Optional<Section> nextLineStation = line.getSections().stream()
                    .filter(it -> it.getUpStation() == finalDownStation)
                    .findFirst();
            if (!nextLineStation.isPresent()) {
                break;
            }
            downStation = nextLineStation.get().getDownStation();
            stations.add(downStation);
        }

        return stations;
    }

    private Station findUpStation(Line line) {
        Station downStation = line.getSections().get(0).getUpStation();
        while (downStation != null) {
            Station finalDownStation = downStation;
            Optional<Section> nextLineStation = line.getSections().stream()
                    .filter(it -> it.getDownStation() == finalDownStation)
                    .findFirst();
            if (!nextLineStation.isPresent()) {
                break;
            }
            downStation = nextLineStation.get().getUpStation();
        }

        return downStation;
    }

    public LineResponse createLineResponse(Line line) {
        List<StationResponse> stations = getStations(line).stream()
                .map(it -> StationResponse.of(it))
                .collect(Collectors.toList());
        return new LineResponse(line.getId(), line.getName(), line.getColor(), stations, line.getCreatedDate(), line.getModifiedDate());
    }
}
