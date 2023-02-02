package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    private LineService lineService;

    private Line 분당선;
    private Line 신분당선;
    private Station 수서역;
    private Station 복정역;
    private Station 판교역;
    private Station 광교역;

    @BeforeEach
    void setup() {
        lineService = new LineService(lineRepository, stationService);

        분당선 = new Line(1L, "분당선", "yellow");
        신분당선 = new Line(2L, "신분당선", "red");
        수서역 = new Station(1L, "수서역");
        복정역 = new Station(2L, "복정역");
        판교역 = new Station(3L, "판교역");
        광교역 = new Station(4L, "광교역");
    }

    @DisplayName("새로운 지하철 구간을 등록한다.")
    @Test
    void addSection() {
        // given
        when(lineRepository.findById(분당선.getId())).thenReturn(Optional.of(분당선));
        when(stationService.findById(수서역.getId())).thenReturn(수서역);
        when(stationService.findById(복정역.getId())).thenReturn(복정역);

        // when
        lineService.addSection(분당선.getId(), new SectionRequest(수서역.getId(), 복정역.getId(), 5));

        // then
        assertThat(분당선.getSections()).hasSize(1);
    }

    @DisplayName("지하철 구간을 제거한다.")
    @Test
    void deleteSection() {
        // given
        when(lineRepository.findById(분당선.getId())).thenReturn(Optional.of(분당선));
        when(stationService.findById(수서역.getId())).thenReturn(수서역);
        when(stationService.findById(복정역.getId())).thenReturn(복정역);

        lineService.addSection(분당선.getId(), new SectionRequest(수서역.getId(), 복정역.getId(), 5));

        // when
        lineService.deleteSection(분당선.getId(), 복정역.getId());

        // then
        assertThat(분당선.getSections()).isEmpty();
    }

    @DisplayName("지하철 구간 제거 시, 전달한 역이 하행 종점역이 아니라면 예외가 발생한다.")
    @Test
    void cannotDeleteSection() {
        // given
        when(lineRepository.findById(분당선.getId())).thenReturn(Optional.of(분당선));
        when(stationService.findById(수서역.getId())).thenReturn(수서역);
        when(stationService.findById(복정역.getId())).thenReturn(복정역);

        // when
        lineService.addSection(분당선.getId(), new SectionRequest(수서역.getId(), 복정역.getId(), 5));

        // then
        assertThatThrownBy(() -> lineService.deleteSection(분당선.getId(), 수서역.getId()))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("새로운 지하철 노선을 등록한다.")
    @Test
    void saveLine() {
        // given
        when(lineRepository.save(any(Line.class))).thenReturn(신분당선);
        when(stationService.findById(판교역.getId())).thenReturn(판교역);
        when(stationService.findById(광교역.getId())).thenReturn(광교역);

        // when
        lineService.saveLine(new LineRequest(신분당선.getName(), 신분당선.getColor(), 판교역.getId(), 광교역.getId(), 20));

        // then
        assertThat(신분당선.getStations()).hasSize(2);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        when(lineRepository.findById(신분당선.getId())).thenReturn(Optional.of(신분당선));

        // when
        lineService.updateLine(신분당선.getId(), new LineRequest("구분당선", "blue", 판교역.getId(), 광교역.getId(), 20));

        // then
        assertThat(신분당선.getName()).isEqualTo("구분당선");
        assertThat(신분당선.getColor()).isEqualTo("blue");
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        doNothing().when(lineRepository).deleteById(신분당선.getId());
        when(lineRepository.findAll()).thenReturn(Collections.emptyList());

        // when
        lineService.deleteLine(신분당선.getId());

        // then
        assertThat(lineRepository.findAll()).isEmpty();
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void showLines() {
        // given
        when(lineRepository.findAll()).thenReturn(Collections.singletonList(분당선));

        // when
        List<LineResponse> response = lineService.showLines();

        // then
        assertThat(response).hasSize(1);
    }

    @DisplayName("식별자로 지하철 노선을 조회한다.")
    @Test
    void findById() {
        // given
        when(lineRepository.findById(분당선.getId())).thenReturn(Optional.of(분당선));

        // when
        LineResponse response = lineService.findById(분당선.getId());

        // then
        assertAll(
            () -> assertThat(response.getName()).isEqualTo("분당선"),
            () -> assertThat(response.getColor()).isEqualTo("yellow")
        );
    }

    @DisplayName("지하철 노선 조회 시, 식별자에 해당하는 노선이 없으면 예외가 발생한다")
    @Test
    void lineNotFound() {
        // when
        when(lineRepository.findById(999L)).thenThrow(IllegalArgumentException.class);

        // then
        assertThatThrownBy(() -> lineService.findById(999L))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
