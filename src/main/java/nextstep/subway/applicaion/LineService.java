package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.*;
import nextstep.subway.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;
    private SectionRepository sectionRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, SectionRepository sectionRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Station upStation = findStationById(request.getUpStationId());
        Station downStation = findStationById(request.getDownStationId());

        Line line = new Line(request.getName(), request.getColor());
        if (Objects.nonNull(upStation) && Objects.nonNull(downStation)) {
            line.addSection(upStation, downStation, request.getDistance());
        }

        return new LineResponse(lineRepository.save(line));
    }

    @Transactional(readOnly = true)
    public List<LineIncludingStationsResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(LineIncludingStationsResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineIncludingStationsResponse findById(Long id) {
        return new LineIncludingStationsResponse(findLineById(id));
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = findLineById(id);
        line.update(lineRequest.getName(), lineRequest.getColor());
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    public SectionResponse addSection(SectionRequest request, Long lineId) {
        Line line = findLineById(lineId);
        Station upStation = findStationById(request.getUpStationId());
        Station downStation = findStationById(request.getDownStationId());

        Section addedSection = line.addSection(upStation, downStation, request.getDistance());
        return new SectionResponse(sectionRepository.save(addedSection));
    }

    public void deleteSection(Long lineId, Long stationId) {
        Line line = findLineById(lineId);
        Station station = findStationById(stationId);

        line.removeSection(station);
    }

    private Line findLineById(Long lineId) {
        if (Objects.isNull(lineId)) {
            throw new NullPointerException("Null 을 입력받아 역을 찾을 수 없습니다");
        }
        return lineRepository.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException("해당 노선을 찾을 수 없습니다. lineId:" + lineId));
    }

    private Station findStationById(Long stationId) {
        if (Objects.isNull(stationId)) {
            throw new NullPointerException("Null 을 입력받아 역을 찾을 수 없습니다");
        }
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 역을 찾을 수 없습니다. stationId:" + stationId));
    }
}
