package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.exception.SubwayRuntimeException;
import nextstep.subway.exception.message.SubwayErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
@DisplayName("지하철 구간 서비스 단위 테스트")
class LineServiceTest {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    private Station 강남역;

    private Station 양재역;

    private Station 판교역;

    private Line 신분당선;


    @BeforeEach()
    void setup() {
        강남역 = stationRepository.save(new Station("강남역"));
        양재역 = stationRepository.save(new Station("양재역"));
        판교역 = stationRepository.save(new Station("판교역"));

        신분당선 = lineRepository.save(new Line("신분당선", "bg-red-600"));
        lineService.addSection(신분당선.getId(), new SectionRequest(강남역.getId(), 양재역.getId(), 10));
    }

    @DisplayName("지하철 구간 추가 [기존 구간 A-C 에 신규 구간 A-B 를 추가하는 경우]")
    @Test
    void addSection() {
        // when
        lineService.addSection(신분당선.getId(), new SectionRequest(강남역.getId(), 판교역.getId(), 5));

        // then
        assertAll(
                () -> assertThat(신분당선.getName()).isEqualTo("신분당선"),
                () -> assertThat(신분당선.getColor()).isEqualTo("bg-red-600"),
                () -> assertThat(신분당선.getStations()).extracting("name").containsExactly("강남역", "판교역", "양재역")
        );
    }

    /**
     * When -> 지하철 구간등록시
     * Then -> 기존 구간에 새로운 구간을 추가한다.
     */
    @DisplayName("지하철 구간 예외 테스트 [1]")
    @Test
    void addSectionException() {
        // When & Then
        assertThatThrownBy(() -> 신분당선.addSection(강남역, 양재역, 10))
                .isInstanceOf(SubwayRuntimeException.class)
                .hasMessage(SubwayErrorCode.DUPLICATE_SECTION.getMessage());
    }

    /**
     * When -> 기존 구간 A-C 에 신규 구간 A-B 를 추가하는 경우 거리값이 기존 구간의 거리값보다 크거나 같은 경우
     * Then -> 예외처리
     */
    @DisplayName("지하철 구간 예외 테스트 [2]")
    @Test
    void addSectionException2() {
        // When & Then
        assertThatThrownBy(() -> 신분당선.addSection(강남역, 판교역, 10))
                .isInstanceOf(SubwayRuntimeException.class)
                .hasMessage(SubwayErrorCode.INVALID_SECTION_DISTANCE.getMessage());
    }
}
