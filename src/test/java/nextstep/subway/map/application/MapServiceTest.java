package nextstep.subway.map.application;

import nextstep.subway.line.application.LineService;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineStationResponse;
import nextstep.subway.map.dto.MapResponse;
import nextstep.subway.station.dto.StationResponse;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;


@DisplayName("노선도 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class MapServiceTest {

    @Mock
    private LineService lineService;
    private List<LineResponse> lines;
    private MapService mapService;

    @BeforeEach
    void setUp() {
        StationResponse stationResponse1 = new StationResponse(1L, "교대역", LocalDateTime.now(), LocalDateTime.now());
        StationResponse stationResponse2 = new StationResponse(2L, "강남역", LocalDateTime.now(), LocalDateTime.now());
        StationResponse stationResponse3 = new StationResponse(3L, "양재역", LocalDateTime.now(), LocalDateTime.now());
        StationResponse stationResponse4 = new StationResponse(4L, "남부터미널역", LocalDateTime.now(), LocalDateTime.now());

        LineStationResponse lineStationResponse1 = new LineStationResponse(stationResponse1, null, 2, 2);
        LineStationResponse lineStationResponse2 = new LineStationResponse(stationResponse2, 1L, 2, 2);

        LineStationResponse lineStationResponse3 = new LineStationResponse(stationResponse2, null, 2, 2);
        LineStationResponse lineStationResponse4 = new LineStationResponse(stationResponse3, 2L, 2, 1);

        LineStationResponse lineStationResponse5 = new LineStationResponse(stationResponse1, null, 2, 2);
        LineStationResponse lineStationResponse6 = new LineStationResponse(stationResponse4, 1L, 1, 2);
        LineStationResponse lineStationResponse7 = new LineStationResponse(stationResponse3, 4L, 2, 2);

        LineResponse lineResponse1 = new LineResponse(1L, "2호선", "GREEN", LocalTime.now(), LocalTime.now(), 5, Lists.newArrayList(lineStationResponse1, lineStationResponse2), LocalDateTime.now(), LocalDateTime.now());
        LineResponse lineResponse2 = new LineResponse(2L, "신분당선", "RED", LocalTime.now(), LocalTime.now(), 5, Lists.newArrayList(lineStationResponse3, lineStationResponse4), LocalDateTime.now(), LocalDateTime.now());
        LineResponse lineResponse3 = new LineResponse(3L, "3호선", "ORANGE", LocalTime.now(), LocalTime.now(), 5, Lists.newArrayList(lineStationResponse5, lineStationResponse6, lineStationResponse7), LocalDateTime.now(), LocalDateTime.now());

        lines = Lists.newArrayList(lineResponse1, lineResponse2, lineResponse3);

        mapService = new MapService(lineService);
    }

    @Test
    @DisplayName("노선도 조회시 등록된 노선도가 없으면 빈 리스트를 담은 객체를 리턴한다.")
    void getEmptyResult() {
        //given
        when(lineService.findAllLines()).thenReturn(Collections.emptyList());

        //when
        MapResponse maps = mapService.getMaps();

        //then
        assertThat(maps.getData()).isEmpty();
    }

    @Test
    @DisplayName("노선도 조회시 등록된 노선도를 응답한다")
    void getMaps() {
        //given
        when(lineService.findAllLines()).thenReturn(lines);
        when(lineService.findLineById(anyLong())).thenReturn(lines.get(0), lines.get(1), lines.get(2));

        //when
        MapResponse maps = mapService.getMaps();

        //then
        assertThat(maps.getData()).hasSize(3)
                .containsExactlyElementsOf(lines);

    }

}