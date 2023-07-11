package nextstep.subway.line.service;

import java.util.ArrayList;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Sections;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.line.view.LineCreateRequest;
import nextstep.subway.line.view.LineModifyRequest;
import nextstep.subway.line.view.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.service.StationService;


@Service
@RequiredArgsConstructor
@Transactional
public class LineManageService {
    private final StationService stationService;
    private final LineRepository lineRepository;
    private final LineReadService lineReadService;

    public LineResponse createLine(LineCreateRequest request) {
        Station upStation = stationService.get(request.getUpStationId());
        Station downStation = stationService.get(request.getDownStationId());

        Line line = mapRequestToEntity(request, upStation, downStation);

        Line createdLine = lineRepository.save(line);

        return LineResponse.from(createdLine);
    }

    private Line mapRequestToEntity(LineCreateRequest request, Station upStation, Station downStation) {
        Section section = Section.builder()
                                 .downStation(downStation)
                                 .upStation(upStation)
                                 .distance(request.getDistance())
                                 .build();

        Line line = Line.builder()
                        .name(request.getName())
                        .color(request.getColor())
                        .upStation(upStation)
                        .downStation(downStation)
                        .sections(new Sections(new ArrayList<>()))
                        .build();

        line.addSection(section);

        return line;
    }

    public void modifyLine(Long id, LineModifyRequest request) {
        Line line = lineReadService.getLine(id);

        line.changeNameAndColor(request.getName(), request.getColor());

        lineRepository.save(line);
    }

    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    public void delete(Long lineId, Long stationId) {
        Line line = lineReadService.getLine(lineId);

        line.deleteSection(stationId);
    }

}
