package nextstep.subway.line;

import lombok.RequiredArgsConstructor;
import nextstep.subway.section.Section;
import nextstep.subway.section.SectionAddRequest;
import nextstep.subway.section.SectionResponse;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    @Transactional
    public LineResponse create(LineCreateRequest request) {
        Station upstation = stationRepository.findById(request.getUpstationId()).orElseThrow(EntityNotFoundException::new);
        Station downstation = stationRepository.findById(request.getDownstationId()).orElseThrow(EntityNotFoundException::new);

        Line line = LineCreateRequest.toEntity(request);

        lineRepository.save(line);
        line.initSection(upstation, downstation);

        return LineResponse.from(line);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findAll() {
        return lineRepository.findAll().stream()
                .map(LineResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findById(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        return LineResponse.from(line);
    }

    @Transactional
    public LineResponse update(Long id, LineUpdateRequest request) {
        Line line = lineRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        line.updateName(request.getName());
        line.updateColor(request.getColor());

        return LineResponse.from(line);
    }

    public void delete(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public SectionResponse addSection(Long lineId, SectionAddRequest request) {
        Line line = lineRepository.findById(lineId).orElseThrow(EntityNotFoundException::new);

        Station upstation = stationRepository.findById(request.getUpstationId()).orElseThrow(EntityNotFoundException::new);
        Station downstation = stationRepository.findById(request.getDownstationId()).orElseThrow(EntityNotFoundException::new);

        Section newSection = Section.builder()
                .line(line)
                .upstation(upstation)
                .downstation(downstation)
                .distance(request.getDistance())
                .build();

        line.addSection(newSection);

        return SectionResponse.from(newSection);
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId).orElseThrow(EntityNotFoundException::new);
        Station deleteStation = stationRepository.findById(stationId).orElseThrow(EntityNotFoundException::new);

        line.popSection(deleteStation);
    }
}
