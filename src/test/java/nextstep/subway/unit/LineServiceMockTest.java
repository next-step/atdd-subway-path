package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.Optional;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        LineService lineService = new LineService(lineRepository, stationService);

        Station 교대역 = new Station("교대역");
        ReflectionTestUtils.setField(교대역, "id", 1L);
        Station 강남역 = new Station("강남역");
        ReflectionTestUtils.setField(강남역, "id", 2L);

        Line 이호선 = new Line("2호선", "bg-green-600");
        ReflectionTestUtils.setField(이호선, "id", 1L);

        given(stationService.findById(교대역.getId())).willReturn(교대역);
        given(stationService.findById(강남역.getId())).willReturn(강남역);
        given(lineRepository.findById(이호선.getId())).willReturn(Optional.of(이호선));


        SectionRequest sectionRequest = new SectionRequest(교대역.getId(), 강남역.getId(), 10);

        // when
        // lineService.addSection 호출
        lineService.addSection(이호선.getId(), sectionRequest);

        // then
        // line.findLineById 메서드를 통해 검증
        LineResponse response = lineService.findById(이호선.getId());
        List<StationResponse> stations = response.getStations();

        assertThat(response.getName()).isEqualTo(이호선.getName());
        assertThat(stations.size()).isEqualTo(2);
    }
}
