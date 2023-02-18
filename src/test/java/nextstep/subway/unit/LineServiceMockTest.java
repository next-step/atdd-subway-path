package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

@DisplayName("지하철 구간 서비스 Mock 테스트")
@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @InjectMocks
    private LineService lineService;

    private Long upStationId;
    private Long downStationId;
    private Long deleteStationId;
    private Long lineId;
    private Station upStation;
    private Station downStation;
    private Station deleteStation;
    private Line line;
    private final int distance = 10;

    @BeforeEach
    void setUp() {
        upStationId = 1L;
        downStationId = 2L;
        deleteStationId = 3L;

        lineId = 1L;

        upStation = new Station("upStation");
        downStation = new Station("downStation");
        deleteStation = new Station("deleteStation");
        line = new Line("line", "color");
    }

    @DisplayName("지하철 노선에 구간을 추가한다.")
    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        given(stationService.findById(upStationId)).willReturn(upStation);
        given(stationService.findById(downStationId)).willReturn(downStation);
        given(lineRepository.getLine(lineId)).willReturn(line);

        // when
        // lineService.addSection 호출
        lineService.addSection(lineId, new SectionRequest(upStationId, downStationId, distance));

        // then
        // lineService.findLineById 메서드를 통해 검증
        final LineResponse lineResponse = lineService.findById(lineId);
        final List<String> stationNames = lineResponse.getStations().stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());
        assertAll(
                () -> assertThat(lineResponse.getName()).isEqualTo(line.getName()),
                () -> assertThat(lineResponse.getColor()).isEqualTo(line.getColor()),
                () -> assertThat(stationNames).containsExactly(upStation.getName(), downStation.getName())
        );
    }

    @DisplayName("지하철 노선의 구간을 삭제한다.")
    @Test
    void removeSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        // lineService.addSection 호출
        given(stationService.findById(upStationId)).willReturn(upStation);
        given(stationService.findById(downStationId)).willReturn(downStation);
        given(stationService.findById(deleteStationId)).willReturn(deleteStation);
        given(lineRepository.getLine(lineId)).willReturn(line);

        lineService.addSection(lineId, new SectionRequest(upStationId, downStationId, distance));
        lineService.addSection(lineId, new SectionRequest(downStationId, deleteStationId, distance));

        // when
        // lineService.deleteSection 호출
        lineService.deleteSection(lineId, deleteStationId);

        // then
        // lineService.findLineById 메서드를 통해 검증
        final LineResponse lineResponse = lineService.findById(lineId);
        assertThat(lineResponse.getStations()).hasSize(2);
    }
}
