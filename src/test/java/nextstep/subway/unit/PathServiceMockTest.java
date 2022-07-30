package nextstep.subway.unit;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.SectionService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.PathDto;
import nextstep.subway.applicaion.dto.SectionDto;
import nextstep.subway.applicaion.dto.StationDto;
import nextstep.subway.applicaion.dto.request.PathRequest;
import nextstep.subway.domain.*;
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
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PathServiceMockTest {

    @Mock
    SectionService sectionService;

    @Mock
    StationService stationService;

    @InjectMocks
    PathService pathService;

    StationDto 강남역;
    StationDto 선릉역;
    StationDto 왕십리역;
    StationDto 역삼역;
    SectionDto 강남_선릉_10;
    SectionDto 선릉_왕십리_10;
    SectionDto 선릉_왕십리_5;
    SectionDto 역삼_왕십리_5;

    @BeforeEach
    void setUp() {
        강남역 = new StationDto(1L, "강남역");
        선릉역 = new StationDto(2L, "선릉역");
        왕십리역 = new StationDto(7L, "왕십리역");
        역삼역 = new StationDto(6L, "역삼역");
        강남_선릉_10 = new SectionDto(1L, FakeLineFactory.분당선(), FakeStationFactory.강남역(), FakeStationFactory.선릉역(),2);
        선릉_왕십리_10 = new SectionDto(2L, FakeLineFactory.분당선(), FakeStationFactory.선릉역(),  FakeStationFactory.왕십리역(), 10);
        선릉_왕십리_5 = new SectionDto(3L, FakeLineFactory.신분당선(), FakeStationFactory.선릉역(), FakeStationFactory.왕십리역(), 5);
        역삼_왕십리_5 = new SectionDto(4L, FakeLineFactory.경의중앙선(), FakeStationFactory.역삼역(), FakeStationFactory.왕십리역(), 5);
    }

    @Test
    void 최단_경로_조회() {
        //given
        //필요한 것 = 전체 지하철역, 전체 구간
        when(stationService.findById(FakeStationFactory.강남역().getId())).thenReturn(FakeStationFactory.강남역());
        when(stationService.findById(FakeStationFactory.왕십리역().getId())).thenReturn(FakeStationFactory.왕십리역());
        when(stationService.findAllStations()).thenReturn(List.of(강남역, 선릉역, 왕십리역));
        when(sectionService.findAll()).thenReturn(List.of(강남_선릉_10, 선릉_왕십리_10, 선릉_왕십리_5));

        //when
        PathRequest pathRequest = new PathRequest(FakeStationFactory.강남역().getId(), FakeStationFactory.왕십리역().getId());
        PathDto pathDto = pathService.findShortPath(pathRequest);

        //then
        최단경로와_최단거리_조회_검증(pathDto);
    }

    @Test
    void 경로가_연결되지_않았을_경우() {
        //given
        //필요한 것 = 전체 지하철역, 전체 구간
        when(stationService.findById(FakeStationFactory.강남역().getId())).thenReturn(FakeStationFactory.강남역());
        when(stationService.findById(FakeStationFactory.역삼역().getId())).thenReturn(FakeStationFactory.역삼역());
        when(stationService.findAllStations()).thenReturn(List.of(강남역, 역삼역, 선릉역,왕십리역));
        when(sectionService.findAll()).thenReturn(List.of(강남_선릉_10, 역삼_왕십리_5));

        //when
        PathRequest pathRequest = new PathRequest(FakeStationFactory.강남역().getId(), FakeStationFactory.역삼역().getId());

        //then
        경로가_이어져있지_않을_경우_검증(pathRequest);
    }

    @Test
    void 목적지와_출발지가_동일할_경우() {
        //given
        //필요한 것 = 전체 지하철역, 전체 구간
        when(stationService.findById(FakeStationFactory.강남역().getId())).thenReturn(FakeStationFactory.강남역());
        when(stationService.findAllStations()).thenReturn(List.of(강남역, 선릉역, 왕십리역));
        when(sectionService.findAll()).thenReturn(List.of(강남_선릉_10, 선릉_왕십리_10, 선릉_왕십리_5));

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

    private void 최단경로와_최단거리_조회_검증(PathDto pathDto) {
        assertThat(pathDto.getShortestPath()
                          .stream()
                          .map(Station::getName)
                          .collect(Collectors.toList()))
                .containsExactly("강남역", "선릉역", "왕십리역");
        assertThat(pathDto.getShortestDistance()).isEqualTo(7);
    }
}
