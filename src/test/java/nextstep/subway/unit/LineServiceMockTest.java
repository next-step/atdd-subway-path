package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.*;
import nextstep.subway.domain.*;
import nextstep.subway.fake.FakeLineFactory;
import nextstep.subway.fake.FakeStationFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;
    @InjectMocks
    private LineService lineService;

    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        when(stationService.findById(1L)).thenReturn(FakeStationFactory.강남역());
        when(stationService.findById(2L)).thenReturn(FakeStationFactory.선릉역());
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(FakeLineFactory.신분당선()));

        // when
        // lineService.addSection 호출
        SectionRequest sectionRequest = new SectionRequest(
                FakeStationFactory.강남역().getId(),
                FakeStationFactory.선릉역().getId(),
                10);
        lineService.addSection(1L, sectionRequest);

        // then
        // line.findLineById 메서드를 통해 검증
        LineResponse findLineResponse = lineService.findById(1L);
        assertThat(findLineResponse.getStations()).hasSize(2);
    }

    @Test
    void 라인_등록_검증() {
        //given
        Line 신분당선 = FakeLineFactory.신분당선();
        when(lineRepository.save(신분당선)).thenReturn(신분당선);

        //when
        Line line = lineRepository.save(신분당선);

        //then
        assertAll(
                () -> assertThat(line.getName()).isEqualTo("신분당선"),
                () -> assertThat(line).isNotNull()
        );
    }

    @Test
    void 라인_수정_검증() {
        //given
        Line 신분당선 = FakeLineFactory.신분당선();
        Line 분당선 = FakeLineFactory.분당선();
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(신분당선));

        //when
        lineService.updateLine(1L, new LineRequest(분당선.getName(), 분당선.getColor()));

        //then
        LineResponse lineResponse = lineService.findById(1L);
        assertAll(
                () -> assertThat(lineResponse.getName()).isEqualTo("분당선"),
                () -> assertThat(lineResponse.getColor()).isEqualTo("yellow")
        );
    }

    @Test
    void 라인_삭제_검증() {
        //when
        lineRepository.deleteById(1L);

        //then
        verify(lineRepository).deleteById(1L);
    }
}
