package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
public class LineServiceTest {
    private static final int DEFAULT_DISTANCE = 5;
    private static final String DEFAULT_LINE_COLOR = "bg-green-600";
    private static final String FIRST_LINE_NAME = "1호선";
    private static final String SECOND_LINE_NAME = "2호선";
    private static final String FIRST_STATION_NAME = "강남역";
    private static final String SECOND_STATION_NAME = "역삼역";
    private static final String THIRD_STATION_NAME = "삼성역";
    private static final String FOURTH_STATION_NAME = "잠실역";

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private LineService lineService;

    @DisplayName("노선을 저장하다.")
    @Test
    void saveLine() {
        // given
        LineRequest request = createLineRequest(FIRST_LINE_NAME);

        // when
        LineResponse lineResponse = lineService.saveLine(request);

        // then
        assertAll(
                () -> assertThat(lineResponse.getName()).isEqualTo(request.getName()),
                () -> assertThat(lineResponse.getColor()).isEqualTo(request.getColor())
        );
    }

    @DisplayName("노선 이름을 중복으로 저장하면 예외가 발생한다")
    @Test
    void duplicationLineNameException() {
        // given
        lineService.saveLine(createLineRequest(FIRST_LINE_NAME));

        // when, then
        assertThatThrownBy(() -> lineService.saveLine(createLineRequest(FIRST_LINE_NAME)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @DisplayName("노선을 저장할 때 구간도 같이 저장한다")
    @Test
    void saveLineAndSaveSection() {
        // given
        Station 강남역 = stationRepository.save(createStationEntity(FIRST_STATION_NAME));
        Station 역삼역 = stationRepository.save(createStationEntity(SECOND_STATION_NAME));

        LineRequest lineRequest = createLineRequest(FIRST_LINE_NAME, DEFAULT_LINE_COLOR, 강남역.getId(), 역삼역.getId(), DEFAULT_DISTANCE);

        // when
        LineResponse lineResponse = lineService.saveLine(lineRequest);

        // then
        List<String> stationNames = lineResponse.getStations().stream()
                                        .map(station -> station.getName())
                                        .collect(Collectors.toList());

        assertAll(
                () -> assertThat(lineResponse.getName()).isEqualTo(lineRequest.getName()),
                () -> assertThat(lineResponse.getColor()).isEqualTo(lineRequest.getColor()),
                () -> assertThat(stationNames).contains(강남역.getName(), 역삼역.getName())
        );
    }

    @DisplayName("모든 노선을 조회한다")
    @Test
    void showLines() {
        // given
        lineService.saveLine(createLineRequest(FIRST_LINE_NAME));
        lineService.saveLine(createLineRequest(SECOND_LINE_NAME));

        // when
        List<LineResponse> lines = lineService.showLines();

        // then
        assertThat(lines).hasSize(2);
    }

    @DisplayName("단일 노선을 조회한다")
    @Test
    void findById() {
        // given
        LineResponse lineResponse = lineService.saveLine(createLineRequest(FIRST_LINE_NAME));

        // when
        LineResponse findLine = lineService.findById(lineResponse.getId());

        // then
        assertAll(
                () -> assertThat(findLine.getId()).isEqualTo(lineResponse.getId()),
                () -> assertThat(findLine.getName()).isEqualTo(lineResponse.getName()),
                () -> assertThat(findLine.getColor()).isEqualTo(lineResponse.getColor())
        );
    }

    @DisplayName("조회할 노선이 없으면 예외 발생")
    @Test
    void findByIdException() {
        // when, then
        assertThatThrownBy(() -> lineService.findById(1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("노선 정보를 수정한다")
    @Test
    void updateLine() {
        // given
        String updateName = "분당선";

        LineResponse 일호선 = lineService.saveLine(createLineRequest(FIRST_LINE_NAME));
        LineRequest updateLineRequest = createLineRequest(updateName);

        // when
        lineService.updateLine(일호선.getId(), updateLineRequest);
        LineResponse updateLineResponse = lineService.findById(일호선.getId());

        // then
        assertThat(updateLineResponse.getName()).isEqualTo(updateName);
    }

    @DisplayName("수정할 노선 이름이 중복이면 예외 발생")
    @Test
    void updateLineDuplicationNameException() {
        // given
        LineResponse 일호선 = lineService.saveLine(createLineRequest(FIRST_LINE_NAME));
        LineResponse 이호선 = lineService.saveLine(createLineRequest(SECOND_LINE_NAME));
        LineRequest 중복_이호선 = createLineRequest(SECOND_LINE_NAME);

        // when, then
        assertThatThrownBy(() -> {
            lineService.updateLine(일호선.getId(), 중복_이호선);
            lineService.showLines();
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @DisplayName("수정할 노선이 없으면 예외 발생")
    @Test
    void updateLineNotFindException() {
        // given
        LineRequest updateLineRequest = createLineRequest(FIRST_LINE_NAME);

        // when, then
        assertThatThrownBy(() -> lineService.updateLine(100000L, updateLineRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("선택한 노선을 삭제한다")
    @Test
    void deleteLine() {
        // given
        LineResponse 일호선 = lineService.saveLine(createLineRequest(FIRST_LINE_NAME));

        // when
        lineService.deleteLine(일호선.getId());
        List<LineResponse> lineResponses = lineService.showLines();

        // then
        assertThat(lineResponses).hasSize(0);
    }

    @DisplayName("구간을 추가하다.")
    @Test
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Line line = lineRepository.save(createLineEntity());
        Station 강남역 = stationRepository.save(createStationEntity(FIRST_STATION_NAME));
        Station 역삼역 = stationRepository.save(createStationEntity(SECOND_STATION_NAME));

        SectionRequest request = createSectionRequest(강남역, 역삼역);

        // when
        // lineService.addSection 호출
        lineService.addSection(line.getId(), request);

        // then
        // line.getSections 메서드를 통해 검증
        assertThat(line.getSections()).hasSize(1);
    }

    @DisplayName("기존 구간 중간에 새로운 구간을 추가하다.")
    @Test
    void addSectionInThMiddle() {
        // given
        Line line = lineRepository.save(createLineEntity());
        Station 강남역 = stationRepository.save(createStationEntity(FIRST_STATION_NAME));
        Station 삼성역 = stationRepository.save(createStationEntity(THIRD_STATION_NAME));
        Station 역삼역 = stationRepository.save(createStationEntity(SECOND_STATION_NAME));

        lineService.addSection(line.getId(), createSectionRequest(강남역, 삼성역));

        SectionRequest request = createSectionRequest(강남역, 역삼역, 2);

        // when
        lineService.addSection(line.getId(), request);

        // then
        assertAll(
                () -> assertThat(line.getSections()).hasSize(2),
                () -> assertThat(line.getStations().get(0)).isEqualTo(강남역),
                () -> assertThat(line.getStations().get(1)).isEqualTo(역삼역),
                () -> assertThat(line.getStations().get(2)).isEqualTo(삼성역)
        );
    }

    @DisplayName("구간을 추가할 노선이 조회안되면 예외 발생.")
    @Test
    void addSectionNotLineException() {
        // given
        Line line = lineRepository.save(createLineEntity());
        Station 강남역 = stationRepository.save(createStationEntity(FIRST_STATION_NAME));
        Station 역삼역 = stationRepository.save(createStationEntity(SECOND_STATION_NAME));

        SectionRequest request = createSectionRequest(강남역, 역삼역);

        // when, then
        assertThatThrownBy(() -> lineService.addSection(100000L, request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("추가할 구간의 역이 조회가 안되면 예외 발생.")
    @Test
    void addSectionNotStationException() {
        // given
        Line line = lineRepository.save(createLineEntity());

        SectionRequest request = createSectionRequest(1L, 2L, DEFAULT_DISTANCE);

        // when, then
        assertThatThrownBy(() -> lineService.addSection(line.getId(), request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("노선의 선택한 구간을 삭제한다")
    @Test
    void deleteSection() {
        // given
        Station upStation = stationRepository.save(createStationEntity(FIRST_STATION_NAME));
        Station downStation = stationRepository.save(createStationEntity(SECOND_STATION_NAME));

        LineRequest lineRequest = createLineRequest(FIRST_LINE_NAME, DEFAULT_LINE_COLOR, upStation.getId(), downStation.getId(), DEFAULT_DISTANCE);
        LineResponse lineResponse = lineService.saveLine(lineRequest);

        // when
        lineService.deleteSection(lineResponse.getId(), lineResponse.getStations().get(1).getId());
        LineResponse findLine = lineService.findById(lineResponse.getId());

        // then
        assertThat(findLine.getStations()).hasSize(0);
    }

    @DisplayName("구간을 삭제할 때 노선이 조회안되면 예외 발생")
    @Test
    void deleteSectionNotLineException() {
        // given
        Station upStation = stationRepository.save(createStationEntity(FIRST_STATION_NAME));
        Station downStation = stationRepository.save(createStationEntity(SECOND_STATION_NAME));

        LineRequest lineRequest = createLineRequest(FIRST_LINE_NAME, DEFAULT_LINE_COLOR, upStation.getId(), downStation.getId(), DEFAULT_DISTANCE);
        LineResponse lineResponse = lineService.saveLine(lineRequest);

        // when
        assertThatThrownBy(() -> lineService.deleteSection(2L, lineResponse.getStations().get(1).getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("구간의 마지막 역이 아니면 삭제 요청 시 예외 발생")
    @Test
    void deleteNotLastSectionStException() {
        // given
        Station upStation = stationRepository.save(createStationEntity(FIRST_STATION_NAME));
        Station downStation = stationRepository.save(createStationEntity(SECOND_STATION_NAME));

        LineRequest lineRequest = createLineRequest(FIRST_LINE_NAME, DEFAULT_LINE_COLOR, upStation.getId(), downStation.getId(), DEFAULT_DISTANCE);
        LineResponse lineResponse = lineService.saveLine(lineRequest);

        // when
        assertThatThrownBy(() -> lineService.deleteSection(lineResponse.getId(), upStation.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private Line createLineEntity() {
        return new Line(FIRST_STATION_NAME, DEFAULT_LINE_COLOR);
    }

    private Station createStationEntity(String name) {
        return new Station(name);
    }

    private LineRequest createLineRequest(String name) {
        return createLineRequest(name, DEFAULT_LINE_COLOR);
    }

    private LineRequest createLineRequest(String name, String color) {
        return createLineRequest(name, color, null, null, DEFAULT_DISTANCE);
    }

    private LineRequest createLineRequest(String name, String color, Long upStationId, Long downStationId, int distance) {
        LineRequest request = new LineRequest();
        request.setName(name);
        request.setColor(color);
        request.setUpStationId(upStationId);
        request.setDownStationId(downStationId);
        request.setDistance(distance);
        return request;
    }

    private SectionRequest createSectionRequest(Station upStation, Station downStation) {
        return createSectionRequest(upStation, downStation, DEFAULT_DISTANCE);
    }

    private SectionRequest createSectionRequest(Station upStation, Station downStation, int distance) {
        SectionRequest request = new SectionRequest();
        request.setUpStationId(upStation.getId());
        request.setDownStationId(downStation.getId());
        request.setDistance(distance);
        return request;
    }

    private SectionRequest createSectionRequest(Long upStationId, Long downStationId, int distance) {
        SectionRequest request = new SectionRequest();
        request.setUpStationId(upStationId);
        request.setDownStationId(downStationId);
        request.setDistance(distance);
        return request;
    }
}
