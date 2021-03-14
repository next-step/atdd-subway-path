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

    @Transactional
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
        final Section newSection = new Section(line, upStation, downStation, distance);
        if (line.getStations().size() == 0) {
            line.getSections().add(newSection);
            return;
        }

        validateStations(line, upStation, downStation);
        if (line.matchSectionWithUpStation(upStation)){
            addUpfrontSection(line, line.findSectionByUpStation(upStation), newSection);
            return;
        }

        if (line.matchSectionByDownStation(downStation)){
            addDownBehindSection(line, line.findSectionByDownStation(downStation), newSection);
            return;
        }
        line.getSections().add(newSection);
    }

    private void addUpfrontSection(Line line, Section oldSection, Section newSection) {
        line.removeSection(oldSection);
        line.addSection(newSection);
        line.addSection(new Section(line, newSection.getDownStation(), oldSection.getDownStation(), oldSection.getDistance() - newSection.getDistance()));
    }

    private void addDownBehindSection(Line line, Section oldSection, Section newSection) {
        line.removeSection(oldSection);
        line.addSection(newSection);
        line.addSection(new Section(line, oldSection.getUpStation(), newSection.getUpStation(), oldSection.getDistance() - newSection.getDistance()));
    }

    public void validateStations(Line line, Station upStation, Station downStation) {
        final boolean existUpStation = line.isInStation(upStation);
        final boolean existDownStation = line.isInStation(downStation);
        if (existUpStation && existDownStation) {
            throw new RuntimeException("이미 두 역은 등록되어 있습니다.");
        }
        if (!existUpStation && !existDownStation) {
            throw new RuntimeException("이미 두 역 중 한 역은 등록되어 있어야 합니다.");
        }
    }

    public void removeSection(Line line, Long stationId) {
        final Station station = stationService.findStationById(stationId);
        line.removeSection(station);
    }

    public LineResponse createLineResponse(Line line) {
        List<StationResponse> stations = line.getStations().stream()
                .map(it -> StationResponse.of(it))
                .collect(Collectors.toList());
        return new LineResponse(line.getId(), line.getName(), line.getColor(), stations, line.getCreatedDate(), line.getModifiedDate());
    }
}
