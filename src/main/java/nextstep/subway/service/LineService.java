package nextstep.subway.service;

import nextstep.subway.controller.dto.StationResponse;
import nextstep.subway.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import nextstep.subway.controller.dto.LineCreateRequest;
import nextstep.subway.controller.dto.LineResponse;
import nextstep.subway.controller.dto.LineUpdateRequest;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.SectionRepository;
import nextstep.subway.repository.StationRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository, SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    @Transactional
    public LineResponse saveLine(LineCreateRequest request) {
        Line line = lineRepository.save(new Line(
                request.getName(),
                request.getColor()
        ));
        Stations stations = new Stations(stationRepository.findByIdIn(request.stationIds()));

        Station upStation = stations.findStationBy(request.getUpStationId());
        Station downStation = stations.findStationBy(request.getDownStationId());

        sectionRepository.save(new Section(
                line,
                upStation,
                downStation,
                request.getDistance()
        ));
        return LineResponse.ofWithStations(line, List.of(upStation, downStation));
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findLines() {
        List<Line> lines = lineRepository.findAll();
        Map<Line, List<Section>> sections = groupSectionsByLine(lines);
        return convertToLineResponses(sections);
    }

    private Map<Line, List<Section>> groupSectionsByLine(List<Line> lines) {
        return sectionRepository.findAllByLineIn(lines).stream()
                .collect(Collectors.groupingBy(Section::line));
    }

    private List<LineResponse> convertToLineResponses(Map<Line, List<Section>> sections) {
        return sections.entrySet().stream()
                .map(entry -> {
                    List<Station> stations = new Sections(entry.getValue()).sortedStations();
                    return LineResponse.ofWithSections(entry.getKey(), StationResponse.listOf(stations));
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse findLine(Long id) {
        Line line = lineRepository.getBy((id));
        List<Section> sections = sectionRepository.findByLine(line);
        List<Station> sortedStations = new Sections(sections).sortedStations();
        return LineResponse.ofWithSections(line, StationResponse.listOf(sortedStations));
    }

    @Transactional
    public void updateLine(Long id, LineUpdateRequest request) {
        Line line = lineRepository.getBy((id));
        line.update(request.getName(), request.getColor());
    }

    @Transactional
    public void deleteLine(Long id) {
        sectionRepository.deleteByLine(new Line(id));
        lineRepository.deleteById(id);
    }
}
