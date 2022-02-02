package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    private static final int DEFAULT_DISTANCE = 5;
    private static final String DEFAULT_LINE_COLOR = "bg-green-600";
    private static final String FIRST_LINE_NAME = "1호선";
    private static final String SECOND_LINE_NAME = "2호선";
    private static final Long FIRST_STATION_ID = 1L;
    private static final String FIRST_STATION_NAME = "강남역";
    private static final Long SECOND_STATION_ID = 2L;
    private static final String SECOND_STATION_NAME = "역삼역";

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;
    @InjectMocks
    private LineService lineService;


    @DisplayName("노선을 저장하다.")
    @Test
    void saveLine() {
        // given
        Line line = createLineEntity(FIRST_LINE_NAME);
        LineRequest lineRequest = createLineRequest(line.getName(), line.getColor());

        when(lineRepository.save(any())).thenReturn(line);

        // when
        LineResponse lineResponse = lineService.saveLine(lineRequest);

        // then
        assertAll(
                () -> verify(lineRepository, times(1)).save(any()),
                () -> assertThat(lineResponse.getName()).isEqualTo(line.getName()),
                () -> assertThat(lineResponse.getColor()).isEqualTo(line.getColor())
        );
    }

    @DisplayName("노선 이름을 중복으로 저장하면 예외가 발생한다")
    @Test
    void duplicationLineNameException() {
        // given
        Line line = createLineEntity(FIRST_LINE_NAME);
        LineRequest lineRequest = createLineRequest(line.getName(), line.getColor());
        LineRequest duplicationNameLineRequest = createLineRequest(line.getName(), line.getColor());

        when(lineRepository.save(any())).thenReturn(line);
        lineService.saveLine(lineRequest);

        when(lineRepository.save(any())).thenThrow(DataIntegrityViolationException.class);

        // when, then
        assertThatThrownBy(() -> lineService.saveLine(duplicationNameLineRequest))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @DisplayName("노선을 저장할 때 구간도 같이 저장한다")
    @Test
    void saveLineAndSaveSection() {
        // given
        Line line = createLineEntity(FIRST_LINE_NAME);
        Station upStation = createStationEntity(FIRST_STATION_ID, FIRST_STATION_NAME);
        Station downStation = createStationEntity(SECOND_STATION_ID, SECOND_STATION_NAME);
        LineRequest lineRequest = createLineRequest(
                line.getName(),
                line.getColor(),
                upStation.getId(),
                downStation.getId(),
                DEFAULT_DISTANCE
        );

        when(lineRepository.save(any())).thenReturn(line);
        when(stationService.findById(upStation.getId())).thenReturn(upStation);
        when(stationService.findById(downStation.getId())).thenReturn(downStation);
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));

        // when
        lineService.saveLine(lineRequest);
        LineResponse lineResponse = lineService.findById(line.getId());

        // then
        assertAll(
                () -> verify(lineRepository, times(1)).save(any()),
                () -> verify(stationService, times(2)).findById(any()),
                () -> assertThat(lineResponse.getId()).isEqualTo(line.getId()),
                () -> assertThat(lineResponse.getStations()).hasSize(2)
        );
    }

    @DisplayName("모든 노선을 조회한다")
    @Test
    void showLines() {
        // given
        Line line1 = createLineEntity(FIRST_LINE_NAME);
        Line line2 = createLineEntity(SECOND_LINE_NAME);

        when(lineRepository.save(any())).thenReturn(line1);
        when(lineRepository.save(any())).thenReturn(line2);
        when(lineRepository.findAll()).thenReturn(Arrays.asList(line1, line2));

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
        Line line = createLineEntity(FIRST_LINE_NAME);
        LineRequest lineRequest = createLineRequest(line.getName(), line.getColor());

        when(lineRepository.save(any())).thenReturn(line);
        when(lineRepository.findById(any())).thenReturn(Optional.of(line));

        lineService.saveLine(lineRequest);

        // when
        LineResponse lineResponse = lineService.findById(line.getId());

        // then
        assertAll(
                () -> verify(lineRepository, times(1)).findById(any()),
                () -> assertThat(line.getName()).isEqualTo(lineResponse.getName()),
                () -> assertThat(line.getColor()).isEqualTo(lineResponse.getColor())
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
        Line line = createLineEntity(FIRST_LINE_NAME);
        LineRequest lineRequest = createLineRequest(line.getName(), line.getColor());
        LineRequest updateLineRequest = createLineRequest(SECOND_LINE_NAME, line.getColor());

        when(lineRepository.save(any())).thenReturn(line);
        when(lineRepository.findById(any())).thenReturn(Optional.of(line));

        LineResponse lineResponse = lineService.saveLine(lineRequest);

        // when
        lineService.updateLine(line.getId(), updateLineRequest);
        LineResponse updateLineResponse = lineService.findById(lineResponse.getId());

        // then
        assertAll(
                () -> verify(lineRepository, times(2)).findById(any()),
                () -> assertThat(updateLineResponse.getName()).isEqualTo(SECOND_LINE_NAME)
        );
    }

    @DisplayName("수정할 노선 이름이 중복이면 예외 발생")
    @Test
    void updateLineDuplicationNameException() {
        // given
        Line line1 = createLineEntity(FIRST_LINE_NAME);
        Line line2 = createLineEntity(SECOND_LINE_NAME);
        LineRequest lineRequest1 = createLineRequest(line1.getName(), line1.getColor());
        LineRequest lineRequest2 = createLineRequest(line2.getName(), line2.getColor());

        when(lineRepository.save(any())).thenReturn(line1)
                                        .thenReturn(line2);
        when(lineRepository.findById(any())).thenReturn(Optional.of(line1));

        lineService.saveLine(lineRequest1);
        lineService.saveLine(lineRequest2);

        // TODO : 에러가 발생안하는 경우는 무엇일까?
        // when, then
        lineService.updateLine(line1.getId(), lineRequest2);

        assertThatThrownBy(() -> lineService.updateLine(line1.getId(), lineRequest2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @DisplayName("수정할 노선이 없으면 예외 발생")
    @Test
    void updateLineNotFindException() {
        when(lineRepository.findById(any())).thenThrow(IllegalArgumentException.class);

        // when, then
        assertThatThrownBy(() -> lineService.updateLine(100000L, createLineRequest(FIRST_LINE_NAME, DEFAULT_LINE_COLOR)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("선택한 노선을 삭제한다")
    @Test
    void deleteLine() {
        // given
        Line line = createLineEntity(FIRST_LINE_NAME);
        LineRequest lineRequest = createLineRequest(line.getName(), line.getColor());

        when(lineRepository.save(any())).thenReturn(line);
        when(lineRepository.findAll()).thenReturn(new ArrayList<>());

        LineResponse lineResponse = lineService.saveLine(lineRequest);

        // when
        lineService.deleteLine(lineResponse.getId());
        List<LineResponse> lineResponses = lineService.showLines();

        // then
        assertAll(
                () -> assertThat(lineResponses).hasSize(0)
        );
    }

    @DisplayName("구간을 추가할 노선이 조회안되면 예외 발생.")
    @Test
    void addSectionNotLineException() {
        // given
        Station upStation = createStationEntity(FIRST_STATION_NAME);
        Station downStation = createStationEntity(SECOND_STATION_NAME);
        SectionRequest request = createSectionRequest(upStation, downStation);

        when(stationService.findById(any())).thenReturn(upStation)
                                            .thenReturn(downStation);
        when(lineRepository.findById(any())).thenThrow(IllegalArgumentException.class);

        // when, then
        assertThatThrownBy(() -> lineService.addSection(100000L, request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("추가할 구간의 역이 조회안되면 예외 발생.")
    @Test
    void addSectionNotStationException() {
        // given
        SectionRequest request = createSectionRequest(1L, 2L, DEFAULT_DISTANCE);

        when(lineRepository.findById(any())).thenThrow(IllegalArgumentException.class);

        // when, then
        assertThatThrownBy(() -> lineService.addSection(1L, request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("노선에 구간을 추가한다")
    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        Line line = createLineEntity(FIRST_LINE_NAME);
        Station upStation = createStationEntity(FIRST_STATION_ID, FIRST_STATION_NAME);
        Station downStation = createStationEntity(SECOND_STATION_ID, SECOND_STATION_NAME);
        SectionRequest sectionRequest = createSectionRequest(upStation.getId(), downStation.getId());

        when(stationService.findById(upStation.getId())).thenReturn(upStation);
        when(stationService.findById(downStation.getId())).thenReturn(downStation);
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));

        // when
        // lineService.addSection 호출
        lineService.addSection(line.getId(), sectionRequest);

        // then
        // line.findLineById 메서드를 통해 검증
        LineResponse lineResponse = lineService.findById(line.getId());
        assertAll(
                () -> verify(stationService, times(2)).findById(any()),
                () -> assertThat(lineResponse.getId()).isEqualTo(line.getId()),
                () -> assertThat(lineResponse.getStations()).hasSize(2)
        );
    }

    @DisplayName("노선의 선택한 구간을 삭제한다")
    @Test
    void deleteSection() {
        // given
        Line line = createLineEntity(FIRST_LINE_NAME);
        Station upStation = createStationEntity(FIRST_STATION_ID, FIRST_STATION_NAME);
        Station downStation = createStationEntity(SECOND_STATION_ID, SECOND_STATION_NAME);
        SectionRequest sectionRequest = createSectionRequest(upStation.getId(), downStation.getId());

        when(stationService.findById(upStation.getId())).thenReturn(upStation);
        when(stationService.findById(downStation.getId())).thenReturn(downStation);
        when(stationService.findById(downStation.getId())).thenReturn(downStation);

        when(lineRepository.findById(any())).thenReturn(Optional.of(line))
                                            .thenReturn(Optional.of(line));

        lineService.addSection(line.getId(), sectionRequest);

        // when
        lineService.deleteSection(line.getId(), downStation.getId());
        LineResponse findLine = lineService.findById(line.getId());

        // then
        assertAll(
                () -> verify(stationService, times(3)).findById(any()),
                () -> verify(lineRepository, times(3)).findById(any()),
                () -> assertThat(findLine.getStations()).hasSize(0)
        );
    }

    @DisplayName("구간을 삭제할 때 노선이 조회안되면 예외 발생")
    @Test
    void deleteSectionNotLineException() {
        // given
        when(lineRepository.findById(any())).thenThrow(IllegalArgumentException.class);

        // when, then
        assertThatThrownBy(() -> lineService.deleteSection(1L, 1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("구간의 마지막 역이 아니면 삭제 요청 시 예외 발생")
    @Test
    void deleteNotLastSectionStException() {
        // given
        Line line = createLineEntity(FIRST_LINE_NAME);
        Station upStation = createStationEntity(FIRST_STATION_ID, FIRST_STATION_NAME);
        Station downStation = createStationEntity(SECOND_STATION_ID, SECOND_STATION_NAME);
        SectionRequest sectionRequest = createSectionRequest(upStation.getId(), downStation.getId());

        when(stationService.findById(upStation.getId())).thenReturn(upStation);
        when(stationService.findById(downStation.getId())).thenReturn(downStation);
        when(lineRepository.findById(any())).thenReturn(Optional.of(line))
                .thenReturn(Optional.of(line));
        when(stationService.findById(downStation.getId())).thenReturn(downStation);

        lineService.addSection(line.getId(), sectionRequest);

        // when, then
        assertThatThrownBy(() -> lineService.deleteSection(line.getId(), upStation.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }
//

    private SectionRequest createSectionRequest(Long upStationId, Long downStationId) {
        return createSectionRequest(upStationId, downStationId, DEFAULT_DISTANCE);
    }

    private SectionRequest createSectionRequest(Long upStationId, Long downStationId, int distance) {
        SectionRequest sectionRequest = new SectionRequest();
        sectionRequest.setUpStationId(upStationId);
        sectionRequest.setDownStationId(downStationId);
        sectionRequest.setDistance(distance);
        return sectionRequest;
    }

    private Station createStationEntity(Long stationId, String stationName) {
        Station station = new Station();
        station.setId(stationId);
        station.setName(stationName);
        return station;
    }

    private Line createLineEntity(String name) {
        return createLineEntity(name, DEFAULT_LINE_COLOR);
    }

    private Line createLineEntity(String name, String color) {
        Line line = new Line();
        line.setName(name);
        line.setColor(color);
        return line;
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
}
