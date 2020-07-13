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
        StationResponse stationResponse1 = createStationResponse(1L, "교대역");
        StationResponse stationResponse2 = createStationResponse(2L, "강남역");
        StationResponse stationResponse3 = createStationResponse(3L, "양재역");
        StationResponse stationResponse4 = createStationResponse(4L, "남부터미널역");

        LineStationResponse lineStationResponse1 = createLineStationResponse(stationResponse1, null);
        LineStationResponse lineStationResponse2 = createLineStationResponse(stationResponse2, 1L);

        LineStationResponse lineStationResponse3 = createLineStationResponse(stationResponse2, null);
        LineStationResponse lineStationResponse4 = createLineStationResponse(stationResponse3, 2L);

        LineStationResponse lineStationResponse5 = createLineStationResponse(stationResponse1, null);
        LineStationResponse lineStationResponse6 = createLineStationResponse(stationResponse4, 1L);
        LineStationResponse lineStationResponse7 = createLineStationResponse(stationResponse3, 4L);

        LineResponse lineResponse1 = createLineResponse(1L, "2호선", "GREEN", Lists.list(lineStationResponse1, lineStationResponse2));
        LineResponse lineResponse2 = createLineResponse(2L, "신분당선", "RED", Lists.list(lineStationResponse3, lineStationResponse4));
        LineResponse lineResponse3 = createLineResponse(3L, "3호선", "ORANGE", Lists.list(lineStationResponse5, lineStationResponse6, lineStationResponse7));

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
        assertThat(maps.getLines()).isEmpty();
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
        assertThat(maps.getLines()).hasSize(3)
                .containsExactlyElementsOf(lines);
    }

    private LineResponse createLineResponse(long id, String name, String color, List<LineStationResponse> lineStationResponses) {
        return new LineResponse(id, name, color, LocalTime.now(), LocalTime.now(), 5, lineStationResponses, LocalDateTime.now(), LocalDateTime.now());
    }

    private LineStationResponse createLineStationResponse(StationResponse stationResponse, Long preStationId) {
        return new LineStationResponse(stationResponse, preStationId, 2, 2);
    }

    private StationResponse createStationResponse(long id, String stationName) {
        return new StationResponse(id, stationName, LocalDateTime.now(), LocalDateTime.now());
    }

}