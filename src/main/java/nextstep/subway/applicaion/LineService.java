package nextstep.subway.applicaion;

import lombok.extern.slf4j.Slf4j;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j
public class LineService {
    private LineRepository lineRepository;
    private StationService stationService;

    public LineService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        Line line = lineRepository.save(new Line(request.getName(), request.getColor()));
        Station upStation = stationService.findById(request.getUpStationId());
        Station downStation = stationService.findById(request.getDownStationId());
        line.getSections().add(new Section(line, upStation, downStation, request.getDistance()));

        return createLineResponse(line);
    }

    public List<LineResponse> showLines() {
        return lineRepository.findAll().stream()
                .map(this::createLineResponse)
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        return createLineResponse(lineRepository.findById(id).orElseThrow(IllegalArgumentException::new));
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findById(id).orElseThrow(IllegalArgumentException::new);

        /**
         * TODO
         * - LineRequest 객체 내부에서 Validation 처리
         */
        if (lineRequest.getName() != null) {
            line.setName(lineRequest.getName());
        }
        if (lineRequest.getColor() != null) {
            line.setColor(lineRequest.getColor());
        }
    }

    @Transactional
    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Station upStation = stationService.findById(sectionRequest.getUpStationId());
        Station downStation = stationService.findById(sectionRequest.getDownStationId());
        Line line = lineRepository.findById(lineId).orElseThrow(IllegalArgumentException::new);

        // new logic start - 0723
        // 기준 - 지하철 노선에 역이 2개만 존재하는 경우
        List<Section> savedSections = line.getSections();
        Section startingSection = savedSections.get(0);
        Station startStation = startingSection.getUpStation();
        Station endStation = startingSection.getDownStation();

        /**
         * 0. 기존 코드에 영향이 가지 않도록 if문 이용 후 메서드 종료 시 return
         * 1. 저장된 Section 객체들 조회
         * 2. 케이스에 따라 Section 객체 update
         * 3. 케이스에 따른 새로운 Section 생성 후 저장
         */
        // 1번 케이스 -> 구간 사이에 새로운 구간이 생성되는 경우
        if (upStation.getId() == startStation.getId()) {
            log.info("[LineService] Check case1");

            /**
             * TODO
             * - 반복문으로 추출해서 3개 이상인 경우에 대해 고려 -> 반복문을 할 필요가 없을 듯(계속해서 메서드 호출되면서 누적되기 때문?)
             * - 2개인 경우와 3개인 경우에 대해서 메서드 분리 후 각각의 로직 실행 후 return
             * - totalDistance 변수를 메서드 상단에 위치
             * */
            int requestedDistance = sectionRequest.getDistance();
            int calculatedDistance = startingSection.getDistance() - requestedDistance;
            startingSection.updateSection(upStation, downStation, calculatedDistance);
            line.getSections().add(new Section(line, upStation, endStation, calculatedDistance));
            return;
        }
        // 2번 케이스 -> 새로운 구간의 상행 역이 상행 종점역인 경우
        else if (downStation.getId() == startStation.getId()) {
            log.info("[LineService] Check case2");

            int requestedDistance = sectionRequest.getDistance();
            startingSection.updateSection(upStation, startStation, requestedDistance);
            line.getSections().add(new Section(line, downStation, endStation, startingSection.getDistance()));
            return;
        }
        log.info("[LineService] Check case3");
        // 3번 케이스 -> 새로운 구간의 상행 역이 하행 종점역인 경우(기존 로직)
        // new logic end - 0723
        line.getSections().add(new Section(line, upStation, downStation, sectionRequest.getDistance()));
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId).orElseThrow(IllegalArgumentException::new);
        Station station = stationService.findById(stationId);

        if (!line.getSections().get(line.getSections().size() - 1).getDownStation().equals(station)) {
            throw new IllegalArgumentException();
        }

        line.getSections().remove(line.getSections().size() - 1);
    }

    private LineResponse createLineResponse(Line line) {
        return new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                createStationResponses(line)
        );
    }

    private List<StationResponse> createStationResponses(Line line) {
        if (line.getSections().isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = line.getSections().stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());

        stations.add(0, line.getSections().get(0).getUpStation());

        return stations.stream()
                .map(it -> stationService.createStationResponse(it))
                .collect(Collectors.toList());
    }
}