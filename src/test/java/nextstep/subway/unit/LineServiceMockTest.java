package nextstep.subway.unit;

import nextstep.subway.application.LineService;
import nextstep.subway.application.dto.LineResponse;
import nextstep.subway.application.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.application.StationService;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    private Station 강남역;
    private Station 선릉역;
    private Station 삼성역;
    private Line 강남_선릉_노선;

    @BeforeEach
    void init() {
        강남역 = new Station(1L, "강남역");
        선릉역 = new Station(2L, "선릉역");
        삼성역 = new Station(3L, "삼성역");
        강남_선릉_노선 = new Line(1L, "노선", "red", 강남역, 선릉역, 10);
    }

    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        when(lineRepository.findById(1L)).thenReturn(Optional.of(강남_선릉_노선));
        when(stationService.findStationById(2L)).thenReturn(선릉역);
        when(stationService.findStationById(3L)).thenReturn(삼성역);
        when(lineRepository.findByIdFetchJoin(강남_선릉_노선.getId())).thenReturn(Optional.of(강남_선릉_노선));
        final LineService lineService = new LineService(lineRepository, stationService);

        // when
        // lineService.addSection 호출
        lineService.addSection(강남_선릉_노선.getId(), new SectionRequest(선릉역.getId(), 삼성역.getId(), 10));

        // then
        // lineService.findLineById 메서드를 통해 검증
        final LineResponse lineResponse = lineService.findLineById(강남_선릉_노선.getId());
        assertThat(lineResponse.getStations()).hasSize(3);
        assertThat(lineResponse.getStations().get(0).getName()).isEqualTo("강남역");
        assertThat(lineResponse.getStations().get(1).getName()).isEqualTo("선릉역");
        assertThat(lineResponse.getStations().get(2).getName()).isEqualTo("삼성역");
    }

}
