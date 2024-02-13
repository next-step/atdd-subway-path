package nextstep.subway.line.api;

import nextstep.subway.line.api.response.LineResponse;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.presentation.request.LineCreateRequest;
import nextstep.subway.line.presentation.request.LineUpdateRequest;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.section.SectionRepository;
import nextstep.subway.section.api.SectionService;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.presentation.request.SectionCreateRequest;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
import nextstep.subway.station.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
public class LineService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final SectionService sectionService;
    private final SectionRepository sectionRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository, SectionService sectionService, SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionService = sectionService;
        this.sectionRepository = sectionRepository;
    }

    @Transactional
    public LineResponse create(LineCreateRequest request) {

        Station upStation = getStationEntity(request.getUpStationId());
        Station downStation = getStationEntity(request.getDownStationId());

        Line line = LineCreateRequest.toEntity(request);
        Line savedLine = lineRepository.save(line);

        StationResponse upStationResponse = StationResponse.of(upStation);
        StationResponse downStationResponse = StationResponse.of(downStation);

        Section section = SectionCreateRequest.toEntity(
                request.getUpStationId(),
                request.getDownStationId(),
                request.getDistance()
        );

        Section savedSection = sectionRepository.save(section);

        line.getSections().addSection(savedSection);
        return LineResponse.of(savedLine, upStationResponse, downStationResponse);
    }

    public List<LineResponse> getLines() {

        List<Line> lines = lineRepository.findAll();
        List<LineResponse> responses = new ArrayList<>();

        for (Line line : lines) {
            Station upStation = getStationEntity(line.getSections().getUpStationId());
            Station downStation = getStationEntity(line.getSections().getDownStationId());

            StationResponse upStationResponse = StationResponse.of(upStation);
            StationResponse downStationResponse = StationResponse.of(downStation);

            LineResponse response = LineResponse.of(line, upStationResponse, downStationResponse);
            responses.add(response);
        }

        return responses;
    }

    public LineResponse getLine(Long lineId) {
        Line line = getLineEntity(lineId);

        Station upStation = getStationEntity(line.getSections().getUpStationId());
        Station downStation = getStationEntity(line.getSections().getDownStationId());

        StationResponse upStationResponse = StationResponse.of(upStation);
        StationResponse downStationResponse = StationResponse.of(downStation);

        return LineResponse.of(line, upStationResponse, downStationResponse);
    }

    public void updateLine(Long lineId, LineUpdateRequest request) {
        Line line = getLineEntity(lineId);
        line.updateName(request.getName());
        line.updateColor(request.getColor());
    }

    public void deleteLine(Long lineId) {
        Line line = getLineEntity(lineId);
        lineRepository.delete(line);
    }

    private Line getLineEntity(Long lineId) {
        return lineRepository.findById(lineId).orElseThrow(
                () -> new EntityNotFoundException("해당 엔티티를 찾을 수 없습니다.")
        );
    }

    private Station getStationEntity(Long stationId) {
        return stationRepository.findById(stationId).orElseThrow(
                () -> new EntityNotFoundException("해당 엔티티를 찾을 수 없습니다.")
        );
    }
}
