package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static nextstep.subway.common.constants.ErrorConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
@DisplayName("지하철 구간 서비스 단위 테스트")
public class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    Station 강남역;
    Station 양재역;
    Line 신분당선;
    @BeforeEach
    void setUp() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        강남역 = stationRepository.save(new Station("강남역"));
        양재역 = stationRepository.save(new Station("양재역"));

        신분당선 = lineRepository.save(new Line("신분당선", "RED"));
    }

    @Test
    @DisplayName("지하철 구간 추가")
    void addSection() {
        // when
        // lineService.addSection 호출
        lineService.addSection(신분당선.getId(), new SectionRequest(강남역.getId(), 양재역.getId(), 10));

        // then
        // line.getSections 메서드를 통해 검증
        assertAll(
                () -> assertThat(신분당선.getName()).isEqualTo("신분당선"),
                () -> assertThat(신분당선.getColor()).isEqualTo("RED"),
                () -> assertThat(신분당선.getStations()).extracting("name").containsExactly("강남역", "양재역")
        );
    }

    @Test
    @DisplayName("기존 구간 길이보다 크거나 같은 역 사이 새로운 역을 갖는 구간 추가")
    void addSection_moreThenDistance() {
        // given
        Station 정자역 = stationRepository.save(new Station("정자역"));

        lineService.addSection(신분당선.getId(), new SectionRequest(강남역.getId(), 양재역.getId(), 10));

        // when
        // then
        assertThatThrownBy(() -> lineService.addSection(신분당선.getId(), new SectionRequest(정자역.getId(), 양재역.getId(), 10)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(MORE_THEN_DISTANCE);
    }

    @Test
    @DisplayName("상행역과 하행역 모두 이미 등록된 구간 추가")
    void addSection_alreadyEnrollStation() {
        // given
        lineService.addSection(신분당선.getId(), new SectionRequest(강남역.getId(), 양재역.getId(), 10));

        // when
        // then
        assertThatThrownBy(() -> lineService.addSection(신분당선.getId(), new SectionRequest(강남역.getId(), 양재역.getId(), 10)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ALREADY_ENROLL_STATION);
    }

    @Test
    @DisplayName("상행역과 하행역 둘 중 하나도 등록되지 않은 구간 추가")
    void addSection_notEnrollStation() {
        // given
        Station 정자역 = stationRepository.save(new Station("정자역"));
        Station 판교역 = stationRepository.save(new Station("판교역"));

        lineService.addSection(신분당선.getId(), new SectionRequest(강남역.getId(), 양재역.getId(), 10));

        // when
        // then
        assertThatThrownBy(() -> lineService.addSection(신분당선.getId(), new SectionRequest(정자역.getId(), 판교역.getId(), 6)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(NOT_ENROLL_STATION);
    }

    @Test
    @DisplayName("기존 구간 사이 역을 갖는 구간 추가")
    void addSection_middleStation() {
        // given
        Station 정자역 = stationRepository.save(new Station("정자역"));

        lineService.addSection(신분당선.getId(), new SectionRequest(강남역.getId(), 양재역.getId(), 10));

        // when
        lineService.addSection(신분당선.getId(), new SectionRequest(정자역.getId(), 양재역.getId(), 6));

        // then
        Line line = lineService.findLineById(신분당선.getId());
        List<Station> stations = line.getStations();
        assertThat(stations).extracting("name").containsExactly("강남역", "정자역", "양재역");
    }

    @Test
    @DisplayName("하행 종점역을 상행역으로 갖는 구간 추가")
    void addSection_lastStation() {
        // given
        Station 정자역 = stationRepository.save(new Station("정자역"));

        lineService.addSection(신분당선.getId(), new SectionRequest(강남역.getId(), 양재역.getId(), 10));

        // when
        lineService.addSection(신분당선.getId(), new SectionRequest(양재역.getId(), 정자역.getId(), 10));

        // then
        Line line = lineService.findLineById(신분당선.getId());
        List<Station> stations = line.getStations();
        assertThat(stations).extracting("name").containsExactly("강남역", "양재역", "정자역");
    }

    @Test
    @DisplayName("상행 종점역을 하행역으로 갖는 구간 추가")
    void addSection_frontStation() {
        // given
        Station 판교역 = stationRepository.save(new Station("판교역"));

        lineService.addSection(신분당선.getId(), new SectionRequest(강남역.getId(), 양재역.getId(), 10));

        // when
        lineService.addSection(신분당선.getId(), new SectionRequest(판교역.getId(), 강남역.getId(), 6));

        // then
        Line line = lineService.findLineById(신분당선.getId());
        List<Station> stations = line.getStations();
        assertThat(stations).extracting("name").containsExactly("판교역", "강남역", "양재역");
    }
}
