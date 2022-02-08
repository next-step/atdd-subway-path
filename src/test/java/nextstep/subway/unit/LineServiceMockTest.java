package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;


    @DisplayName("구간 등록")
    @Test
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        LineService lineService = new LineService(lineRepository, stationService);
        Line line = new Line(1L,"간선", "blue");
        when(lineRepository.findById(1L)).thenReturn(Optional.of(line));
        Station 수원역 = new Station(1L, "수원역");
        Station 수원중앙역 = new Station(2L, "수원중앙역");
        when(stationService.findById(1L)).thenReturn(new Station(1L,"수원역"));
        when(stationService.findById(2L)).thenReturn(new Station(2L, "수원중앙역"));

        // when
        // lineService.addSection 호출
        SectionRequest sectionRequest = new SectionRequest(수원역.getId(), 수원중앙역.getId(), 10);
        lineService.addSection(line.getId(), sectionRequest);

        // then
        // line.findLineById 메서드를 통해 검증
        assertThat(line.getStations()).containsExactly(수원역, 수원중앙역);
    }

    @DisplayName("첫번째 구간 등록")
    @Test
    void addFirstSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        LineService lineService = new LineService(lineRepository, stationService);
        Line line = new Line(1L,"간선", "blue");
        when(lineRepository.findById(1L)).thenReturn(Optional.of(line));
        Station 수원역 = new Station(1L, "수원역");
        Station 수원중앙역 = new Station(2L, "수원중앙역");
        Station 강남역 = new Station(3L, "강남역");
        when(stationService.findById(1L)).thenReturn(new Station(1L,"수원역"));
        when(stationService.findById(2L)).thenReturn(new Station(2L, "수원중앙역"));
        when(stationService.findById(3L)).thenReturn(new Station(3L, "강남역"));

        // when
        // lineService.addSection 호출
        SectionRequest sectionRequest = new SectionRequest(수원역.getId(), 수원중앙역.getId(), 10);
        lineService.addSection(line.getId(), sectionRequest);

        SectionRequest sectionRequest2 = new SectionRequest(강남역.getId(), 수원역.getId(), 10);
        lineService.addSection(line.getId(), sectionRequest2);

        // then
        // line.findLineById 메서드를 통해 검증
        assertThat(line.getStations()).containsExactly(강남역, 수원역, 수원중앙역);
    }

    @DisplayName("지하철역의 최단거리 경로를 찾는다.")
    @Test
    void 지하철역의_최단거리_경로를_찾는다() {
        Station 수원역 = new Station(1L, "수원역");
        Station 수원중앙역 = new Station(2L, "수원중앙역");
        Station 강남역 = new Station(3L, "강남역");
        Station 서울역 = new Station(4L, "서울역");
        Station 역삼역 = new Station(5L, "역삼역");
        Station 곰역 = new Station(6L, "곰역");

        LineService lineService = new LineService(lineRepository, stationService);
        Line 일호선 = new Line("1호선", "red");
        int 역삼역_수원역_거리 = 5;
        int 수원역_수원중앙역_거리 = 5;
        int 수원중앙역_곰역_거리 = 10;
        일호선.addSections(역삼역, 수원역, 역삼역_수원역_거리);
        일호선.addSections(수원역, 수원중앙역, 수원역_수원중앙역_거리);
        일호선.addSections(수원중앙역, 곰역, 수원중앙역_곰역_거리);
        Line 이호선 = new Line("2호선", "blue");
        int 역삼역_강남역_거리 = 5;
        int 강남역_서울역_거리 = 4;
        int 서울역_곰역_거리 = 1;
        이호선.addSections(역삼역, 강남역, 역삼역_강남역_거리);
        이호선.addSections(강남역, 서울역, 강남역_서울역_거리);
        이호선.addSections(서울역, 곰역, 서울역_곰역_거리);

        List<Line> lines = Arrays.asList(일호선, 이호선);
        when(lineRepository.findAll()).thenReturn(lines);
        when(stationService.findById(역삼역.getId())).thenReturn(역삼역);
        when(stationService.findById(곰역.getId())).thenReturn(곰역);
        StationResponse 역삼역_응답 =
                new StationResponse(역삼역.getId(), 역삼역.getName(), null, null);
        when(stationService.createStationResponse(역삼역))
                .thenReturn(역삼역_응답);
        StationResponse 강남역_응답 =
                new StationResponse(강남역.getId(), 강남역.getName(), null, null);
        when(stationService.createStationResponse(강남역))
                .thenReturn(강남역_응답);
        StationResponse 서울역_응답 =
                new StationResponse(서울역.getId(), 서울역.getName(), null, null);
        when(stationService.createStationResponse(서울역))
                .thenReturn(서울역_응답);
        StationResponse 곰역_응답 = new StationResponse(곰역.getId(), 곰역.getName(), null, null);
        when(stationService.createStationResponse(곰역))
                .thenReturn(곰역_응답);

        //then
        PathResponse path = lineService.getPath(역삼역.getId(), 곰역.getId());
        assertThat(path.getStations()).containsExactly(역삼역_응답, 강남역_응답, 서울역_응답, 곰역_응답);
    }



}
