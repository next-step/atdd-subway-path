package nextstep.subway.unit;

import nextstep.subway.global.error.code.ErrorCode;
import nextstep.subway.global.error.exception.InvalidLineSectionException;
import nextstep.subway.global.error.exception.NotEntityFoundException;
import nextstep.subway.line.dto.request.SaveLineSectionRequestDto;
import nextstep.subway.line.dto.response.LineResponseDto;
import nextstep.subway.line.entity.Line;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.line.service.LineService;
import nextstep.subway.station.dto.response.StationResponseDto;
import nextstep.subway.station.entity.Station;
import nextstep.subway.station.repository.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class LineServiceTest {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    private Station 신사역;

    private Station 강남역;

    private Station 판교역;

    private Station 광교역;

    private Line 신분당선;

    @BeforeEach
    void setUp() {
        // given
        신사역 = stationRepository.save(new Station("신사역"));
        강남역 = stationRepository.save(new Station("강남역"));
        판교역 = stationRepository.save(new Station("판교역"));
        광교역 = stationRepository.save(new Station("광교역"));
        신분당선 = lineRepository.save(
                Line.builder()
                        .name("신분당선")
                        .color("#f5222d")
                        .upStation(신사역)
                        .downStation(강남역)
                        .distance(15)
                        .build()
        );
    }

    @Test
    @DisplayName("구간을 추가한다.")
    void addSection() {
        // when
        SaveLineSectionRequestDto 광교역이_하행_종점역인_구간 = SaveLineSectionRequestDto.builder()
                .upStationId(노선의_하행_종점역_아이디를_찾는다(신분당선))
                .downStationId(광교역.getId())
                .distance(13)
                .build();

        LineResponseDto lineResponseDto = lineService.saveLineSection(신분당선.getId(), 광교역이_하행_종점역인_구간);

        // then
        Long 노선의_하행_종점역_아이디 = 노선의_하행_종점역_아이디를_찾는다(lineResponseDto);

        assertThat(노선의_하행_종점역_아이디).isEqualTo(광교역이_하행_종점역인_구간.getDownStationId());
    }

    @Test
    @DisplayName("존재하지 않는 역이 하행 종점역인 구간을 추가한다.")
    void addNotExistDownStation() {
        // given
        SaveLineSectionRequestDto 존재하지_않는_역이_하행_종점역인_구간 = SaveLineSectionRequestDto.builder()
                .upStationId(노선의_하행_종점역_아이디를_찾는다(신분당선))
                .downStationId(0L)
                .distance(8)
                .build();

        // when & then
        assertThatThrownBy(() -> lineService.saveLineSection(신분당선.getId(), 존재하지_않는_역이_하행_종점역인_구간))
                .isInstanceOf(NotEntityFoundException.class)
                .hasMessageContaining(ErrorCode.NOT_EXIST_STATION.getMessage());
    }

    @Test
    @DisplayName("이미 등록되어 있는 역이 하행 종점역인 구간을 추가한다.")
    void addAlreadyRegisteredDownStation() {
        // given
        Long 신분당선_아이디 = 신분당선.getId();

        SaveLineSectionRequestDto 판교역이_하행_종점역인_구간 = SaveLineSectionRequestDto.builder()
                .upStationId(노선의_하행_종점역_아이디를_찾는다(신분당선))
                .downStationId(판교역.getId())
                .distance(5)
                .build();

        lineService.saveLineSection(신분당선_아이디, 판교역이_하행_종점역인_구간);

        // when & then
        SaveLineSectionRequestDto 이미_등록된_역이_하행_종점역인_구간 = SaveLineSectionRequestDto.builder()
                .upStationId(판교역.getId())
                .downStationId(노선의_하행_종점역_아이디를_찾는다(신분당선))
                .distance(5)
                .build();

        assertThatThrownBy(() -> lineService.saveLineSection(신분당선_아이디, 이미_등록된_역이_하행_종점역인_구간))
                .isInstanceOf(InvalidLineSectionException.class)
                .hasMessageContaining(ErrorCode.ALREADY_REGISTERED_STATION.getMessage());
    }

    @Test
    @DisplayName("노선의 마지막 구간을 삭제한다.")
    void deleteSection() {
        // given
        SaveLineSectionRequestDto 광교역이_하행_종점역인_구간 = SaveLineSectionRequestDto.builder()
                .upStationId(노선의_하행_종점역_아이디를_찾는다(신분당선))
                .downStationId(광교역.getId())
                .distance(13)
                .build();

        LineResponseDto lineResponseDto = lineService.saveLineSection(신분당선.getId(), 광교역이_하행_종점역인_구간);

        // when
        Long 노선의_하행_종점역_아이디 = 노선의_하행_종점역_아이디를_찾는다(lineResponseDto);
        lineService.deleteLineSectionByStationId(신분당선.getId(), 노선의_하행_종점역_아이디);

        // then
        List<Long> 노선에_등록된_역_아이디_목록 = lineService.findLineById(lineResponseDto.getId())
                .getStations()
                .stream()
                .map(StationResponseDto::getId)
                .collect(Collectors.toList());

        assertThat(노선에_등록된_역_아이디_목록).doesNotContain(노선의_하행_종점역_아이디);
    }

    @Test
    @DisplayName("등록되어 있지 않은 구간을 삭제한다.")
    void deleteNotExistSection() {
        // when & then
        assertThatThrownBy(() -> lineService.deleteLineSectionByStationId(신분당선.getId(), 판교역.getId()))
                .isInstanceOf(InvalidLineSectionException.class)
                .hasMessageContaining(ErrorCode.UNREGISTERED_STATION.getMessage());
    }

    @Test
    @DisplayName("구간이 1개일 때 삭제한다.")
    void deleteStandaloneSection() {
        // when & then
        assertThatThrownBy(() -> lineService.deleteLineSectionByStationId(신분당선.getId(), 강남역.getId()))
                .isInstanceOf(InvalidLineSectionException.class)
                .hasMessageContaining(ErrorCode.STAND_ALONE_LINE_SECTION.getMessage());
    }

    private Long 노선의_하행_종점역_아이디를_찾는다(Line line) {
        List<Station> stations = line.getSections().getAllStations();
        int lastIndex = stations.size() - 1;
        return stations
                .get(lastIndex)
                .getId();
    }

    private Long 노선의_하행_종점역_아이디를_찾는다(LineResponseDto lineResponseDto) {
        List<StationResponseDto> 노선에_등록된_역_목록 = lineResponseDto.getStations();
        int lastIndex = 노선에_등록된_역_목록.size() - 1;
        return 노선에_등록된_역_목록
                .get(lastIndex)
                .getId();
    }
}
