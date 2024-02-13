package nextstep.subway.line.service;

import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineUpdateRequest;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.line.entity.Line;
import nextstep.subway.line.entity.Section;
import nextstep.subway.line.entity.Sections;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.station.entity.Station;
import nextstep.subway.station.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(final LineRepository lineRepository, final StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse createSubwayLine(final LineRequest request) {
        final String lineName = request.getName();
        final String lineColor = request.getColor();
        final Station upStation = this.findStationById(request.getUpStationId());
        final Station downStation = this.findStationById(request.getDownStationId());
        final int lineDistance = request.getDistance();

        final Line newLine = new Line(lineName, lineColor, upStation, downStation, lineDistance);
        final Line savedLine = lineRepository.save(newLine);

        return LineResponse.convertToDto(savedLine);
    }

    public LineResponse getSubwayLine(final Long lindId) {
        final Line line = this.findLineById(lindId);
        return LineResponse.convertToDto(line);
    }

    public List<LineResponse> getSubwayLines() {
        return lineRepository.findAll().stream()
                .map(LineResponse::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateSubwayLine(final Long id, final LineUpdateRequest request) {
        final Line line = this.findLineById(id);

        final String name = request.getName();
        final String color = request.getColor();
        line.updateDetails(name, color);
    }

    @Transactional
    public void deleteSubwayLine(final Long lineId) {
        this.findLineById(lineId);
        lineRepository.deleteById(lineId);
    }

    private Station findStationById(final Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new EntityNotFoundException("Station not found. Station Id: " + stationId));
    }

    private Line findLineById(final Long lindId) {
        return lineRepository.findById(lindId)
                .orElseThrow(() -> new EntityNotFoundException("Line not found. Line Id: " + lindId));
    }

    @Transactional
    public LineResponse createLineSection(final Long lineId, final SectionRequest request) {
        final Line line = this.findLineById(lineId);

        final Sections sections = line.getSections();
        final Long upStationId = request.getUpStationId();
        final Long downStationId = request.getDownStationId();

        if (sections.isSectionRegistered(upStationId, downStationId)) {
            throw new IllegalArgumentException();
        }

        final Station requestUpStation = this.findStationById(upStationId);
        final Station requestDownStation = this.findStationById(downStationId);

        final Section section = new Section(line, requestUpStation, requestDownStation, request.getDistance());

        line.addSection(section);

        return LineResponse.convertToDto(line);
    }

    @Transactional
    public void deleteLineSection(final Long lineId, final Long stationId) {
        Line line = this.findLineById(lineId);

        if (line.getSections().size() == 1) {
            throw new IllegalArgumentException();
        }

        Station station = stationRepository.findById(stationId).orElseThrow(() -> new IllegalArgumentException());
        line.removeSection(station);
    }
}
