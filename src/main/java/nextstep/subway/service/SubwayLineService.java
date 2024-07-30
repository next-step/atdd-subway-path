package nextstep.subway.service;

import lombok.RequiredArgsConstructor;
import nextstep.subway.domain.SectionBuilder;
import nextstep.subway.domain.SubwayLineBuilder;
import org.springframework.stereotype.Service;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.SubwayLine;
import nextstep.subway.dto.SubwayLineRequest;
import nextstep.subway.dto.SubwayLineResponse;
import nextstep.subway.dto.SubwayLineUpdateRequest;
import nextstep.subway.repository.SubwayLineRepository;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubwayLineService {
    private final SubwayLineRepository subwayLineRepository;
    private final StationService stationService;

    @Transactional
    public SubwayLineResponse saveSubwayLine(SubwayLineRequest request) {
        var upStation = stationService.findStationOrElseThrow(request.getUpStationId());
        var downStation = stationService.findStationOrElseThrow(request.getDownStationId());
        var section = new SectionBuilder().
                distance(request.getDistance())
                .upStation(upStation)
                .downStation(downStation)
                .build();
        var subwayLine = new SubwayLineBuilder()
                .name(request.getName())
                .color(request.getColor())
                .section(section)
                .build();
        var saved = subwayLineRepository.save(subwayLine);
        return SubwayLineResponse.from(saved);
    }

    public List<SubwayLineResponse> findAllSubwayLines() {
        return subwayLineRepository.findAll().stream()
                .map(SubwayLineResponse::from)
                .collect(Collectors.toList());
    }

    public SubwayLineResponse findSubwayLine(Long id) {
        var subwayLine = findSubwayLineOrElseThrow(id);
        return SubwayLineResponse.from(subwayLine);
    }

    @Transactional
    public void updateSubwayLine(Long id, SubwayLineUpdateRequest request) {
        var subwayLine = findSubwayLineOrElseThrow(id);
        subwayLine.updateBasicInfo(request.getName(), request.getColor());
    }

    @Transactional
    public void deleteSubwayLine(Long id) {
        findSubwayLineOrElseThrow(id);
        subwayLineRepository.deleteById(id);
    }

    public SubwayLine findSubwayLineOrElseThrow(Long id) {
        return subwayLineRepository
                .findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }
}
