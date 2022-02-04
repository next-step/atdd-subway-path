package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    private Station 사당역;
    private Station 방배역;
    private Line 이호선;
    private Section 사당_방배_구간;


    @BeforeEach
    void setUp() {
        사당역 = new Station("사당");
        방배역 = new Station("방배");
        이호선 = new Line("2호선", "green");
        사당_방배_구간 = new Section(사당역, 방배역, 5);
        이호선.addSection(사당_방배_구간);
    }

    @DisplayName("요청한 구간이 마지막 구간과 연결되면 구간 등록 성공")
    @Test
    void addSection() {
        // given
        int distance = 3;
        Station 서초역 = new Station("서초");
        Section 방배_서초_구간 = new Section(방배역, 서초역, distance);

        when(lineRepository.findById(any())).thenReturn(Optional.of(이호선));
        when(stationService.findById(1L)).thenReturn(방배역);
        when(stationService.findById(2L)).thenReturn(서초역);

        LineService lineService = new LineService(lineRepository, stationService);

        Long lineId = 1L;
        SectionRequest sectionRequest = new SectionRequest(1L, 2L, distance);

        // when
        // lineService.addSection 호출
        lineService.addSection(lineId, sectionRequest);

        // then
        // line.findLineById 메서드를 통해 검증
        LineResponse lineResponse = lineService.findLineById(1L);
        List<String> stationNameList = lineResponse.getStations().stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        assertThat(stationNameList).contains("서초");
    }


}
