package nextstep.subway.applicaion;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.LineCreationRequest;
import nextstep.subway.applicaion.dto.LineModificationRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRegistrationRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class LineCommandService {

    private final LineQueryService lineQueryService;
    private final StationQueryService stationQueryService;
    private final LineRepository lineRepository;

    public LineResponse saveLine(LineCreationRequest lineRequest) {
        Station upStation = stationQueryService.findById(lineRequest.getUpStationId());
        Station downStation = stationQueryService.findById(lineRequest.getDownStationId());

        Line line = lineRepository.save(new Line(lineRequest.getName(), lineRequest.getColor()));
        line.addSection(upStation, downStation, lineRequest.getDistance());
        return LineResponse.from(line);
    }

    public void addSection(Long lineId, SectionRegistrationRequest sectionRequest) {
        Station upStation = stationQueryService.findById(sectionRequest.getUpStationId());
        Station downStation = stationQueryService.findById(sectionRequest.getDownStationId());

        lineQueryService.findById(lineId)
                .addSection(upStation, downStation, sectionRequest.getDistance());
    }

    public void modifyLine(Long lineId, LineModificationRequest lineRequest) {
        lineQueryService.findById(lineId)
                .updateNameAndColor(lineRequest.getName(), lineRequest.getColor());
    }

    public void deleteLineById(Long lineId) {
        lineRepository.deleteById(lineId);
    }

    public void removeSection(Long lineId, Long stationId) {
        Station station = stationQueryService.findById(stationId);
        lineQueryService.findById(lineId)
                .removeSection(station);
    }

}
