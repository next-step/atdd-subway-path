package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.ExploreResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.handler.exception.ExploreException;
import nextstep.subway.handler.exception.StationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static nextstep.subway.domain.factory.EntityFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@DisplayName("경로 탐색 비즈니스 로직 테스트")
class PathServiceTest {
    @Autowired
    private PathService pathService;
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    private Line 이호선;
    private Line 삼호선;
    private Line 신분당선;

    private Station 강남역;
    private Station 교대역;
    private Station 서초역;
    private Station 양재역;
    private Station 매봉역;
    private Station 양재시민의숲역;

    @BeforeEach
    void init() {
        강남역 = 역_생성("강남역");
        교대역 = 역_생성("교대역");
        서초역 = 역_생성("서초역");
        양재역 = 역_생성("양재역");
        매봉역 = 역_생성("매봉역");
        양재시민의숲역 = 역_생성("양재시민의숲역");

        이호선 = createLine("2호선", "green", 강남역, 교대역, 6);
        이호선.addSection(createSection(이호선, 교대역, 서초역, 4));
        lineRepository.save(이호선);

        삼호선 = createLine("3호선", "orange", 교대역, 매봉역, 11);
        삼호선.addSection(createSection(삼호선, 교대역, 양재역, 7));
        lineRepository.save(삼호선);

        신분당선 = createLine("신분당선", "red", 강남역, 양재시민의숲역, 16);
        신분당선.addSection(createSection(이호선, 양재역, 양재시민의숲역, 6));
        lineRepository.save(신분당선);
    }

    private Station 역_생성(String stationName) {
        Station station = createStation(stationName);
        stationRepository.save(station);

        return station;
    }

    @Test
    @DisplayName("두 역에 대해 경로 탐색을 처리한다.")
    void explore() {
        // when
        ExploreResponse response = pathService.explore(강남역.getId(), 매봉역.getId());

        // then
        assertThat(response.getStations()).hasSize(3);
        assertThat(response.getStations().get(0).getName()).isEqualTo("강남역");
        assertThat(response.getStations().get(2).getName()).isEqualTo("매봉역");
        assertThat(response.getDistance()).isEqualTo(14);
    }

    @Test
    @DisplayName("두 역이 이어져 있지 않은 경우, 탐색 처리중에 예외를 발생한다.")
    void validateExplore() {
        // given
        Station 용산역 = 역_생성("용산역");
        Station 운정역 = 역_생성("운정역");
        Line 경의중앙선 = createLine("경의중앙선", "blue", 용산역, 운정역, 35);
        lineRepository.save(경의중앙선);

        // when/then
        assertThatThrownBy(() -> pathService.explore(강남역.getId(), 운정역.getId()))
                .isInstanceOf(ExploreException.class);
    }

    @Test
    @DisplayName("두 역이 같은 경우 탐색을 하지 못한다.")
    void validateStations() {
        // when/then
        assertThatThrownBy(() -> pathService.explore(강남역.getId(), 강남역.getId()))
                .isInstanceOf(ExploreException.class);
    }

    @Test
    @DisplayName("존재하지 않는 역에 대해서는 탐색을 하지 못한다.")
    void validateStations2() {
        // when/then
        assertThatThrownBy(() -> pathService.explore(강남역.getId(), 100L))
                .isInstanceOf(StationException.class);
    }
}