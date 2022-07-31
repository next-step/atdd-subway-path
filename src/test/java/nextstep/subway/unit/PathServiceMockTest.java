package nextstep.subway.unit;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.request.PathRequest;
import nextstep.subway.applicaion.dto.response.PathResponse;
import nextstep.subway.applicaion.dto.response.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.enums.SubwayErrorMessage;
import nextstep.subway.fake.FakeLineFactory;
import nextstep.subway.fake.FakeStationFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PathServiceMockTest {

    @Mock
    LineRepository lineRepository;

    @Mock
    StationRepository stationRepository;

    @InjectMocks
    PathService pathService;

    Station 강남역;
    Station 선릉역;
    Station 왕십리역;
    Station 역삼역;
    Station 신촌역;
    Station 영통역;
    Section 강남_선릉_10;
    Section 선릉_왕십리_10;
    Section 선릉_왕십리_5;
    Section 왕십리_역삼_5;
    Section 신촌_영통_5;
    Line 신분당선;
    Line 분당선;
    Line 경의중앙선;

    @BeforeEach
    void setUp() {
        강남역 = FakeStationFactory.강남역();
        선릉역 = FakeStationFactory.선릉역();
        왕십리역 = FakeStationFactory.왕십리역();
        역삼역 =  FakeStationFactory.역삼역();
        신촌역 =  FakeStationFactory.신촌역();
        영통역 =  FakeStationFactory.영통역();

        신분당선 = FakeLineFactory.신분당선();
        분당선 = FakeLineFactory.분당선();
        경의중앙선 = FakeLineFactory.경의중앙선();

        강남_선릉_10 = new Section(1L, 분당선, 강남역, 선릉역,2);
        선릉_왕십리_10 = new Section(2L, 분당선, 선릉역,  왕십리역, 10);
        선릉_왕십리_5 = new Section(3L, 신분당선, 선릉역, 왕십리역, 5);
        왕십리_역삼_5 = new Section(4L, 경의중앙선, 왕십리역, 역삼역, 5);
        신촌_영통_5 = new Section(5L, 경의중앙선, 신촌역, 영통역, 5);


        신분당선.addSection(강남_선릉_10);
        신분당선.addSection(선릉_왕십리_10);
        분당선.addSection(선릉_왕십리_5);
        분당선.addSection(왕십리_역삼_5);
        경의중앙선.addSection(신촌_영통_5);
    }

    @Test
    void 최단_경로_조회() {
        //given
        //필요한 것 = 전체 지하철역, 전체 구간
        when(stationRepository.findById(강남역.getId())).thenReturn(Optional.of(강남역));
        when(stationRepository.findById(왕십리역.getId())).thenReturn(Optional.of(왕십리역));
        when(stationRepository.findAll()).thenReturn(List.of(강남역, 선릉역, 왕십리역, 역삼역));
        when(lineRepository.findAll()).thenReturn(List.of(신분당선, 분당선));


        //when
        PathRequest pathRequest = new PathRequest(강남역.getId(), 왕십리역.getId());
        PathResponse pathResponse = pathService.findShortPath(pathRequest);

        //then
        최단경로와_최단거리_조회_검증(pathResponse);
    }

    @Test
    void 경로가_연결되지_않았을_경우() {
        //given
        //필요한 것 = 전체 지하철역, 전체 구간
        when(stationRepository.findById(강남역.getId())).thenReturn(Optional.of(강남역));
        when(stationRepository.findById(영통역.getId())).thenReturn(Optional.of(영통역));
        when(stationRepository.findAll()).thenReturn(List.of(강남역, 선릉역, 왕십리역, 신촌역, 영통역, 역삼역));
        when(lineRepository.findAll()).thenReturn(List.of(신분당선, 분당선, 경의중앙선));

        //when
        PathRequest pathRequest = new PathRequest(강남역.getId(), 영통역.getId());

        //then
        경로가_이어져있지_않을_경우_검증(pathRequest);
    }

    @Test
    void 목적지와_출발지가_동일할_경우() {
        //given
        //필요한 것 = 전체 지하철역, 전체 구간
        when(stationRepository.findById(강남역.getId())).thenReturn(Optional.of(강남역));
        when(stationRepository.findAll()).thenReturn(List.of(강남역, 선릉역, 왕십리역));
        when(lineRepository.findAll()).thenReturn(List.of(신분당선));

        //when
        PathRequest pathRequest = new PathRequest(FakeStationFactory.강남역().getId(), FakeStationFactory.강남역().getId());

        //then
        출발지와_목적지가_동일한_경우_검증(pathRequest);
    }

    private void 출발지와_목적지가_동일한_경우_검증(PathRequest pathRequest) {
        assertThatThrownBy(() -> pathService.findShortPath(pathRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(SubwayErrorMessage.SAME_SOURCE_AND_DESTINATION.getMessage());
    }

    private void 경로가_이어져있지_않을_경우_검증(PathRequest pathRequest) {
        assertThatThrownBy(() -> pathService.findShortPath(pathRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(SubwayErrorMessage.UNCONNECTED_PATH.getMessage());
    }

    private void 최단경로와_최단거리_조회_검증(PathResponse response) {
        assertThat(response.getStations()
                          .stream()
                          .map(StationResponse::getName)
                          .collect(Collectors.toList()))
                .containsExactly("강남역", "선릉역", "왕십리역");
        assertThat(response.getDistance()).isEqualTo(7);
    }
}
