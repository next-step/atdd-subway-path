package nextstep.subway.unit;

import nextstep.config.BaseUnitTest;
import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.LineRepository;
import nextstep.subway.domain.line.section.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.exception.SectionExceptionCode;
import nextstep.subway.exception.StationExceptionCode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DisplayName("구간 서비스 단위 테스트 without Mock")
public class LineServiceTest extends BaseUnitTest {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    Station 신대방역;
    Station 신림역;
    Station 봉천역;
    Line 노선;

    @BeforeEach
    void setup() {
        신대방역 = stationRepository.save(new Station("신대방역"));
        신림역 = stationRepository.save(new Station("신림역"));
        봉천역 = stationRepository.save(new Station("봉천역"));
        노선 = lineRepository.save(new Line("2호선", "#00FF00"));
        노선.addSection(new Section(노선, 신대방역, 신림역, 10));
        노선.addSection(new Section(노선, 신림역, 봉천역, 10));
    }

    @Test
    @DisplayName("지하철 노선 구간 추가")
    void addSection() {
        // given
        Station 서울대입구역 = stationRepository.save(new Station("서울대입구역"));
        SectionRequest request = SectionRequest.builder()
            .upStationId(봉천역.getId())
            .downStationId(서울대입구역.getId())
            .distance(5)
            .build();

        // when
        lineService.addNewSection(노선.getId(), request);

        // then
        Assertions.assertThat(노선.getStations())
            .asList()
            .containsExactly(신대방역, 신림역, 봉천역, 서울대입구역);
    }

    @Test
    @DisplayName("새로운 구간의 상,하행역이 모두 기존 노선에 있으면 노선을 등록 할 수 없다.")
    void 노선등록_실패_1() {
        SectionRequest request = SectionRequest.builder()
            .upStationId(신대방역.getId())
            .downStationId(봉천역.getId())
            .distance(10)
            .build();

        // when && then
        assertThatThrowsSubwayException (
            () -> lineService.addNewSection(노선.getId(), request),
            StationExceptionCode.STATION_ALREADY_EXIST
        );
    }

    @Test
    @DisplayName("새로운 구간의 상,하행역이 기존 존재하는 구간과 동일하면 노선을 등록 할 수 없다.")
    void 노선등록_실패_2() {

        // given
        SectionRequest request = SectionRequest.builder()
            .upStationId(봉천역.getId())
            .downStationId(신림역.getId())
            .distance(10)
            .build();

        // when && then
        assertThatThrowsSubwayException (
            () -> lineService.addNewSection(노선.getId(), request),
            SectionExceptionCode.SECTION_ALREADY_EXIST
        );
    }

    @Test
    @DisplayName("새로운 구간의 상,하행역이 기존 존재하는 구간과 동일하면 노선을 등록 할 수 없다. - 역방향")
    void 노선등록_실패_3() {
        // given
        SectionRequest request = SectionRequest.builder()
            .upStationId(봉천역.getId())
            .downStationId(신림역.getId())
            .distance(10)
            .build();

        // when && then
        assertThatThrowsSubwayException (
            () -> lineService.addNewSection(노선.getId(), request),
            SectionExceptionCode.SECTION_ALREADY_EXIST
        );
    }

    @Test
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없다.")
    void 노선등록_실패_4() {
        // given
        Station 신림_봉천_사이역 = stationRepository.save(new Station("신림_봉천_사이역"));
        SectionRequest request = SectionRequest.builder()
            .upStationId(신림_봉천_사이역.getId())
            .downStationId(봉천역.getId())
            .distance(20)
            .build();

        // when && then
        assertThatThrowsSubwayException (
            () -> lineService.addNewSection(노선.getId(), request),
            SectionExceptionCode.EXCEED_MAXIMUM_DISTANCE
        );
    }
}
