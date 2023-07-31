package nextstep.subway.unit.line;

import nextstep.subway.fixture.StationFixture;
import nextstep.subway.global.error.code.ErrorCode;
import nextstep.subway.global.error.exception.InvalidLineSectionException;
import nextstep.subway.global.error.exception.NotEntityFoundException;
import nextstep.subway.line.adapters.persistence.LineJpaAdapter;
import nextstep.subway.line.dto.request.SaveLineSectionRequest;
import nextstep.subway.line.dto.response.LineResponse;
import nextstep.subway.line.entity.Line;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.line.service.LineService;
import nextstep.subway.station.adapters.persistence.StationJpaAdapter;
import nextstep.subway.station.dto.response.StationResponse;
import nextstep.subway.station.entity.Station;
import nextstep.subway.station.repository.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

    @Mock
    LineRepository lineRepository;

    @Mock
    StationRepository stationRepository;

    private LineService lineService;

    private static final Long 까치산역_아이디 = StationFixture.까치산역.getId();

    private static final Long 신도림역_아이디 = StationFixture.신도림역.getId();

    private static final Long 신촌역_아이디 = StationFixture.신촌역.getId();

    private static final Long 잠실역_아이디 = StationFixture.잠실역.getId();

    private Line 이호선;

    @BeforeEach
    void setUp() {
        LineJpaAdapter lineJpaAdapter = new LineJpaAdapter(lineRepository);
        StationJpaAdapter stationJpaAdapter = new StationJpaAdapter(stationRepository);
        lineService = new LineService(stationJpaAdapter, lineJpaAdapter);

        // given
        이호선 = Line.builder()
                .name("2호선")
                .color("#52c41a")
                .upStation(StationFixture.까치산역)
                .downStation(StationFixture.신촌역)
                .distance(12)
                .build();
    }

    @Test
    @DisplayName("지하철 노선의 상행 종점역이 하행역인 구간을 추가한다.")
    void addFirstLineSection() {
        // given
        given(stationRepository.findById(신도림역_아이디)).willReturn(Optional.of(StationFixture.신도림역));
        given(stationRepository.findById(까치산역_아이디)).willReturn(Optional.of(StationFixture.까치산역));
        given(lineRepository.findById(이호선.getId())).willReturn(Optional.of(이호선));

        SaveLineSectionRequest 신도림역_까치산역_구간_생성_요청 = SaveLineSectionRequest.builder()
                .upStationId(신도림역_아이디)
                .downStationId(까치산역_아이디)
                .distance(5)
                .build();

        // when
        lineService.saveLineSection(이호선.getId(), 신도림역_까치산역_구간_생성_요청);

        // then
        List<Long> 등록된_지하철역_아이디_목록 = 노선에_등록된_역_아이디_목록을_가져온다(이호선);
        assertThat(등록된_지하철역_아이디_목록).containsExactly(신도림역_아이디, 까치산역_아이디, 신촌역_아이디);
    }

    @Test
    @DisplayName("지하철 노선의 상행 종점역과 하행 종점역 사이에 구간을 추가한다.")
    void addMiddleLineSection() {
        // given
        given(stationRepository.findById(신도림역_아이디)).willReturn(Optional.of(StationFixture.신도림역));
        given(stationRepository.findById(까치산역_아이디)).willReturn(Optional.of(StationFixture.까치산역));
        given(lineRepository.findById(이호선.getId())).willReturn(Optional.of(이호선));

        SaveLineSectionRequest 까치산역_신도림역_구간_생성_요청 =
                이호선에_신도림이_하행역인_구간을_생성한다(까치산역_아이디, 5);

        // when
        lineService.saveLineSection(이호선.getId(), 까치산역_신도림역_구간_생성_요청);

        // then
        List<Long> 등록된_지하철역_아이디_목록 = 노선에_등록된_역_아이디_목록을_가져온다(이호선);
        assertThat(등록된_지하철역_아이디_목록).containsExactly(까치산역_아이디, 신도림역_아이디, 신촌역_아이디);
    }

    @Test
    @DisplayName("지하철 노선의 하행 종점역에 구간을 추가한다.")
    void addLastLineSection() {
        // given
        given(stationRepository.findById(잠실역_아이디)).willReturn(Optional.of(StationFixture.잠실역));
        given(stationRepository.findById(신촌역_아이디)).willReturn(Optional.of(StationFixture.신촌역));
        given(lineRepository.findById(이호선.getId())).willReturn(Optional.of(이호선));

        SaveLineSectionRequest 신촌역_잠실역_구간_생성_요청 = 이호선에_잠실역이_하행_종점역인_구간을_생성한다(신촌역_아이디);

        // when
        lineService.saveLineSection(이호선.getId(), 신촌역_잠실역_구간_생성_요청);

        // then
        List<Long> 등록된_지하철역_아이디_목록 = 노선에_등록된_역_아이디_목록을_가져온다(이호선);
        assertThat(등록된_지하철역_아이디_목록).containsExactly(까치산역_아이디, 신촌역_아이디, 잠실역_아이디);
    }

    @Test
    @DisplayName("존재하지 않는 역이 하행 종점역인 구간을 추가한다.")
    void addNotExistDownStation() {
        // given
        given(stationRepository.findById(신촌역_아이디)).willReturn(Optional.of(StationFixture.신촌역));
        given(stationRepository.findById(잠실역_아이디)).willReturn(Optional.empty());

        SaveLineSectionRequest 존재하지_않는_역이_하행_종점역인_구간_생성_요청 = 이호선에_잠실역이_하행_종점역인_구간을_생성한다(
                지하철_노선의_하행_종점역_아이디를_찾는다(이호선)
        );

        // when & then
        assertThatThrownBy(() -> lineService.saveLineSection(이호선.getId(), 존재하지_않는_역이_하행_종점역인_구간_생성_요청))
                .isInstanceOf(NotEntityFoundException.class)
                .hasMessageContaining(ErrorCode.NOT_EXIST_STATION.getMessage());
    }

    @Test
    @DisplayName("역 사이에 기존 역 사이 길이보다 크거나 같은 노선을 등록한다.")
    void addInvalidDistanceLineSection() {
        // given
        given(stationRepository.findById(신도림역_아이디)).willReturn(Optional.of(StationFixture.신도림역));
        given(stationRepository.findById(까치산역_아이디)).willReturn(Optional.of(StationFixture.까치산역));
        given(lineRepository.findById(이호선.getId())).willReturn(Optional.of(이호선));

        SaveLineSectionRequest 까치산역_신도림역_구간_생성_요청 =
                이호선에_신도림이_하행역인_구간을_생성한다(까치산역_아이디, 15);

        // when & then
        assertThatThrownBy(() -> lineService.saveLineSection(이호선.getId(), 까치산역_신도림역_구간_생성_요청))
                .isInstanceOf(InvalidLineSectionException.class)
                .hasMessageContaining(ErrorCode.INVALID_DISTANCE.getMessage());
    }

    @Test
    @DisplayName("이미 등록되어 있는 노선을 등록한다.")
    void addAlreadyRegisteredLineSection() {
        // given
        given(stationRepository.findById(신촌역_아이디)).willReturn(Optional.of(StationFixture.신촌역));
        given(stationRepository.findById(까치산역_아이디)).willReturn(Optional.of(StationFixture.까치산역));
        given(lineRepository.findById(이호선.getId())).willReturn(Optional.of(이호선));

        SaveLineSectionRequest 까치산역_신촌역_구간_생성_요청 = SaveLineSectionRequest.builder()
                .upStationId(까치산역_아이디)
                .downStationId(신촌역_아이디)
                .distance(12)
                .build();

        // when & then
        assertThatThrownBy(() -> lineService.saveLineSection(이호선.getId(), 까치산역_신촌역_구간_생성_요청))
                .isInstanceOf(InvalidLineSectionException.class)
                .hasMessageContaining(ErrorCode.ALREADY_REGISTERED_SECTION.getMessage());
    }

    @Test
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않은 노선을 등록한다.")
    void addLineSectionWithUnregisteredStation() {
        // given
        given(stationRepository.findById(신도림역_아이디)).willReturn(Optional.of(StationFixture.신도림역));
        given(stationRepository.findById(잠실역_아이디)).willReturn(Optional.of(StationFixture.잠실역));
        given(lineRepository.findById(이호선.getId())).willReturn(Optional.of(이호선));

        SaveLineSectionRequest 신도림역_잠실역_구간_생성_요청 = 이호선에_잠실역이_하행_종점역인_구간을_생성한다(신도림역_아이디);

        // when & then
        assertThatThrownBy(() -> lineService.saveLineSection(이호선.getId(), 신도림역_잠실역_구간_생성_요청))
                .isInstanceOf(InvalidLineSectionException.class)
                .hasMessageContaining(ErrorCode.UNREGISTERED_STATION.getMessage());
    }

    @Test
    @DisplayName("노선의 상행 종점역을 삭제한다.")
    void deleteUpStation() {
        // given
        given(stationRepository.findById(잠실역_아이디)).willReturn(Optional.of(StationFixture.잠실역));
        given(stationRepository.findById(신촌역_아이디)).willReturn(Optional.of(StationFixture.신촌역));
        given(lineRepository.findById(이호선.getId())).willReturn(Optional.of(이호선));
        SaveLineSectionRequest 신촌역_잠실역_구간_생성_요청 = 이호선에_잠실역이_하행_종점역인_구간을_생성한다(신촌역_아이디);
        LineResponse 신촌역_잠실역_구간_생성_응답 = lineService.saveLineSection(이호선.getId(), 신촌역_잠실역_구간_생성_요청);

        // when
        Long 노선의_상행_종점역_아이디 = 지하철_노선의_성행_종점역_아이디를_찾는다(신촌역_잠실역_구간_생성_응답);
        lineService.deleteLineSectionByStationId(이호선.getId(), 노선의_상행_종점역_아이디);

        // then
        List<Long> 노선에_등록된_역_아이디_목록 = 노선에_등록된_역_아이디_목록을_가져온다(이호선);
        assertThat(노선에_등록된_역_아이디_목록).doesNotContain(노선의_상행_종점역_아이디);
    }

    @Test
    @DisplayName("노선의 중간역을 삭제한다.")
    void deleteMiddleStation() {
        // given
        given(stationRepository.findById(잠실역_아이디)).willReturn(Optional.of(StationFixture.잠실역));
        given(stationRepository.findById(신촌역_아이디)).willReturn(Optional.of(StationFixture.신촌역));
        given(lineRepository.findById(이호선.getId())).willReturn(Optional.of(이호선));
        SaveLineSectionRequest 신촌역_잠실역_구간_생성_요청 = 이호선에_잠실역이_하행_종점역인_구간을_생성한다(신촌역_아이디);
        lineService.saveLineSection(이호선.getId(), 신촌역_잠실역_구간_생성_요청);

        // when
        Long 노선의_중간역_아이디 = 신촌역_잠실역_구간_생성_요청.getUpStationId();
        lineService.deleteLineSectionByStationId(이호선.getId(), 노선의_중간역_아이디);

        // then
        List<Long> 노선에_등록된_역_아이디_목록 = 노선에_등록된_역_아이디_목록을_가져온다(이호선);
        assertThat(노선에_등록된_역_아이디_목록).doesNotContain(노선의_중간역_아이디);
    }

    @Test
    @DisplayName("노선의 하행 종점역을 삭제한다.")
    void deleteDownStation() {
        // given
        given(stationRepository.findById(잠실역_아이디)).willReturn(Optional.of(StationFixture.잠실역));
        given(stationRepository.findById(신촌역_아이디)).willReturn(Optional.of(StationFixture.신촌역));
        given(lineRepository.findById(이호선.getId())).willReturn(Optional.of(이호선));
        SaveLineSectionRequest 신촌역_잠실역_구간_생성_요청 = 이호선에_잠실역이_하행_종점역인_구간을_생성한다(신촌역_아이디);
        LineResponse 신촌역_잠실역_구간_생성_응답 = lineService.saveLineSection(이호선.getId(), 신촌역_잠실역_구간_생성_요청);

        // when
        Long 노선의_하행_종점역_아이디 = 지하철_노선의_하행_종점역_아이디를_찾는다(신촌역_잠실역_구간_생성_응답);
        lineService.deleteLineSectionByStationId(이호선.getId(), 노선의_하행_종점역_아이디);

        // then
        List<Long> 노선에_등록된_역_아이디_목록 = 노선에_등록된_역_아이디_목록을_가져온다(이호선);
        assertThat(노선에_등록된_역_아이디_목록).doesNotContain(노선의_하행_종점역_아이디);
    }

    @Test
    @DisplayName("등록되어 있지 않은 구간을 삭제한다.")
    void deleteNotExistSection() {
        // given
        given(lineRepository.findById(이호선.getId())).willReturn(Optional.of(이호선));

        // when & then
        assertThatThrownBy(() -> lineService.deleteLineSectionByStationId(이호선.getId(), 잠실역_아이디))
                .isInstanceOf(InvalidLineSectionException.class)
                .hasMessageContaining(ErrorCode.UNREGISTERED_STATION.getMessage());
    }

    @Test
    @DisplayName("구간이 1개일 때 삭제한다.")
    void deleteStandaloneSection() {
        // given
        given(lineRepository.findById(이호선.getId())).willReturn(Optional.of(이호선));

        Long 노선의_하행_종점역_아이디 = 지하철_노선의_하행_종점역_아이디를_찾는다(이호선);

        // when & then
        assertThatThrownBy(() -> lineService.deleteLineSectionByStationId(이호선.getId(), 노선의_하행_종점역_아이디))
                .isInstanceOf(InvalidLineSectionException.class)
                .hasMessageContaining(ErrorCode.STAND_ALONE_LINE_SECTION.getMessage());
    }

    private List<Long> 노선에_등록된_역_아이디_목록을_가져온다(Line line) {
        return line.getSections()
                .getAllStations()
                .stream()
                .map(Station::getId)
                .collect(Collectors.toList());
    }

    private SaveLineSectionRequest 이호선에_잠실역이_하행_종점역인_구간을_생성한다(Long upStationId) {
        return SaveLineSectionRequest.builder()
                .upStationId(upStationId)
                .downStationId(잠실역_아이디)
                .distance(12)
                .build();
    }

    private SaveLineSectionRequest 이호선에_신도림이_하행역인_구간을_생성한다(Long upStationId, Integer distance) {
        return SaveLineSectionRequest.builder()
                .upStationId(upStationId)
                .downStationId(신도림역_아이디)
                .distance(distance)
                .build();
    }

    private Long 지하철_노선의_성행_종점역_아이디를_찾는다(LineResponse lineResponseDto) {
        return lineResponseDto.getStations()
                .get(0)
                .getId();
    }

    private Long 지하철_노선의_하행_종점역_아이디를_찾는다(Line line) {
        List<Station> stations = line.getSections().getAllStations();
        int lastIndex = stations.size() - 1;

        return stations.get(lastIndex).getId();
    }

    private Long 지하철_노선의_하행_종점역_아이디를_찾는다(LineResponse lineResponseDto) {
        List<StationResponse> 노선에_등록된_역_목록 = lineResponseDto.getStations();
        int lastIndex = 노선에_등록된_역_목록.size() - 1;

        return 노선에_등록된_역_목록
                .get(lastIndex)
                .getId();
    }
}
