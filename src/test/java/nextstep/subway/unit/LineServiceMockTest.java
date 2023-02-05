package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@DisplayName("노선의 대한 Mock 테스트")
@ExtendWith(MockitoExtension.class)
class LineServiceMockTest {

    @Mock
    private LineRepository lineRepository;
    @InjectMocks
    private LineService lineService;

    @DisplayName("노선 목록을 조회한다.")
    @Test
    void showLines() {
        final Station 상행종점_강남역 = new Station(1L, "강남역");
        final Station 하행종점_잠실역 = new Station(2L, "잠실역");
        final Line 이호선 = new Line(1L, "2호선", "bg-red-600", 상행종점_강남역, 하행종점_잠실역, 10);
        final Station 상행종점_양재역 = new Station(3L, "양재역");
        final Station 하행종점_몽총토성역 = new Station(4L, "몽촌토성역");
        final Line 신분당선 = new Line(2L, "신분당선", "bg-red-600", 상행종점_양재역, 하행종점_몽총토성역, 10);

        when(lineRepository.findAll()).thenReturn(List.of(이호선, 신분당선));

        final List<LineResponse> 응답_노선_목록 = lineService.showLines();

        assertThat(응답_노선_목록).hasSize(2);
    }

    @DisplayName("특정 노선을 조회한다.")
    @Test
    void findLine() {
        final Long 조회_노선 = 1L;
        final Station 상행종점_강남역 = new Station(1L, "강남역");
        final Station 하행종점_잠실역 = new Station(2L, "잠실역");
        final Line 이호선 = new Line(1L, "2호선", "bg-red-600", 상행종점_강남역, 하행종점_잠실역, 10);

        when(lineRepository.findById(1L)).thenReturn(Optional.of(이호선));

        final LineResponse 응답_노선 = lineService.findById(조회_노선);

        assertAll(
                () -> assertThat(응답_노선.getId()).isEqualTo(1L),
                () -> assertThat(응답_노선.getName()).isEqualTo("2호선"),
                () -> assertThat(응답_노선.getColor()).isEqualTo("bg-red-600"),
                () -> assertThat(응답_노선.getStationResponses()).hasSize(2)
        );
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void updateLine() {
        final Long 요청_조회_노선 = 1L;
        final LineRequest 요청_수정_노선 = LineRequest.from("2호선", "bg-red-600");
        final Station 상행종점_강남역 = new Station(1L, "강남역");
        final Station 하행종점_잠실역 = new Station(2L, "잠실역");
        final Line 이호선 = new Line(1L, "2호선", "bg-red-600", 상행종점_강남역, 하행종점_잠실역, 10);

        when(lineRepository.findById(1L)).thenReturn(Optional.of(이호선));

        lineService.updateLine(요청_조회_노선, 요청_수정_노선);

        assertAll(
                () -> assertThat(이호선.getName()).isEqualTo("2호선"),
                () -> assertThat(이호선.getColor()).isEqualTo("bg-red-600")
        );
    }

    @DisplayName("노선을 삭제한다.")
    @Test
    void deleteLine() {
        final Long 요청_삭제_노선 = 1L;
        doNothing().when(lineRepository).deleteById(anyLong());

        lineService.deleteLine(요청_삭제_노선);

        final InOrder inOrder = inOrder(lineRepository);
        inOrder.verify(lineRepository, times(1)).deleteById(anyLong());
    }
}
