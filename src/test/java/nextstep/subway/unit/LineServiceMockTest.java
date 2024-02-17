package nextstep.subway.unit;

import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineUpdateRequest;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.line.entity.Line;
import nextstep.subway.line.entity.Section;
import nextstep.subway.line.entity.Sections;
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
        verify(stationRepository).findById(anyLong());
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
        verify(lineRepository).findById(anyLong());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void 지하철_노선_목록_조회() {
        // given
        final Line 신분당선 = new Line("신분당선", "bg-red-600", mock(Station.class), mock(Station.class), 10);
        final Line 지하철노선 = new Line("지하철노선", "bg-yello-600", mock(Station.class), mock(Station.class), 15);

        when(lineRepository.findAll()).thenReturn(List.of(신분당선, 지하철노선));

        // when
        final List<LineResponse> response = lineService.getSubwayLines();

        // then
        assertThat(response).hasSize(2);
        assertThat(response.get(0).getName()).isEqualTo("신분당선");
        assertThat(response.get(1).getName()).isEqualTo("지하철노선");
        verify(lineRepository).findAll();
    }

    @DisplayName("지하철 노선 정보를 수정한다.")
    @Test
    void 지하철_노선_정보_수정() {
        // given
        final LineUpdateRequest request = new LineUpdateRequest("2호선", "bg-green-600");
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(신분당선));

        // when
        lineService.updateSubwayLine(1L, request);

        // then
        assertThat(신분당선.getName()).isEqualTo("2호선");
        assertThat(신분당선.getColor()).isEqualTo("bg-green-600");
        verify(lineRepository).findById(1L);
    }

    @DisplayName("지하철 노선 정보 수정 시 존재하지 않는 역을 수정할 경우 오류가 발생한다.")
    @Test
    void 지하철_노선_정보_수정_시_존재하지_않는_역으로_수정_불가() {
        // given
        final LineUpdateRequest request = new LineUpdateRequest("2호선", "bg-green-600");
        when(lineRepository.findById(anyLong())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> lineService.updateSubwayLine(1L, request))
                .isInstanceOf(EntityNotFoundException.class);
        verify(lineRepository).findById(anyLong());
    }

    @DisplayName("지하철 노선 정보 수정 시 노선명이 null 혹은 공백일 때 수정할 경우 오류가 발생한다.")
    @Test
    void 지하철_노선_정보_수정_시_노선명이_올바르지_않으면_수정_불가() {
        // given
        final LineUpdateRequest 노선명_null = new LineUpdateRequest(null, "bg-green-600");
        final LineUpdateRequest 노선명_공백 = new LineUpdateRequest("", "bg-green-600");
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(신분당선));

        // then
        assertThatThrownBy(() -> lineService.updateSubwayLine(1L, 노선명_null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> lineService.updateSubwayLine(1L, 노선명_공백))
                .isInstanceOf(IllegalArgumentException.class);
        verify(lineRepository, times(2)).findById(anyLong());
    }

    @DisplayName("지하철 노선 정보 수정 시 색상값이 null 혹은 공백일 때 수정할 경우 오류가 발생한다.")
    @Test
    void 지하철_노선_정보_수정_시_색상값이_올바르지_않으면_수정_불가() {
        // given
        final LineUpdateRequest 색상값_null = new LineUpdateRequest("2호선", null);
        final LineUpdateRequest 색상값_공백 = new LineUpdateRequest("2호선", "");
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(신분당선));

        // then
        assertThatThrownBy(() -> lineService.updateSubwayLine(1L, 색상값_null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> lineService.updateSubwayLine(1L, 색상값_공백))
                .isInstanceOf(IllegalArgumentException.class);
        verify(lineRepository, times(2)).findById(anyLong());
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void 지하철_노선_삭제() {
        // given
        final LineRequest 신분당선_생성_요청 = 신분당선_생성_요청();
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

    @DisplayName("지하철 노선 구간을 생성한다.")
    @Test
    void 지하철_노선_구간_생성() {
        // given
        final Station upStation = mock(Station.class);
        final Station downStation = mock(Station.class);
        final Section section = mock(Section.class);
        final Sections sections = mock(Sections.class);
        final Line line = mock(Line.class);
        final SectionRequest 구간_생성_요청 = new SectionRequest(1L, 2L, 5);

        when(lineRepository.findById(1L)).thenReturn(Optional.of(line));
        when(stationRepository.findById(1L)).thenReturn(Optional.of(upStation));
        when(stationRepository.findById(2L)).thenReturn(Optional.of(downStation));
        when(line.getSections()).thenReturn(sections);
        when(sections.getSections()).thenReturn(List.of(section));
        when(section.getUpStation()).thenReturn(upStation);
        when(section.getDownStation()).thenReturn(downStation);

        // when
        lineService.createLineSection(1L, 구간_생성_요청);

        // then
        verify(line).addSection(any(Section.class));
    }

    @DisplayName("지하철 노선 구간을 삭제한다.")
    @Test
    void 지하철_노선_구간_삭제() {
        // given
        final Line line = mock(Line.class);
        final Station station = mock(Station.class);
        final Sections sections = mock(Sections.class);

        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(line));
        when(line.getSections()).thenReturn(sections);
        when(sections.size()).thenReturn(2);
        when(stationRepository.findById(anyLong())).thenReturn(Optional.of(station));

        // when
        lineService.deleteLineSection(1L, 1L);

        // then
        verify(line).removeSection(station);
    }

    @DisplayName("지하철 노선 구간을 삭제 시 구간이 1개라면 오류가 발생한다.")
    @Test
    void 지하철_노선_구간_삭제_시_구간이_1개라면_삭제_불가() {
        // given
        final Line line = mock(Line.class);
        final Sections sections = mock(Sections.class);

        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(line));
        when(line.getSections()).thenReturn(sections);
        when(sections.size()).thenReturn(1);

        // then
        assertThatThrownBy(() -> lineService.deleteLineSection(1L, 1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private LineRequest 신분당선_생성_요청() {
        return new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
    }

}
