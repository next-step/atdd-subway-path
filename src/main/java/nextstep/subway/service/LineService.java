package nextstep.subway.service;


import static nextstep.subway.dto.LineResponse.createLineResponse;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.exception.NoLineException;
import nextstep.subway.exception.NoStationException;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.StationRepository;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = getStation(lineRequest.getUpStationId());
        Station downStation = getStation(lineRequest.getDownStationId());
        Line line = Line.of(lineRequest, upStation, downStation);
        lineRepository.save(line);
        return createLineResponse(line);
    }

    private Station getStation(Long stationId) {
        return stationRepository.findById(stationId)
                                .orElseThrow(() -> new NoStationException(stationId + "에 해당하는 지하철 역이 존재하지 않습니다."));
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll()
                             .stream()
                             .map(LineResponse::createLineResponse)
                             .collect(Collectors.toList());
    }

    public LineResponse getLine(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(() -> new NoLineException(id + "에 해당하는 지하철 노선이 존재하지 않습니다."));
        return LineResponse.createLineResponse(line);
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id).orElseThrow(() -> new NoLineException(id + "에 해당하는 지하철 노선이 존재하지 않습니다."));
        line.updateLine(lineRequest.getName(), lineRequest.getColor());
    }

    @Transactional
    public void deleteLine(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(() -> new NoLineException(id + "에 해당하는 지하철 노선이 존재하지 않습니다."));
        lineRepository.delete(line);
    }

    @Transactional
    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = lineRepository.findById(lineId).orElseThrow(() -> new NoLineException(lineId + "에 해당하는 지하철 노선이 존재하지 않습니다."));
        Station upStation = stationRepository.findById(sectionRequest.getUpStationId())
                                             .orElseThrow(() -> new NoStationException(sectionRequest.getUpStationId() + "에 해당하는 지하철 역이 존재하지 않습니다."));
        Station downStation = stationRepository.findById(sectionRequest.getDownStationId())
                                               .orElseThrow(() -> new NoStationException(sectionRequest.getDownStationId() + "에 해당하는 지하철 역이 존재하지 않습니다."));
        Section section = Section.of(upStation, downStation, sectionRequest.getDistance());
        line.addSection(section);
        lineRepository.save(line);
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId).orElseThrow(() -> new NoLineException(lineId + "에 해당하는 지하철 노선이 존재하지 않습니다."));
        Station station = stationRepository.findById(stationId).orElseThrow(() -> new NoStationException(stationId + "에 해당하는 지하철 역이 존재하지 않습니다."));
        line.deleteSection(station);
        lineRepository.save(line);
    }
}
