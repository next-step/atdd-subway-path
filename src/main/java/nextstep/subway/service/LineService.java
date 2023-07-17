package nextstep.subway.service;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.repository.LineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private LineRepository lineRepository;
    private StationService stationService;

    public LineService(StationService stationService,LineRepository lineRepository) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Line line = lineRepository.save(lineRequest.toEntity());
        return LineResponse.fromEntity(line);
    }


    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
                .map(LineResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse getLineResponse(Long lineId) {
        return lineRepository.findById(lineId)
                .map(LineResponse::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("line not found"));
    }

    public Line findLine(Long lineId){
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new EntityNotFoundException("line not found"));
        return line;
    }

    @Transactional
    public void updateLine(Long lineId, LineRequest lineRequest) {
        final Line line = this.findLine(lineId);
        line.updateInfo(lineRequest.getName(), lineRequest.getColor());
    }

    @Transactional
    public void deleteLine(Long lineId) {
        Line line = this.findLine(lineId);

        lineRepository.delete(line);
    }

    @Transactional
    public void saveSection(Long lineId, SectionRequest sectionRequest) {
        Station upStation = stationService.findStation(sectionRequest.getUpStationId());
        Station downStation = stationService.findStation(sectionRequest.getDownStationId());
        Line line = this.findLine(lineId);

        Section section = new Section(line, upStation, downStation, sectionRequest.getDistance());

        line.addSection(section);

    }



    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Line line = this.findLine(lineId);
        Station station = stationService.findStation(stationId);

        line.removeSection(station);


    }







}
