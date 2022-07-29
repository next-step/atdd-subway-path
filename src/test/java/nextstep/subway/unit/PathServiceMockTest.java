package nextstep.subway.unit;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.SectionService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionDto;
import nextstep.subway.applicaion.dto.StationDto;
import nextstep.subway.applicaion.dto.request.PathRequest;
import nextstep.subway.applicaion.dto.response.PathResponse;
import nextstep.subway.applicaion.dto.response.StationResponse;
import nextstep.subway.domain.*;
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
import static org.mockito.ArgumentMatchers.any;
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
    SectionDto 강남_선릉_10;
    SectionDto 선릉_왕십리_10;
    SectionDto 선릉_왕십리_5;

    @BeforeEach
    void setUp() {
        강남역 = new StationDto(1L, "강남역");
        선릉역 = new StationDto(2L, "선릉역");
        왕십리역 = new StationDto(7L, "왕십리역");
        강남_선릉_10 = new SectionDto(1L, FakeLineFactory.분당선(), FakeStationFactory.강남역(), FakeStationFactory.선릉역(),2);
        선릉_왕십리_10 = new SectionDto(2L, FakeLineFactory.분당선(), FakeStationFactory.선릉역(),  FakeStationFactory.왕십리역(), 10);
        선릉_왕십리_5 = new SectionDto(3L, FakeLineFactory.신분당선(), FakeStationFactory.선릉역(), FakeStationFactory.왕십리역(), 5);
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
        PathResponse pathResponse = pathService.findShortPath(pathRequest);

        //then
        assertThat(pathResponse.getStations()
                               .stream()
                               .map(StationResponse::getName)
                               .collect(Collectors.toList()))
                .containsExactly("강남역", "선릉역", "왕십리역");
        assertThat(pathResponse.getDistance()).isEqualTo(7);
    }
}
