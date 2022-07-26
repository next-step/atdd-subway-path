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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @InjectMocks
    private LineService lineService;

    private Line 이호선;
    private Station 서울대입구역;
    private Station 낙성대역;
    private Station 사당역;

    @BeforeEach
    void setUp() {
        이호선 = new Line(1L,"이호선", "br-red-600");
        서울대입구역 = new Station(1L,"서울대입구역");
        낙성대역 = new Station(2L,"낙성대역");
        사당역 = new Station(3L, "사당역");
    }

    @DisplayName("지하철 구간을 등록합니다.")
    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        given(lineRepository.findById(이호선.getId())).willReturn(Optional.of(이호선));
        given(stationService.findById(서울대입구역.getId())).willReturn(서울대입구역);
        given(stationService.findById(낙성대역.getId())).willReturn(낙성대역);

        // when
        // lineService.addSection 호출
        lineService.addSection(이호선.getId(), new SectionRequest(서울대입구역.getId(),낙성대역.getId(),10));

        // then
        // line.findLineById 메서드를 통해 검증
        LineResponse 이호선_반환값 = lineService.findById(이호선.getId());
        LineResponse 이호선_예상값 = createLineResponse(이호선, List.of(서울대입구역, 낙성대역));
        assertThat(이호선_반환값).isEqualTo(이호선_예상값);
    }

    @DisplayName("지하철 구간을 삭제합니다.")
    @Test
    void deleteSection() {
        // Given
        given(lineRepository.findById(이호선.getId())).willReturn(Optional.of(이호선));
        given(stationService.findById(사당역.getId())).willReturn(사당역);

        이호선.addSection(new Section(이호선, 서울대입구역, 낙성대역, 10));
        이호선.addSection(new Section(이호선, 낙성대역, 사당역, 10));

        // When
        lineService.deleteSection(이호선.getId(), 사당역.getId());

        // Then
        LineResponse 이호선_반환값 = lineService.findById(이호선.getId());
        LineResponse 이호선_예상값 = createLineResponse(이호선, List.of(서울대입구역, 낙성대역));
        assertThat(이호선_반환값).isEqualTo(이호선_예상값);
    }

    private LineResponse createLineResponse(Line line, List<Station> stations) {
        LineResponse lineResponse = new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                createStationResponses(stations)
        );
        return lineResponse;
    }

    private List<StationResponse> createStationResponses(List<Station> stations) {
        List<StationResponse> collect = stations.stream()
                .map(it -> new StationResponse(it.getId(), it.getName()))
                .collect(Collectors.toList());
        return collect;
    }
}
