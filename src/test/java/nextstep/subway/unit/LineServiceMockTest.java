package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.CustomException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;

    @Mock
    private StationService stationService;

    @InjectMocks
    private LineService lineService;

    private final Station 강남역 = new Station(1L, "강남역");
    private final Station 역삼역 = new Station(2L, "역삼역");

    private final Station 선릉역 = new Station(3L, "역삼역");

    private final Line 이호선 = new Line(1L, "이호선", "green");

    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        when(stationService.findById(강남역.getId())).thenReturn(강남역);
        when(stationService.findById(역삼역.getId())).thenReturn(역삼역);
        when(lineRepository.findById(이호선.getId())).thenReturn(Optional.of(이호선));
        SectionRequest sectionRequest = new SectionRequest(강남역.getId(), 역삼역.getId(), 10);

        // when
        // lineService.addSection 호출
        lineService.addSection(이호선.getId(), sectionRequest);

        // then
        // line.findLineById 메서드를 통해 검증
        Line line = lineRepository.findById(이호선.getId()).get();
        assertAll(() -> {
            assertThat(line.getSections()).hasSize(1);
            assertThat(line.getStations()).containsExactlyElementsOf(List.of(강남역, 역삼역));
        });
    }

    @Test
    void saveLine() {
        //given
        LineRequest lineRequest = new LineRequest("이호선", "green", 강남역.getId(), 역삼역.getId(), 10);
        when(lineRepository.save(any(Line.class))).thenReturn(이호선);
        when(stationService.findById(강남역.getId())).thenReturn(강남역);
        when(stationService.findById(역삼역.getId())).thenReturn(역삼역);

        //when
        LineResponse response = lineService.saveLine(lineRequest);

        //then
        verify(lineRepository, times(1)).save(any());
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(이호선.getId());
    }

    @Test
    void findById() {
        //given
        when(lineRepository.findById(이호선.getId())).thenReturn(Optional.of(이호선));

        //when
        LineResponse response = lineService.findById(이호선.getId());

        //then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(이호선.getId());
    }

    @Test
    void showLine() {
        //given
        when(lineRepository.findAll()).thenReturn(List.of(이호선));

        //when
        List<LineResponse> lines = lineService.showLines();

        //then
        assertThat(lines).hasSize(1);
    }

    @Test
    void deleteLine() {
        //when
        lineService.deleteLine(이호선.getId());

        //then
        verify(lineRepository, times(1)).deleteById(이호선.getId());
    }

    @Test
    void deleteSection_When_마지막_역을_제거하면_Then_마지막_구간제거() {
        // given
        // 강남역 --- 역삼역 --- (선릉역)
        when(lineRepository.findById(이호선.getId())).thenReturn(Optional.of(이호선));

        when(stationService.findById(강남역.getId())).thenReturn(강남역);
        when(stationService.findById(역삼역.getId())).thenReturn(역삼역);
        when(stationService.findById(선릉역.getId())).thenReturn(선릉역);

        SectionRequest 강남역_역삼역 = new SectionRequest(강남역.getId(), 역삼역.getId(), 15);
        SectionRequest 역삼역_선릉역 = new SectionRequest(역삼역.getId(), 선릉역.getId(), 15);

        lineService.addSection(이호선.getId(), 강남역_역삼역);
        lineService.addSection(이호선.getId(), 역삼역_선릉역);

        //when
        lineService.deleteSection(이호선.getId(), 선릉역.getId());

        //then
        Line line = lineRepository.findById(이호선.getId()).get();
        assertAll(() -> {
            assertThat(line.getSections()).hasSize(1);
            assertThat(line.getStations()).containsExactlyElementsOf(List.of(강남역, 역삼역));
        });
    }

    @Test
    void deleteSection_When_중간_역을_제거하면_Then_중간_구간제거() {
        // given
        // 강남역 --- (역삼역) --- 선릉역
        when(lineRepository.findById(이호선.getId())).thenReturn(Optional.of(이호선));

        when(stationService.findById(강남역.getId())).thenReturn(강남역);
        when(stationService.findById(역삼역.getId())).thenReturn(역삼역);
        when(stationService.findById(선릉역.getId())).thenReturn(선릉역);

        SectionRequest 강남역_역삼역 = new SectionRequest(강남역.getId(), 역삼역.getId(), 15);
        SectionRequest 역삼역_선릉역 = new SectionRequest(역삼역.getId(), 선릉역.getId(), 15);

        lineService.addSection(이호선.getId(), 강남역_역삼역);
        lineService.addSection(이호선.getId(), 역삼역_선릉역);

        //when
        lineService.deleteSection(이호선.getId(), 역삼역.getId());

        //then
        Line line = lineRepository.findById(이호선.getId()).get();
        assertAll(() -> {
            assertThat(line.getSections()).hasSize(1);
            assertThat(line.getStations()).containsExactlyElementsOf(List.of(강남역, 선릉역));
            assertThat(line.getSections().get(0).getDistance()).isEqualTo(30);
        });
    }

    @Test
    void deleteSection_Given_노선에_구간이_하나뿐일때_When_구간을_제거하면_Then_ThrowException() {
        // given
        // 강남역 --- 역삼역
        when(lineRepository.findById(이호선.getId())).thenReturn(Optional.of(이호선));

        when(stationService.findById(강남역.getId())).thenReturn(강남역);
        when(stationService.findById(역삼역.getId())).thenReturn(역삼역);

        SectionRequest 강남역_역삼역 = new SectionRequest(강남역.getId(), 역삼역.getId(), 15);
        lineService.addSection(이호선.getId(), 강남역_역삼역);

        //when
        assertThatThrownBy(() -> lineService.deleteSection(이호선.getId(), 역삼역.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(CustomException.LINE_HAS_SECTION_AT_LEAST_ONE);
    }
}
