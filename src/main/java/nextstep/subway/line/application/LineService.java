package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineUpdateRequest;
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

    public LineResponse saveLine(LoginMember loginMember, LineRequest lineRequest) {
        checkDuplicateName(loginMember, lineRequest.getName());
        Station upStation = stationService.findMyStationById(loginMember, lineRequest.getUpStationId());
        Station downStation = stationService.findMyStationById(loginMember, lineRequest.getDownStationId());

        Line line = new Line(loginMember.getId(), lineRequest.getName(), lineRequest.getColor());
        line.addSection(upStation, downStation, lineRequest.getDistance(), lineRequest.getDuration());
        Line persistLine = lineRepository.save(line);

        return LineResponse.of(persistLine);
    }

    private void checkDuplicateName(LoginMember loginMember, String name) {
        List<Line> lines = lineRepository.findAllByUserId(loginMember.getId());
        lines.stream()
                .filter(it -> it.getName().equals(name))
                .findFirst()
                .ifPresent(it -> {
                    throw new LineDuplicateException();
                });
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

    public void updateLine(LoginMember loginMember, Long id, LineUpdateRequest lineUpdateRequest) {
        Line line = findMyLineById(loginMember, id);
        if (!line.getName().equals(lineUpdateRequest.getName())) {
            checkDuplicateName(loginMember, lineUpdateRequest.getName());
        }
        line.update(new Line(loginMember.getId(), lineUpdateRequest.getName(), lineUpdateRequest.getColor()));
    }

    public void deleteLineById(LoginMember loginMember, Long id) {
        Line line = findMyLineById(loginMember, id);
        lineRepository.delete(line);
    }

    public LineResponse findLineResponseById(LoginMember loginMember, Long id) {
        Line line = findMyLineById(loginMember, id);
        return LineResponse.of(line);
    }

    public Line findMyLineById(LoginMember loginMember, Long id) {
        Line line = findLineById(id);
        checkOwner(loginMember, line);
        return line;
    }

    private Line findLineById(Long id) {
        return lineRepository.findById(id).orElseThrow(LineNotFoundException::new);
    }

    private void checkOwner(LoginMember loginMember, Line line) {
        if (!line.isOwner(loginMember.getId())) {
            throw new LineNotFoundException();
        }
    }

    public void addSection(LoginMember loginMember, Long lineId, SectionRequest request) {
        Line line = findMyLineById(loginMember, lineId);
        Station upStation = stationService.findMyStationById(loginMember, request.getUpStationId());
        Station downStation = stationService.findMyStationById(loginMember, request.getDownStationId());
        line.addSection(upStation, downStation, request.getDistance(), request.getDuration());
    }

    public void removeSection(LoginMember loginMember, Long lineId, Long stationId) {
        Line line = findMyLineById(loginMember, lineId);
        Station station = stationService.findMyStationById(loginMember, stationId);
        line.removeSection(station);
    }
}
