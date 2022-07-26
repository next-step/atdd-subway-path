package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.*;
import nextstep.subway.domain.*;
import nextstep.subway.fake.FakeLineFactory;
import nextstep.subway.fake.FakeStationFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;
    @InjectMocks
    private LineService lineService;
    private long 강남역_ID;
    private long 선릉역_ID;
    private long 신분당선_ID;

    @BeforeEach
    void setUp() {
        강남역_ID = 1L;
        선릉역_ID = 2L;
        신분당선_ID = 1L;
    }

    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        when(stationService.findById(강남역_ID)).thenReturn(FakeStationFactory.강남역());
        when(stationService.findById(선릉역_ID)).thenReturn(FakeStationFactory.선릉역());
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(FakeLineFactory.신분당선()));

        // when
        // lineService.addSection 호출
        SectionRequest sectionRequest = new SectionRequest(
                FakeStationFactory.강남역().getId(),
                FakeStationFactory.선릉역().getId(),
                10);
        lineService.addSection(신분당선_ID, sectionRequest);

        // then
        // line.findLineById 메서드를 통해 검증
        LineResponse findLineResponse = lineService.findById(신분당선_ID);
        assertThat(findLineResponse.getStations()).hasSize(2);
    }

    @Test
    void 라인_등록_검증() {
        //given
        Line 신분당선 = FakeLineFactory.신분당선();
        when(lineRepository.save(any())).thenReturn(신분당선);

        //when
        LineRequest lineRequest = new LineRequest(신분당선.getName(), 신분당선.getColor());
        LineResponse lineResponse = lineService.saveLine(lineRequest);

        //then
        assertAll(
                () -> assertThat(lineResponse.getName()).isEqualTo("신분당선"),
                () -> assertThat(lineResponse).isNotNull()
        );
    }

    @Test
    void 라인_수정_검증() {
        //given
        Line 신분당선 = FakeLineFactory.신분당선();
        Line 분당선 = FakeLineFactory.분당선();
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(신분당선));

        //when
        lineService.updateLine(신분당선_ID, new LineRequest(분당선.getName(), 분당선.getColor()));

        //then
        LineResponse lineResponse = lineService.findById(신분당선_ID);
        assertAll(
                () -> assertThat(lineResponse.getName()).isEqualTo("분당선"),
                () -> assertThat(lineResponse.getColor()).isEqualTo("yellow")
        );
    }

    @Test
    void 라인_삭제_검증() {
        //when
        lineService.deleteLine(신분당선_ID);

        //then
        verify(lineRepository).deleteById(신분당선_ID);
    }
}
