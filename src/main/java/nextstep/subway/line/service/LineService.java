package nextstep.subway.line.service;

import lombok.RequiredArgsConstructor;
import nextstep.subway.line.dto.CreateLineRequest;
import nextstep.subway.line.dto.UpdateLineRequest;
import nextstep.subway.line.repository.Line;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.section.dto.CreateSectionRequest;
import nextstep.subway.section.repository.Section;
import nextstep.subway.station.repository.Station;
import nextstep.subway.station.service.StationFindable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public
class LineService implements LineFindable, LineSavable, LineUpdatable, LineDeletable {
    private final LineRepository lineRepository;
    private final StationFindable stationFindable;

    @Transactional
    public Line saveLine(CreateLineRequest request) {
        Section initSection = createSection(request.getUpStationId(), request.getDownStationId(), request.getDistance());

        return lineRepository.save(Line.builder()
                .name(request.getName())
                .color(request.getColor())
                .initSection(initSection)
                .build());
    }

    public List<Line> findAllLines() {
        return lineRepository.findAll();
    }

    public Line findLineById(Long id) {
        return lineRepository.findById(id).orElseThrow(() -> new RuntimeException("Not Exist Line"));
    }

    @Transactional
    public void updateLineById(Long id, UpdateLineRequest request) {
        Line line = findLineById(id);
        line.changeName(request.getName());
        line.changeColor(request.getColor());
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public Line addSection(Long id, CreateSectionRequest request) {
        Line line = findLineById(id);
        System.out.println(line.getSections().size());
        line.getSections().addSection(createSection(request.getUpStationId(), request.getDownStationId(), request.getDistance()));
        System.out.println(line.getSections().size());
        return line;
    }

    @Transactional
    public void deleteSectionByStationId(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId).orElseThrow(() -> new RuntimeException("Not Exist Line"));
        Station station = stationFindable.findStationById(stationId);
        line.getSections().deleteSectionByLastStation(station);
    }

    private Section createSection(Long upStationId, Long downStationId, Long distance) {
        return Section.builder()
                .upStation(stationFindable.findStationById(upStationId))
                .downStation(stationFindable.findStationById(downStationId))
                .distance(distance)
                .build();
    }
}
