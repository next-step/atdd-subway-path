package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.member.domain.LoginMember;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;
    private StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public LineResponse saveLine(LoginMember loginMember, LineRequest request) {
        Station upStation = stationService.findStationById(loginMember, request.getUpStationId());
        Station downStation = stationService.findStationById(loginMember, request.getDownStationId());

        Line line = new Line(loginMember.getId(), request.getName(), request.getColor());
        line.addSection(upStation, downStation, request.getDistance(), request.getDuration());
        Line persistLine = lineRepository.save(line);

        return LineResponse.of(persistLine);
    }

    public List<Line> findLines(LoginMember loginMember) {
        return lineRepository.findAllByUserId(loginMember.getId());
    }

    public List<LineResponse> findLineResponses(LoginMember loginMember) {
        List<Line> persistLines = lineRepository.findAllByUserId(loginMember.getId());
        return persistLines.stream()
                .map(line -> LineResponse.of(line))
                .collect(Collectors.toList());
    }

    public Line findLineById(LoginMember loginMember, Long id) {
        Line persistLine = lineRepository.findById(id).orElseThrow(LineNotFoundException::new);
        if (!persistLine.isOwner(loginMember.getId())) {
            throw new LineNotFoundException();
        }
        return persistLine;
    }

    public void updateLine(LoginMember loginMember, Long id, LineRequest lineUpdateRequest) {
        Line persistLine = lineRepository.findById(id).orElseThrow(LineNotFoundException::new);
        if (!persistLine.isOwner(loginMember.getId())) {
            throw new LineNotFoundException();
        }
        persistLine.update(new Line(loginMember.getId(), lineUpdateRequest.getName(), lineUpdateRequest.getColor()));
    }

    public void deleteLineById(LoginMember loginMember, Long id) {
        Line persistLine = lineRepository.findById(id).orElseThrow(LineNotFoundException::new);
        if (!persistLine.isOwner(loginMember.getId())) {
            throw new LineNotFoundException();
        }
        lineRepository.deleteById(id);
    }

    public LineResponse findLineResponseById(LoginMember loginMember, Long id) {
        Line persistLine = findLineById(loginMember, id);
        return LineResponse.of(persistLine);
    }

    public void addSection(LoginMember loginMember, Long lineId, SectionRequest request) {
        Line line = findLineById(loginMember, lineId);
        Station upStation = stationService.findStationById(loginMember, request.getUpStationId());
        Station downStation = stationService.findStationById(loginMember, request.getDownStationId());
        line.addSection(upStation, downStation, request.getDistance(), request.getDuration());
    }

    public void removeSection(LoginMember loginMember, Long lineId, Long stationId) {
        Line line = findLineById(loginMember, lineId);
        Station station = stationService.findStationById(loginMember, stationId);
        line.removeSection(station);
    }
}
