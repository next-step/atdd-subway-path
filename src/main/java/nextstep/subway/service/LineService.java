package nextstep.subway.service;

import nextstep.subway.controller.dto.LineCreateRequest;
import nextstep.subway.controller.dto.LineResponse;
import nextstep.subway.controller.dto.LineUpdateRequest;
import nextstep.subway.controller.dto.SectionAddRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.SectionRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

/** 지하철 노선 관리 담당 서비스 */
@Transactional(readOnly = true)
@Service
public class LineService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository, SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    /**
     * 주어진 지하철 노선 생성 데이터로 지하철 노선을 생성 후 생성 정보를 반환합니다.
     *
     * @param createRequest 지하철 노선 생성 데이터
     * @return 생성된 지하철 노선 정보
     */
    @Transactional
    public LineResponse createLine(LineCreateRequest createRequest) {

        Station upStation = findStationById(createRequest.getUpStationId());
        Station downStation = findStationById(createRequest.getDownStationId());

        Line line = Line.of(
            createRequest.getName(),
            createRequest.getColor()
        );

        line.addSection(upStation, downStation, createRequest.getDistance());

        return LineResponse.of(lineRepository.save(line));
    }

    /**
     * 모든 지하철 노선 정보를 반환합니다.
     *
     * @return 모든 지하철 노선 정보
     */
    public List<LineResponse> getLines() {
        return lineRepository.findAll()
            .stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    /**
     * 주어진 지하철 노선 식별자로 찾은 노션의 정보를 반환합니다. 찾지못하면 예외를 던집니다.
     *
     * @param lineId 지하철 노선 식별자
     * @return 지하철 노선 정보
     * @throws EntityNotFoundException 식별자에 해당하는 지하철노선을 찾지 못한 경우 던짐
     */
    public LineResponse getLine(Long lineId) {
        return LineResponse.of(findLineById(lineId));
    }

    /**
     * 주어진 지하철 노선 식별자로 찾은 노선의 정보를 주어진 변경 정보로 수정합니다.
     * 지하철 노선을 찾지 못하면 예외를 던집니다.
     *
     * @param lineId           지하철 노선 식별자
     * @param updateRequestDto 지하철 노선 변경 데이터
     * @throws EntityNotFoundException 식별자에 해당하는 지하철노선을 찾지 못한 경우 던짐
     */
    @Transactional
    public void updateLine(Long lineId, LineUpdateRequest updateRequestDto) {
        Line line = findLineById(lineId);

        lineRepository.save(
            line.update(updateRequestDto.getName(), updateRequestDto.getColor())
        );
    }

    /**
     * 주어진 지하철 노선 식별자로 찾은 노선을 삭제합니다. 만약 찾지 못하면 예외를 던집니다.
     *
     * @param lineId 지하철 노선 식별자
     * @throws EntityNotFoundException 식별자에 해당하는 지하철노선을 찾지 못한 경우 던짐
     */
    @Transactional
    public void deleteLine(Long lineId) {
        Line line = findLineById(lineId);

        sectionRepository.deleteAll(line.getAllSections());
        lineRepository.delete(line);
    }

    @Transactional
    public void addSection(Long lineId, SectionAddRequest request) {
        Line line = findLineById(lineId);

        Station upStation = findStationById(request.getUpStationId());
        Station downStation = findStationById(request.getDownStationId());

        line.addSection(upStation, downStation, request.getDistance());
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Line line = findLineById(lineId);

        Section section = sectionRepository.findByLineIdAndDownStationId(lineId, stationId)
            .orElseThrow(EntityNotFoundException::new);

        line.removeSection(section);
    }

    /** 주어진 지하철 노선 식별자로 찾은 노선정보 엔티티 반환. 찾지못하면 예외 던짐 */
    private Line findLineById(final Long lineId) {
        // TODO: 익셉션 정의?
        return lineRepository.findById(lineId)
            .orElseThrow(EntityNotFoundException::new);
    }

    private Station findStationById(Long stationId) {
        return stationRepository.findById(stationId)
            .orElseThrow(EntityNotFoundException::new);
    }
}
