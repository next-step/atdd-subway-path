package nextstep.subway.line.service;

import lombok.RequiredArgsConstructor;
import nextstep.subway.line.adapters.persistence.LineJpaAdapter;
import nextstep.subway.line.dto.request.SaveLineRequest;
import nextstep.subway.line.dto.request.SaveLineSectionRequest;
import nextstep.subway.line.dto.request.UpdateLineRequest;
import nextstep.subway.line.dto.response.LineResponse;
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
    public LineResponse saveLine(SaveLineRequest lineRequest) {
        Station upStation = stationJpaAdapter.findById(lineRequest.getUpStationId());
        Station downStation = stationJpaAdapter.findById(lineRequest.getDownStationId());

        Line line = lineJpaAdapter.save(lineRequest.toEntity(upStation, downStation));
        return LineResponse.of(line);
    }

    public List<LineResponse> findAllLines() {
        return lineJpaAdapter.findAll()
                .stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(Long id) {
        return LineResponse.of(lineJpaAdapter.findById(id));
    }

    @Transactional
    public void updateLine(Long id, UpdateLineRequest lineRequest) {
        Line targetLine = lineJpaAdapter.findById(id);
        targetLine.updateLine(lineRequest.toEntity());
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineJpaAdapter.deleteById(id);
    }

    @Transactional
    public LineResponse saveLineSection(Long lineId, SaveLineSectionRequest lineSectionRequest) {
        Station upStation = stationJpaAdapter.findById(lineSectionRequest.getUpStationId());
        Station downStation = stationJpaAdapter.findById(lineSectionRequest.getDownStationId());

        Line targetLine = lineJpaAdapter.findById(lineId);
        Section section = Section.builder()
                .upStation(upStation)
                .downStation(downStation)
                .distance(lineSectionRequest.getDistance())
                .build();
        targetLine.addSection(section);

        return LineResponse.of(targetLine);
    }

    @Transactional
    public void deleteLineSectionByStationId(Long lineId, Long stationId) {
        Line targetLine = lineJpaAdapter.findById(lineId);
        targetLine.deleteSectionByStationId(stationId);
    }
}
