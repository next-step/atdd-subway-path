package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static nextstep.subway.utils.LineFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    private LineService lineService;

    @BeforeEach
    void setUp() {
        lineService = new LineService(lineRepository, stationService);
    }

    @Test
    @DisplayName("역 목록을 보여준다.")
    void showLines() {
        // when
        when(lineRepository.findAll()).thenReturn(List.of(신분당선));
        List<LineResponse> responses = lineService.showLines();

        // then
        assertThat(responses).hasSize(1);
    }

    @Test
    @DisplayName("식별자를 이용해 노선을 조회한다.")
    void findById() {
        // when
        when(lineRepository.findById(1L)).thenReturn(Optional.of(신분당선));

        // then
        lineService.findById(1L);
    }

    @Test
    @DisplayName("식별자로 노선을 조회하지 못하는 경우 예외가 발생한다.")
    void findByIdValidation1() {
        // when
        when(lineRepository.findById(1L)).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(
            () -> lineService.findById(1L)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("노선의 이름과 색을 수정한다.")
    void updateLine() {
        // given
        LineRequest lineRequest = new LineRequest("분당선", "green", null, null, 0);

        // when
        when(lineRepository.findById(1L)).thenReturn(Optional.of(신분당선));
        lineService.updateLine(1L, lineRequest);
        assertThat(신분당선.getName()).isEqualTo("분당선");
        assertThat(신분당선.getColor()).isEqualTo("green");
    }

    @Test
    @DisplayName("노선의 이름과 색을 수정할 때, 식별자로 노선을 조회하지 못하는 경우 예외가 발생한다.")
    void updateLineValidation1() {
        // given
        LineRequest lineRequest = new LineRequest("분당선", "green", null, null, 0);

        // when
        when(lineRepository.findById(1L)).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(
            () -> lineService.updateLine(1L, lineRequest)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("노선을 삭제한다.")
    void deleteLine() {
        // given
        long id = 1L;

        // when
        lineService.deleteLine(id);

        // then
        verify(lineRepository, atLeast(1)).deleteById(id);
    }

    @Test
    @DisplayName("구간을 추가한다.")
    void addSection() {
        // given
        SectionRequest 역삼역_잠실역 = new SectionRequest(역삼역_ID, 잠실역_ID, 10);

        // when
        when(stationService.findById(역삼역_ID)).thenReturn(역삼역);
        when(stationService.findById(잠실역_ID)).thenReturn(잠실역);
        when(lineRepository.findById(신분당선_ID)).thenReturn(Optional.of(신분당선));
        lineService.addSection(신분당선_ID, 역삼역_잠실역);

        // then
        assertThat(신분당선.getSections()).hasSize(2);
    }


    @Test
    @DisplayName("구간을 삭제한다.")
    void deleteSection() {
        // given
        Line 신분당선 = new Line(신분당선_ID, "신분당선", "red", 강남역_역삼역);
        신분당선.addSection(역삼역_잠실역);

        // when
        when(lineRepository.findById(신분당선_ID)).thenReturn(Optional.of(신분당선));

        // then
        lineService.deleteSection(신분당선_ID, 잠실역_ID);
    }

}
