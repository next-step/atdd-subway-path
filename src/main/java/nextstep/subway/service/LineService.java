package nextstep.subway.service;

import nextstep.subway.domain.entity.Line;
import nextstep.subway.domain.entity.Section;
import nextstep.subway.domain.entity.Station;
import nextstep.subway.domain.request.LineRequest;
import nextstep.subway.domain.request.SectionRequest;
import nextstep.subway.domain.response.LineResponse;
import nextstep.subway.domain.response.SectionResponse;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.SectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final StationService stationService;
    private final SectionService sectionService;
    private final LineRepository lineRepository;
    private final SectionRepository sectionRepository;

    public LineService(LineRepository lineRepository, StationService stationService, SectionService sectionService, SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
        this.sectionService = sectionService;
        this.sectionRepository = sectionRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        Station upStation = findStationById(request.getUpStationId());
        Station downStation = findStationById(request.getDownStationId());

        Line line = lineRepository.save(new Line(request.getName(), request.getColor()));
        Section section = new Section(line, upStation, downStation, request.getDistance());
        line.addSection(section);
        sectionRepository.save(section);
        return createLineResponse(line);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
                .map(this::createLineResponse)
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(Long id) {
        Line line = lineRepository.findById(id).get();
        return createLineResponse(line);
     }

    @Transactional
    public LineResponse updateLine(Long id, LineRequest request) {
        Line line = lineRepository.findById(id).get();
        line.updateNameAndColor(request.getName(), request.getColor());
        Line updatedLine = lineRepository.save(line);
        return createLineResponse(updatedLine);
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public Station findStationById(Long id) {
        return this.stationService.findById(id);
    }

    private LineResponse createLineResponse(Line line) {
        return new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                line.getSectionList().stream().map(section -> sectionService.createSectionResponse(section)).collect(Collectors.toList()),
                line.getStations().stream().map(station -> station.createStationResponse()).collect(Collectors.toList()),
                line.getDistance()
        );
    }

    public SectionResponse findSection(Long id, Long sectionId) {
        Section section = sectionRepository.findByLineIdAndId(id, sectionId);
        return sectionService.createSectionResponse(section);
    }

    @Transactional
    public LineResponse addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = lineRepository.findById(lineId).get();
        Station upStation = stationService.findById(sectionRequest.getUpStationId());
        Station downStation = stationService.findById(sectionRequest.getDownStationId());

        // 중간에 구간 추가
        Section newSection = new Section(line, upStation, downStation, sectionRequest.getDistance());
        line.addSection(newSection);
        return createLineResponse(line);
    }
}
