package nextstep.subway.unit;

import nextstep.subway.controller.dto.LineResponse;
import nextstep.subway.controller.dto.SectionCreateRequest;
import nextstep.subway.controller.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.repository.LineRepository;
import nextstep.subway.service.LineService;
import nextstep.subway.service.StationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@DisplayName("구간 서비스 단위 테스트 with Mock")
@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;
    @InjectMocks
    private LineService lineService;
    private final Long 신림선_아이디 = 1L;

    Line 신림선;
    Station 신림역;
    Station 보라매역;
    Station 보라매병원역;

    private final Long 신림역_아이디= 1L;
    private final Long 보라매역_아이디= 2L;
    private final Long 보라매병원역_아이디= 3L;

    @BeforeEach
    public void setUp() {
        신림역 = new Station("신림역");
        ReflectionTestUtils.setField(신림역, "id", 신림역_아이디);
        보라매역 = new Station("보라매역");
        ReflectionTestUtils.setField(보라매역, "id", 보라매역_아이디);
        보라매병원역 = new Station("보라매병원역");
        ReflectionTestUtils.setField(보라매병원역, "id", 보라매병원역_아이디);

        신림선 = Line.builder()
                .name("신림선")
                .upStation(신림역)
                .downStation(보라매역)
                .color("BLUE")
                .distance(10L)
                .build();
    }

    @Test
    @DisplayName("구간을 등록한다.")
    void addSection() {
        // given
        when(stationService.getStationById(보라매역.getId())).thenReturn(보라매역);
        when(stationService.getStationById(보라매병원역.getId())).thenReturn(보라매병원역);
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(신림선));


        // when
        SectionCreateRequest 구간_생성_요청 = SectionCreateRequest.builder()
                .upStationId(String.valueOf(보라매역.getId()))
                .downStationId(String.valueOf(보라매병원역.getId()))
                .distance(22L)
                .build();
        lineService.addSection(신림선_아이디, 구간_생성_요청);


        // then
        LineResponse lineResponse = lineService.findLineById(1L);
        assertThat(lineResponse.getStations()).hasSize(3);
    }

    @Test
    @DisplayName("지하철 마지막 구간을 등록한다.")
    void 지하철_마지막구간_등록() {
        // given
        when(stationService.getStationById(보라매역.getId())).thenReturn(보라매역);
        when(stationService.getStationById(보라매병원역.getId())).thenReturn(보라매병원역);
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(신림선));


        // when
        SectionCreateRequest 구간_생성_요청 = SectionCreateRequest.builder()
                .upStationId(String.valueOf(보라매역.getId()))
                .downStationId(String.valueOf(보라매병원역.getId()))
                .distance(22L)
                .build();
        lineService.addSection(신림선_아이디, 구간_생성_요청);


        // then
        LineResponse lineResponse = lineService.findLineById(1L);
        assertThat(lineResponse.getStations()).extracting(StationResponse::getName)
                .containsExactly(신림역.getName(), 보라매역.getName(), 보라매병원역.getName());
    }

    @Test
    @DisplayName("지하철 중간 구간을 등록한다.")
    void 지하철_중간구간_등록() {
        // given
        when(stationService.getStationById(신림역.getId())).thenReturn(신림역);
        when(stationService.getStationById(보라매병원역.getId())).thenReturn(보라매병원역);
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(신림선));


        // when
        SectionCreateRequest 구간_생성_요청 = SectionCreateRequest.builder()
                .upStationId(String.valueOf(신림역.getId()))
                .downStationId(String.valueOf(보라매병원역.getId()))
                .distance(5L)
                .build();
        lineService.addSection(신림선_아이디, 구간_생성_요청);


        // then
        LineResponse lineResponse = lineService.findLineById(1L);
        assertThat(lineResponse.getStations()).extracting(StationResponse::getName)
                .containsExactly(신림역.getName(), 보라매병원역.getName(), 보라매역.getName());
    }

    @Test
    @DisplayName("지하철 처음 구간을 등록한다.")
    void 지하철_처음구간_등록() {
        // given
        when(stationService.getStationById(신림역.getId())).thenReturn(신림역);
        when(stationService.getStationById(보라매병원역.getId())).thenReturn(보라매병원역);
        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(신림선));


        // when
        SectionCreateRequest 구간_생성_요청 = SectionCreateRequest.builder()
                .upStationId(String.valueOf(보라매병원역.getId()))
                .downStationId(String.valueOf(신림역.getId()))
                .distance(5L)
                .build();
        lineService.addSection(신림선_아이디, 구간_생성_요청);


        // then
        LineResponse lineResponse = lineService.findLineById(1L);
        assertThat(lineResponse.getStations()).extracting(StationResponse::getName)
                .containsExactly(보라매병원역.getName(), 신림역.getName(), 보라매역.getName());
    }

    @Test
    @DisplayName("지하철 노선의 마지막역을 삭제한다.")
    void 지하철노선_마지막역_삭제() {
        //given
        Section 보라매보라매병원역 = Section.builder()
                .upStation(보라매역)
                .downStation(보라매병원역)
                .line(신림선)
                .distance(11L)
                .build();
        신림선.addSection(보라매보라매병원역);

        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(신림선));

        //when
        lineService.removeSection(신림선_아이디, 보라매병원역.getId());

        //then
        LineResponse lineResponse = lineService.findLineById(1L);
        assertThat(lineResponse.getStations()).hasSize(2);
    }

    @Test
    @DisplayName("지하철 노선의 시작역을 삭제한다.")
    void 지하철노선_시작역_삭제() {
        //given
        Section 보라매보라매병원역 = Section.builder()
                .upStation(보라매역)
                .downStation(보라매병원역)
                .line(신림선)
                .distance(11L)
                .build();
        신림선.addSection(보라매보라매병원역);

        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(신림선));

        //when
        lineService.removeSection(신림선_아이디, 신림역.getId());

        //then
        LineResponse lineResponse = lineService.findLineById(1L);
        assertThat(lineResponse.getStations()).hasSize(2);
    }

    @Test
    @DisplayName("지하철 노선의 중간역을 삭제한다.")
    void 지하철노선_중간역_삭제() {
        //given
        Section 보라매보라매병원역 = Section.builder()
                .upStation(보라매역)
                .downStation(보라매병원역)
                .line(신림선)
                .distance(11L)
                .build();
        신림선.addSection(보라매보라매병원역);

        when(lineRepository.findById(anyLong())).thenReturn(Optional.of(신림선));

        //when
        lineService.removeSection(신림선_아이디, 보라매역.getId());

        //then
        LineResponse lineResponse = lineService.findLineById(1L);
        assertThat(lineResponse.getStations()).hasSize(2);
    }
}
