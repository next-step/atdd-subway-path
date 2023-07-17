package nextstep.subway.unit;

import nextstep.subway.global.error.code.ErrorCode;
import nextstep.subway.global.error.exception.InvalidLineSectionException;
import nextstep.subway.global.error.exception.NotEntityFoundException;
import nextstep.subway.line.adapters.persistence.LineJpaAdapter;
import nextstep.subway.line.dto.request.SaveLineSectionRequestDto;
import nextstep.subway.line.dto.response.LineResponseDto;
import nextstep.subway.line.entity.Line;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.line.service.LineService;
import nextstep.subway.section.entity.Section;
import nextstep.subway.station.adapters.persistence.StationJpaAdapter;
import nextstep.subway.station.dto.response.StationResponseDto;
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

    private Station 까치산역;

    private Station 신도림역;

    private Station 신촌역;

    private Station 강남역;

    private Line 이호선;

    @BeforeEach
    void setUp() {
        LineJpaAdapter lineJpaAdapter = new LineJpaAdapter(lineRepository);
        StationJpaAdapter stationJpaAdapter = new StationJpaAdapter(stationRepository);
        lineService = new LineService(stationJpaAdapter, lineJpaAdapter);

        // given
        this.까치산역 = new Station(1L, "까치산역");
        this.신도림역 = new Station(2L, "신도림역");
        this.신촌역 = new Station(3L, "신촌역");
        this.강남역 = new Station(4L, "강남역");

        this.이호선 = Line.builder()
                .name("2호선")
                .color("#52c41a")
                .upStation(까치산역)
                .downStation(신도림역)
                .distance(5)
                .build();
        this.이호선.addSection(
                Section.builder()
                        .upStation(신도림역)
                        .downStation(신촌역)
                        .distance(7)
                        .build()
        );
    }

    @Test
    @DisplayName("구간을 추가한다.")
    void addSection() {
        // given
        given(stationRepository.findById(강남역.getId())).willReturn(Optional.of(강남역));
        given(stationRepository.findById(신촌역.getId())).willReturn(Optional.of(신촌역));
        given(lineRepository.findById(이호선.getId())).willReturn(Optional.of(이호선));

        SaveLineSectionRequestDto saveLineSectionRequestDto = 이호선에_강남역이_하행_종점역인_구간을_생성한다(
                지하철_노선의_하행_종점역_아이디를_찾는다(이호선)
        );

        // when
        LineResponseDto responseDto = lineService.saveLineSection(이호선.getId(), saveLineSectionRequestDto);

        // then
        Long 노선의_하행_종점역_아이디 = 지하철_노선의_하행_종점역_아이디를_찾는다(responseDto);

        assertThat(노선의_하행_종점역_아이디).isEqualTo(saveLineSectionRequestDto.getDownStationId());
    }

    @Test
    @DisplayName("존재하지 않는 역이 하행 종점역인 구간을 추가한다.")
    void addNotExistDownStation() {
        // given
        given(stationRepository.findById(신촌역.getId())).willReturn(Optional.of(신촌역));
        given(stationRepository.findById(강남역.getId())).willReturn(Optional.empty());

        SaveLineSectionRequestDto 존재하지_않는_역이_하행_종점역인_구간 = 이호선에_강남역이_하행_종점역인_구간을_생성한다(
                지하철_노선의_하행_종점역_아이디를_찾는다(이호선)
        );

        // when & then
        assertThatThrownBy(() -> lineService.saveLineSection(이호선.getId(), 존재하지_않는_역이_하행_종점역인_구간))
                .isInstanceOf(NotEntityFoundException.class)
                .hasMessageContaining(ErrorCode.NOT_EXIST_STATION.getMessage());
    }

    @Test
    @DisplayName("이미 등록되어 있는 역이 하행 종점역인 구간을 추가한다.")
    void addAlreadyRegisteredDownStation() {
        // given
        given(stationRepository.findById(까치산역.getId())).willReturn(Optional.of(까치산역));
        given(stationRepository.findById(신촌역.getId())).willReturn(Optional.of(신촌역));
        given(lineRepository.findById(이호선.getId())).willReturn(Optional.of(이호선));

        SaveLineSectionRequestDto 이미_등록된_역이_하행_종점역인_구간 = SaveLineSectionRequestDto.builder()
                .upStationId(지하철_노선의_하행_종점역_아이디를_찾는다(이호선))
                .downStationId(까치산역.getId())
                .distance(5)
                .build();

        // when & then
        assertThatThrownBy(() -> lineService.saveLineSection(이호선.getId(), 이미_등록된_역이_하행_종점역인_구간))
                .isInstanceOf(InvalidLineSectionException.class)
                .hasMessageContaining(ErrorCode.ALREADY_REGISTERED_STATION.getMessage());
    }

    @Test
    @DisplayName("노선의 마지막 구간을 삭제한다.")
    void deleteSection() {
        // given
        given(lineRepository.findById(이호선.getId())).willReturn(Optional.of(이호선));
        Long 노선의_하행_종점역_아이디 = 지하철_노선의_하행_종점역_아이디를_찾는다(이호선);

        // when
        lineService.deleteLineSectionByStationId(이호선.getId(), 노선의_하행_종점역_아이디);

        // then
        List<Long> 노선에_등록된_역_아이디_목록 = 이호선.getSections()
                .getAllStations()
                .stream()
                .map(Station::getId)
                .collect(Collectors.toList());

        assertThat(노선에_등록된_역_아이디_목록).doesNotContain(노선의_하행_종점역_아이디);
    }

    @Test
    @DisplayName("등록되어 있지 않은 구간을 삭제한다.")
    void deleteNotExistSection() {
        // given
        given(lineRepository.findById(이호선.getId())).willReturn(Optional.of(이호선));

        // when & then
        assertThatThrownBy(() -> lineService.deleteLineSectionByStationId(이호선.getId(), 강남역.getId()))
                .isInstanceOf(InvalidLineSectionException.class)
                .hasMessageContaining(ErrorCode.UNREGISTERED_STATION.getMessage());
    }

    @Test
    @DisplayName("구간이 1개일 때 삭제한다.")
    void deleteStandaloneSection() {
        // given: 현재 두 구간(까치산역 - 신도림역 - 신촌역)을 가지고 있기 때문에 한 구간을 삭제한다.
        이호선.deleteSectionByStationId(지하철_노선의_하행_종점역_아이디를_찾는다(이호선));
        given(lineRepository.findById(이호선.getId())).willReturn(Optional.of(이호선));

        Long 노선의_하행_종점역_아이디 = 지하철_노선의_하행_종점역_아이디를_찾는다(이호선);

        // when & then
        assertThatThrownBy(() -> lineService.deleteLineSectionByStationId(이호선.getId(), 노선의_하행_종점역_아이디))
                .isInstanceOf(InvalidLineSectionException.class)
                .hasMessageContaining(ErrorCode.STAND_ALONE_LINE_SECTION.getMessage());
    }

    private SaveLineSectionRequestDto 이호선에_강남역이_하행_종점역인_구간을_생성한다(Long upStationId) {
        return SaveLineSectionRequestDto.builder()
                .upStationId(upStationId)
                .downStationId(강남역.getId())
                .distance(12)
                .build();
    }

    private Long 지하철_노선의_하행_종점역_아이디를_찾는다(Line line) {
        List<Station> stations = line.getSections().getAllStations();
        int lastIndex = stations.size() - 1;

        return stations.get(lastIndex).getId();
    }

    private Long 지하철_노선의_하행_종점역_아이디를_찾는다(LineResponseDto responseDto) {
        List<StationResponseDto> stations = responseDto.getStations();
        int lastIndex = stations.size() - 1;

        return stations.get(lastIndex).getId();
    }
}
