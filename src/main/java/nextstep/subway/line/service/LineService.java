package nextstep.subway.line.service;

import lombok.RequiredArgsConstructor;
import nextstep.subway.line.adapters.persistence.LineJpaAdapter;
import nextstep.subway.line.dto.request.SaveLineRequestDto;
import nextstep.subway.line.dto.request.SaveLineSectionRequestDto;
import nextstep.subway.line.dto.request.UpdateLineRequestDto;
import nextstep.subway.line.dto.response.LineResponseDto;
import nextstep.subway.line.entity.Line;
import nextstep.subway.section.entity.Section;
import nextstep.subway.station.adapters.persistence.StationJpaAdapter;
import nextstep.subway.station.entity.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LineService {

    private final StationJpaAdapter stationJpaAdapter;

    private final LineJpaAdapter lineJpaAdapter;

    @Transactional
    public LineResponseDto saveLine(SaveLineRequestDto lineRequest) {
        Station upStation = stationJpaAdapter.findById(lineRequest.getUpStationId());
        Station downStation = stationJpaAdapter.findById(lineRequest.getDownStationId());

        Line line = lineJpaAdapter.save(lineRequest.toEntity(upStation, downStation));
        return LineResponseDto.of(line);
    }

    public List<LineResponseDto> findAllLines() {
        return lineJpaAdapter.findAll()
                .stream()
                .map(LineResponseDto::of)
                .collect(Collectors.toList());
    }

    public LineResponseDto findLineById(Long id) {
        return LineResponseDto.of(lineJpaAdapter.findById(id));
    }

    @Transactional
    public void updateLine(Long id, UpdateLineRequestDto lineRequest) {
        Line targetLine = lineJpaAdapter.findById(id);
        targetLine.updateLine(lineRequest.toEntity());
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineJpaAdapter.deleteById(id);
    }

    @Transactional
    public LineResponseDto saveLineSection(Long lineId, SaveLineSectionRequestDto lineSectionRequest) {
        Station upStation = stationJpaAdapter.findById(lineSectionRequest.getUpStationId());
        Station downStation = stationJpaAdapter.findById(lineSectionRequest.getDownStationId());

        Line targetLine = lineJpaAdapter.findById(lineId);
        Section section = Section.builder()
                .upStation(upStation)
                .downStation(downStation)
                .distance(lineSectionRequest.getDistance())
                .build();
        targetLine.addSection(section);

        return LineResponseDto.of(targetLine);
    }

    @Transactional
    public void deleteLineSectionByStationId(Long lineId, Long stationId) {
        Line targetLine = lineJpaAdapter.findById(lineId);
        targetLine.deleteSectionByStationId(stationId);
    }
}
