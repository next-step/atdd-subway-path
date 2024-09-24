package nextstep.subway.line.api;

import lombok.RequiredArgsConstructor;
import nextstep.subway.line.api.response.LineResponse;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.presentation.request.LineCreateRequest;
import nextstep.subway.line.presentation.request.LineUpdateRequest;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.path.domain.PathEvent;
import nextstep.subway.section.SectionRepository;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.presentation.request.SectionCreateRequest;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
import nextstep.subway.station.StationResponse;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class LineService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public LineResponse create(LineCreateRequest request) {
        eventPublisher.publishEvent(new PathEvent(this));

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

        line.getSections().addSection(section);
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
        Line line = findLineById(lineId);

        Station upStation = getStationEntity(line.getSections().getUpStationId());
        Station downStation = getStationEntity(line.getSections().getDownStationId());

        StationResponse upStationResponse = StationResponse.of(upStation);
        StationResponse downStationResponse = StationResponse.of(downStation);

        return LineResponse.of(line, upStationResponse, downStationResponse);
    }

    public void updateLine(Long lineId, LineUpdateRequest request) {
        Line line = findLineById(lineId);
        line.updateName(request.getName());
        line.updateColor(request.getColor());
    }

    public void deleteLine(Long lineId) {
        Line line = findLineById(lineId);
        lineRepository.delete(line);
    }

    public void addSection(Line line, Section section) {
        line.getSections().addSection(section);
    }


    public Line findLineById(Long lineId) {
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
