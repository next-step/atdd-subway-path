package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.*;
import nextstep.subway.domain.*;
import nextstep.subway.fake.FakeStationFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
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
        when(stationService.findById(1L)).thenReturn(FakeStationFactory.강남역);
        when(stationService.findById(2L)).thenReturn(FakeStationFactory.선릉역);
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(new Line( "신분당선", "blue")));

        // when
        // lineService.addSection 호출
        SectionRequest sectionRequest = new SectionRequest(
                FakeStationFactory.강남역.getId(),
                FakeStationFactory.선릉역.getId(),
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

        //when

        //then
    }

    @Test
    void 라인_수정_검증() {
        //given

        //when

        //then
    }

    @Test
    void 라인_삭제_검증() {
        //given

        //when

        //then
    }
}
