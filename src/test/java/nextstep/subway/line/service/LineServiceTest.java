package nextstep.subway.line.service;

import nextstep.subway.common.exception.BadRequestException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.station.repository.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class LineServiceTest {
    static final Long 이호선_코드 = 1L;

    static final Long 이호선_상행종점역_코드 = 1L;
    static final Long 이호선_하행종점역_코드 = 2L;
    static final Long 새로운구간_상행역_코드 = 3L;
    static final Long 새로운구간_하행역_코드 = 4L;

    static final int 이호선_첫구간_길이 = 7;
    static final int 새로운구간_길이 = 6;

    @Mock
    LineRepository lineRepository;

    @Mock
    StationRepository stationRepository;

    @InjectMocks
    LineService lineService;

    Line 이호선;

    @BeforeEach
    void setUp() {
        이호선 = Line.builder()
                .id(이호선_코드)
                .name("2호선")
                .color("bg-green")
                .build();

        Station 신도림역 = new Station(이호선_상행종점역_코드, "신도림역");
        Station 영등포구청역 = new Station(이호선_하행종점역_코드, "영등포구청역");

        이호선.addSection(신도림역, 영등포구청역, 이호선_첫구간_길이);

        when(lineRepository.findById(이호선.getId())).thenReturn(Optional.of(이호선));
        when(stationRepository.findById(신도림역.getId())).thenReturn(Optional.of(신도림역));
        when(stationRepository.findById(영등포구청역.getId())).thenReturn(Optional.of(영등포구청역));
    }

    @Test
    void 노선_등록() {
        // given
        Station 부천역 = new Station(새로운구간_상행역_코드, "부천역");
        Station 소사역 = new Station(새로운구간_하행역_코드, "소사역");

        LineRequest request = LineRequest.builder()
                .name("1호선")
                .color("bg-blue")
                .upStationId(새로운구간_상행역_코드)
                .downStationId(새로운구간_하행역_코드)
                .distance(3)
                .build();

        when(stationRepository.findById(부천역.getId())).thenReturn(Optional.of(부천역));
        when(stationRepository.findById(소사역.getId())).thenReturn(Optional.of(소사역));

        // when
        LineResponse lineResponse = lineService.saveLine(request);

        // then
        List<Long> stationIds = lineResponse.getStations().stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        assertThat(stationIds).containsExactly(부천역.getId(), 소사역.getId());
    }

    @Test
    void 중복된_이름으로_노선등록_안된다() {
        // given
        when(lineRepository.findByName(이호선.getName())).thenReturn(Optional.of(이호선));

        LineRequest request = LineRequest.builder()
                .name(이호선.getName())
                .color(이호선.getColor())
                .distance(이호선_첫구간_길이)
                .upStationId(이호선_상행종점역_코드)
                .downStationId(이호선_하행종점역_코드)
                .build();

        // when, then
        assertThatThrownBy(() ->
                lineService.saveLine(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(LineService.DUPLICATE_LINE_EXCEPTION_MSG);
    }

    @Test
    void 노선_수정() {
        // given
        LineRequest request = LineRequest.builder()
                .name("1호선")
                .color("bg-blue")
                .build();

        // when
        LineResponse lineResponse = lineService.updateLine(이호선_코드, request);

        // then
        assertThat(lineResponse.getName()).isEqualTo(request.getName());
        assertThat(lineResponse.getColor()).isEqualTo(request.getColor());
    }

    @Test
    void 구간_추가() {
        // given
        Station 문래역 = new Station(새로운구간_하행역_코드, "문래역");
        long 문래역_코드 = 문래역.getId();
        when(stationRepository.findById(문래역_코드)).thenReturn(Optional.of(문래역));

        SectionRequest request = new SectionRequest(이호선_상행종점역_코드, 문래역_코드, 새로운구간_길이);

        // when
        lineService.addSection(이호선_코드, request);
        LineResponse line = lineService.findLine(이호선_코드);

        // then
        List<Long> stationIds = line.getStations().stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        assertThat(stationIds).containsExactly(이호선_상행종점역_코드, 문래역_코드, 이호선_하행종점역_코드);
    }

    @Test
    void 역_삭제() {
        // given
        Station 문래역 = new Station(새로운구간_하행역_코드, "문래역");
        long 문래역_코드 = 문래역.getId();
        when(stationRepository.findById(문래역_코드)).thenReturn(Optional.of(문래역));

        SectionRequest request = new SectionRequest(이호선_상행종점역_코드, 문래역_코드, 새로운구간_길이);
        lineService.addSection(이호선_코드, request);

        // when
        lineService.removeSection(이호선_코드, 문래역_코드);
        LineResponse line = lineService.findLine(이호선_코드);

        // then
        assertThat(line.getStations()).hasSize(2);
    }
}
