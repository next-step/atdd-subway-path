package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.request.LineCreateRequest;
import nextstep.subway.applicaion.dto.request.LineUpdateRequest;
import nextstep.subway.applicaion.dto.response.LineResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly =true)
public class LineService {

    private final LineRepository lineRepository;

    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineCreateRequest lineCreateRequest) {
        Station upStation = stationRepository.findById(lineCreateRequest.getUpStationId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역입니다."));
        Station downStation = stationRepository.findById(lineCreateRequest.getDownStationId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역입니다."));

        Line line = lineRepository.save(new Line(lineCreateRequest.getName()
                                                        ,lineCreateRequest.getColor()
                                                        ,upStation
                                                        ,downStation
                                                        ,lineCreateRequest.getDistance()));


        return new LineResponse(line);
    }

    public List<LineResponse> findAllLineResponse() {
        return lineRepository.findAll().stream()
                .map(LineResponse::createLineResponse)
                .collect(Collectors.toList());
    }

    public List<LineResponse> findByIdLineResponse(Long id) {
        return lineRepository.findById(id).stream()
                .map(LineResponse::createLineResponse)
                .collect(Collectors.toList());
    }


    @Transactional
    public LineResponse updateLine(LineUpdateRequest lineCreateRequest) {
        Line line = lineRepository.findById(lineCreateRequest.getId()).get();
        line.changeName(lineCreateRequest.getName());
        line.changeColor(lineCreateRequest.getColor());

        return LineResponse.createLineResponse(line);
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }
}
