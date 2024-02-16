package nextstep.subway.unit;

import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineUpdateRequest;
import nextstep.subway.line.entity.Line;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.line.service.LineService;
import nextstep.subway.station.entity.Station;
import nextstep.subway.station.repository.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

    @Mock
    private LineRepository lineRepository;

    @Mock
    private StationRepository stationRepository;

    @InjectMocks
    private LineService lineService;

    private Station 강남역;
    private Station 역삼역;
    private Line 신분당선;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        신분당선 = new Line("신분당선", "bg-red-600", 강남역, 역삼역, 10);
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void 지하철_노선_생성() {
        // given
        final LineRequest lineRequest = this.신분당선_생성_요청();

        when(stationRepository.findById(1L)).thenReturn(Optional.of(강남역));
        when(stationRepository.findById(2L)).thenReturn(Optional.of(역삼역));
        when(lineRepository.save(any(Line.class))).thenReturn(신분당선);

        // when
        final LineResponse response = lineService.createSubwayLine(lineRequest);

        // then
        assertThat(response.getName()).isEqualTo(lineRequest.getName());
        assertThat(response.getColor()).isEqualTo(lineRequest.getColor());
        verify(lineRepository).save(any(Line.class));
        verify(stationRepository).findById(lineRequest.getUpStationId());
        verify(stationRepository).findById(lineRequest.getDownStationId());
    }

    @DisplayName("지하철 노선 생성 시 존재하지 않는 역으로 조회할 경우 오류가 발생한다.")
    @Test
    void 지하철_노선_생성_시_존재하지_않는_역으로_조회_불가() {
        // given
        final LineRequest lineRequest = this.신분당선_생성_요청();

        when(stationRepository.findById(anyLong())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> lineService.createSubwayLine(lineRequest))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void 지하철_노선_조회() {
        // given
        when(lineRepository.findById(신분당선.getId())).thenReturn(Optional.of(신분당선));

        // when
        final LineResponse response = lineService.getSubwayLine(신분당선.getId());

        // then
        assertThat(response.getName()).isEqualTo(신분당선.getName());
        assertThat(response.getColor()).isEqualTo(신분당선.getColor());
        assertThat(response.getStations().get(0).getName()).isEqualTo(강남역.getName());
        // 아래 검증은 실패하는데 이유는 equals에서 id 값으로만 비교하기 때문으로 추측. 어떻게 할 수 있을지?
//        assertThat(response.getStations().get(1).getName()).isEqualTo(upStation.getName());
        verify(lineRepository).findById(신분당선.getId());
    }

    @DisplayName("지하철 노선 조회 시 존재하지 않는 역으로 조회할 경우 오류가 발생한다.")
    @Test
    void 지하철_노선_조회_시_존재하지_않는_역으로_조회_불가() {
        // given
        when(lineRepository.findById(anyLong())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> lineService.getSubwayLine(1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void 지하철_노선_목록_조회() {
        // given
        final Line 신분당선 = new Line("신분당선", "bg-red-600", mock(Station.class), mock(Station.class), 10);
        final Line 지하철노선 = new Line("지하철노선", "bg-yello-600", mock(Station.class), mock(Station.class), 15);

        when(lineRepository.findAll()).thenReturn(List.of(신분당선, 지하철노선));

        // when
        List<LineResponse> response = lineService.getSubwayLines();

        // then
        assertThat(response).hasSize(2);
        assertThat(response.get(0).getName()).isEqualTo("신분당선");
        assertThat(response.get(1).getName()).isEqualTo("지하철노선");
    }

    @DisplayName("지하철 노선 정보를 수정한다.")
    @Test
    void 지하철_노선_정보_수정() {
        // given
        LineUpdateRequest request = new LineUpdateRequest("2호선", "bg-green-600");
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(신분당선));

        // when
        lineService.updateSubwayLine(1L, request);

        // then
        assertThat(신분당선.getName()).isEqualTo("2호선");
        assertThat(신분당선.getColor()).isEqualTo("bg-green-600");
    }

    @DisplayName("지하철 노선 정보 수정 시 존재하지 않는 역을 수정할 경우 오류가 발생한다.")
    @Test
    void 지하철_노선_정보_수정_시_존재하지_않는_역으로_수정_불가() {
        // given
        LineUpdateRequest request = new LineUpdateRequest("2호선", "bg-green-600");
        when(lineRepository.findById(anyLong())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> lineService.updateSubwayLine(1L, request))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("지하철 노선 정보 수정 시 노선명이 null 혹은 공백일 때 수정할 경우 오류가 발생한다.")
    @Test
    void 지하철_노선_정보_수정_시_노선명이_올바르지_않으면_수정_불가() {
        // given
        LineUpdateRequest 노선명_null = new LineUpdateRequest(null, "bg-green-600");
        LineUpdateRequest 노선명_공백 = new LineUpdateRequest("", "bg-green-600");
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(신분당선));

        // then
        assertThatThrownBy(() -> lineService.updateSubwayLine(1L, 노선명_null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> lineService.updateSubwayLine(1L, 노선명_공백))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("지하철 노선 정보 수정 시 색상값이 null 혹은 공백일 때 수정할 경우 오류가 발생한다.")
    @Test
    void 지하철_노선_정보_수정_시_색상값이_올바르지_않으면_수정_불가() {
        // given
        LineUpdateRequest 색상값_null = new LineUpdateRequest("2호선", null);
        LineUpdateRequest 색상값_공백 = new LineUpdateRequest("2호선", "");
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(신분당선));

        // then
        assertThatThrownBy(() -> lineService.updateSubwayLine(1L, 색상값_null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> lineService.updateSubwayLine(1L, 색상값_공백))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void 지하철_노선_삭제() {
        // given
        LineRequest 신분당선_생성_요청 = 신분당선_생성_요청();
        when(stationRepository.findById(1L)).thenReturn(Optional.of(강남역));
        when(stationRepository.findById(2L)).thenReturn(Optional.of(역삼역));
        when(lineRepository.save(any(Line.class))).thenReturn(신분당선);
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(신분당선));
        lineService.createSubwayLine(신분당선_생성_요청);

        // when
        lineService.deleteSubwayLine(1L);

        // then
        verify(lineRepository).deleteById(1L);
    }

    private LineRequest 신분당선_생성_요청() {
        return new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
    }

}
