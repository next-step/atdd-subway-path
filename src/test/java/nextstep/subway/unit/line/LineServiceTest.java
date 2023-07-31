package nextstep.subway.unit.line;

import nextstep.subway.global.error.code.ErrorCode;
import nextstep.subway.global.error.exception.InvalidLineSectionException;
import nextstep.subway.global.error.exception.NotEntityFoundException;
import nextstep.subway.line.dto.request.SaveLineSectionRequest;
import nextstep.subway.line.dto.response.LineResponse;
import nextstep.subway.line.entity.Line;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.line.service.LineService;
import nextstep.subway.station.dto.response.StationResponse;
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

    private Long 신사역_아이디;

    private Long 강남역_아이디;

    private Long 판교역_아이디;

    private Long 광교역_아이디;

    private Line 신분당선;

    @BeforeEach
    void setUp() {
        // given
        Station 신사역 = stationRepository.save(new Station("신사역"));
        신사역_아이디 = 신사역.getId();

        Station 강남역 = stationRepository.save(new Station("강남역"));
        강남역_아이디 = 강남역.getId();

        Station 판교역 = stationRepository.save(new Station("판교역"));
        판교역_아이디 = 판교역.getId();

        Station 광교역 = stationRepository.save(new Station("광교역"));
        광교역_아이디 = 광교역.getId();

        신분당선 = lineRepository.save(
                Line.builder()
                        .name("신분당선")
                        .color("#f5222d")
                        .upStation(신사역)
                        .downStation(판교역)
                        .distance(23)
                        .build()
        );
    }

    @Test
    @DisplayName("지하철 노선의 상행 종점역이 하행역인 구간을 추가한다.")
    void addFirstLineSection() {
        // when
        SaveLineSectionRequest 강남역_신사역_노선_요청 = SaveLineSectionRequest.builder()
                .upStationId(강남역_아이디)
                .downStationId(신사역_아이디)
                .distance(15)
                .build();

        LineResponse 강남역_신사역_노선_응답 = lineService.saveLineSection(신분당선.getId(), 강남역_신사역_노선_요청);

        // then
        List<Long> 등록된_지하철역_아이디_목록 = 노선에_등록된_역_아이디_목록을_가져온다(강남역_신사역_노선_응답);
        assertThat(등록된_지하철역_아이디_목록).containsExactly(강남역_아이디, 신사역_아이디, 판교역_아이디);
    }

    @Test
    @DisplayName("지하철 노선의 상행 종점역과 하행 종점역 사이에 구간을 추가한다.")
    void addMiddleLineSection() {
        // when
        SaveLineSectionRequest 신사역_강남역_노선_생성_요청 = SaveLineSectionRequest.builder()
                .upStationId(신사역_아이디)
                .downStationId(강남역_아이디)
                .distance(15)
                .build();

        LineResponse 신사역_강남역_노선_생성_응답 = lineService.saveLineSection(신분당선.getId(), 신사역_강남역_노선_생성_요청);

        // then
        List<Long> 등록된_지하철역_아이디_목록 = 노선에_등록된_역_아이디_목록을_가져온다(신사역_강남역_노선_생성_응답);
        assertThat(등록된_지하철역_아이디_목록).containsExactly(신사역_아이디, 강남역_아이디, 판교역_아이디);
    }

    @Test
    @DisplayName("지하철 노선의 하행 종점역에 구간을 추가한다.")
    void addLastLineSection() {
        // when
        SaveLineSectionRequest 판교역_광교역_노선_생성_요청 = SaveLineSectionRequest.builder()
                .upStationId(판교역_아이디)
                .downStationId(광교역_아이디)
                .distance(5)
                .build();

        LineResponse 판교역_광교역_노선_생성_응답 = lineService.saveLineSection(신분당선.getId(), 판교역_광교역_노선_생성_요청);

        // then
        List<Long> 등록된_지하철역_아이디_목록 = 노선에_등록된_역_아이디_목록을_가져온다(판교역_광교역_노선_생성_응답);
        assertThat(등록된_지하철역_아이디_목록).containsExactly(신사역_아이디, 판교역_아이디, 광교역_아이디);
    }

    @Test
    @DisplayName("존재하지 않는 역이 하행 종점역인 구간을 추가한다.")
    void addNotExistDownStation() {
        // given
        SaveLineSectionRequest 존재하지_않는_역이_하행_종점역인_구간_생성_요청 = SaveLineSectionRequest.builder()
                .upStationId(노선의_하행_종점역_아이디를_찾는다(신분당선))
                .downStationId(0L)
                .distance(8)
                .build();

        // when & then
        assertThatThrownBy(() -> lineService.saveLineSection(신분당선.getId(), 존재하지_않는_역이_하행_종점역인_구간_생성_요청))
                .isInstanceOf(NotEntityFoundException.class)
                .hasMessageContaining(ErrorCode.NOT_EXIST_STATION.getMessage());
    }

    @Test
    @DisplayName("역 사이에 기존 역 사이 길이보다 크거나 같은 노선을 등록한다.")
    void addInvalidDistanceLineSection() {
        // given
        SaveLineSectionRequest 신사역_강남역_노선_생성_요청 = SaveLineSectionRequest.builder()
                .upStationId(신사역_아이디)
                .downStationId(강남역_아이디)
                .distance(23)
                .build();

        // when & then
        assertThatThrownBy(() -> lineService.saveLineSection(신분당선.getId(), 신사역_강남역_노선_생성_요청))
                .isInstanceOf(InvalidLineSectionException.class)
                .hasMessageContaining(ErrorCode.INVALID_DISTANCE.getMessage());
    }

    @Test
    @DisplayName("이미 등록되어 있는 노선을 등록한다.")
    void addAlreadyRegisteredLineSection() {
        // given
        SaveLineSectionRequest 신사역_판교역_노선_생성_요청 = SaveLineSectionRequest.builder()
                .upStationId(신사역_아이디)
                .downStationId(판교역_아이디)
                .distance(24)
                .build();

        // when & then
        assertThatThrownBy(() -> lineService.saveLineSection(신분당선.getId(), 신사역_판교역_노선_생성_요청))
                .isInstanceOf(InvalidLineSectionException.class)
                .hasMessageContaining(ErrorCode.ALREADY_REGISTERED_SECTION.getMessage());
    }

    @Test
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않은 노선을 등록한다.")
    void addLineSectionWithUnregisteredStation() {
        // given
        SaveLineSectionRequest 강남역_광교역_노선_생성_요청 = SaveLineSectionRequest.builder()
                .upStationId(강남역_아이디)
                .downStationId(광교역_아이디)
                .distance(13)
                .build();

        // when & then
        assertThatThrownBy(() -> lineService.saveLineSection(신분당선.getId(), 강남역_광교역_노선_생성_요청))
                .isInstanceOf(InvalidLineSectionException.class)
                .hasMessageContaining(ErrorCode.UNREGISTERED_STATION.getMessage());
    }

    @Test
    @DisplayName("노선의 상행 종점역을 삭제한다.")
    void deleteUpStation() {
        // given
        SaveLineSectionRequest 광교역이_하행_종점역인_구간_생성_요청 = SaveLineSectionRequest.builder()
                .upStationId(노선의_하행_종점역_아이디를_찾는다(신분당선))
                .downStationId(광교역_아이디)
                .distance(13)
                .build();

        LineResponse 강남역_광교역_노선_생성_응답 = lineService.saveLineSection(신분당선.getId(), 광교역이_하행_종점역인_구간_생성_요청);

        // when
        Long 노선의_상행_종점역_아이디 = 노선의_상행_종점역_아이디를_찾는다(강남역_광교역_노선_생성_응답);
        lineService.deleteLineSectionByStationId(신분당선.getId(), 노선의_상행_종점역_아이디);

        // then
        LineResponse findLineResponseDto = lineService.findLineById(강남역_광교역_노선_생성_응답.getId());
        List<Long> 노선에_등록된_역_아이디_목록 = 노선에_등록된_역_아이디_목록을_가져온다(findLineResponseDto);
        assertThat(노선에_등록된_역_아이디_목록).doesNotContain(노선의_상행_종점역_아이디);
    }

    @Test
    @DisplayName("노선의 중간역을 삭제한다.")
    void deleteMiddleStation() {
        // given
        SaveLineSectionRequest 광교역이_하행_종점역인_구간_생성_요청 = SaveLineSectionRequest.builder()
                .upStationId(노선의_하행_종점역_아이디를_찾는다(신분당선))
                .downStationId(광교역_아이디)
                .distance(13)
                .build();

        LineResponse 광교역이_하행_종점역인_구간_생성_응답 =
                lineService.saveLineSection(신분당선.getId(), 광교역이_하행_종점역인_구간_생성_요청);

        // when
        Long 노선의_중간역_아이디 = 광교역이_하행_종점역인_구간_생성_요청.getUpStationId();
        lineService.deleteLineSectionByStationId(신분당선.getId(), 노선의_중간역_아이디);

        // then
        LineResponse findLineResponseDto = lineService.findLineById(광교역이_하행_종점역인_구간_생성_응답.getId());
        List<Long> 노선에_등록된_역_아이디_목록 = 노선에_등록된_역_아이디_목록을_가져온다(findLineResponseDto);
        assertThat(노선에_등록된_역_아이디_목록).doesNotContain(노선의_중간역_아이디);
    }

    @Test
    @DisplayName("노선의 하행 종점역을 삭제한다.")
    void deleteDownStation() {
        // given
        SaveLineSectionRequest 광교역이_하행_종점역인_구간_생성_요청 = SaveLineSectionRequest.builder()
                .upStationId(노선의_하행_종점역_아이디를_찾는다(신분당선))
                .downStationId(광교역_아이디)
                .distance(13)
                .build();

        LineResponse saveLineResponseDto = lineService.saveLineSection(신분당선.getId(), 광교역이_하행_종점역인_구간_생성_요청);

        // when
        Long 광교역이_하행_종점역인_구간_생성_응답 = 노선의_하행_종점역_아이디를_찾는다(saveLineResponseDto);
        lineService.deleteLineSectionByStationId(신분당선.getId(), 광교역이_하행_종점역인_구간_생성_응답);

        // then
        LineResponse 신분당선_조회_응답 = lineService.findLineById(신분당선.getId());
        List<Long> 노선에_등록된_역_아이디_목록 = 노선에_등록된_역_아이디_목록을_가져온다(신분당선_조회_응답);
        assertThat(노선에_등록된_역_아이디_목록).doesNotContain(광교역이_하행_종점역인_구간_생성_응답);
    }

    @Test
    @DisplayName("등록되어 있지 않은 구간을 삭제한다.")
    void deleteNotExistSection() {
        // when & then
        assertThatThrownBy(() -> lineService.deleteLineSectionByStationId(신분당선.getId(), 강남역_아이디))
                .isInstanceOf(InvalidLineSectionException.class)
                .hasMessageContaining(ErrorCode.UNREGISTERED_STATION.getMessage());
    }

    @Test
    @DisplayName("구간이 1개일 때 삭제한다.")
    void deleteStandaloneSection() {
        // when & then
        assertThatThrownBy(() -> lineService.deleteLineSectionByStationId(신분당선.getId(), 판교역_아이디))
                .isInstanceOf(InvalidLineSectionException.class)
                .hasMessageContaining(ErrorCode.STAND_ALONE_LINE_SECTION.getMessage());
    }

    private List<Long> 노선에_등록된_역_아이디_목록을_가져온다(LineResponse lineResponseDto) {
        return lineResponseDto
                .getStations()
                .stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
    }

    private long 노선의_상행_종점역_아이디를_찾는다(LineResponse lineResponseDto) {
        return lineResponseDto.getStations()
                .get(0)
                .getId();
    }

    private Long 노선의_하행_종점역_아이디를_찾는다(Line line) {
        List<Station> stations = line.getSections().getAllStations();
        int lastIndex = stations.size() - 1;
        return stations
                .get(lastIndex)
                .getId();
    }

    private Long 노선의_하행_종점역_아이디를_찾는다(LineResponse lineResponseDto) {
        List<StationResponse> 노선에_등록된_역_목록 = lineResponseDto.getStations();
        int lastIndex = 노선에_등록된_역_목록.size() - 1;
        return 노선에_등록된_역_목록
                .get(lastIndex)
                .getId();
    }
}
